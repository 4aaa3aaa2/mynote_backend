package com.aaa.notes.service.impl;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.mapper.QuestionMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.base.Pagination;
import com.aaa.notes.model.dto.note.CreateNoteRequest;
import com.aaa.notes.model.dto.note.NoteQueryParams;
import com.aaa.notes.model.dto.note.UpdateNoteRequest;
import com.aaa.notes.model.entity.Note;
import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.model.entity.Question;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.model.vo.category.CategoryVO;
import com.aaa.notes.model.vo.note.*;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.*;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.MarkdownUtil;
import com.aaa.notes.utils.PaginationUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class NoteServiceImpl implements NoteService{
    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NoteLikeService noteLikeService;

    @Autowired
    private CollectionNoteService collectionNoteService;

    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public ApiResponse<List<NoteVO>> getNotes(NoteQueryParams params){
        int offset = PaginationUtils.calculateOffset(params.getPage(),params.getPageSize());
        int total = noteMapper.countNotes(params);

        Pagination pagination = new Pagination(params.getPage(),params.getPageSize(), total);
        List<Note> notes = noteMapper.findByQueryParams(params,offset, pagination.getPageSize());

        List<Integer> questionIds = notes.stream().map(Note::getQuestionId).distinct().toList();
        List<Long>  authorIds = notes.stream().map(Note::getAuthorId).distinct().toList();
        List<Integer> noteIds = notes.stream().map(Note::getNoteId).toList();

        Map<Long, User> userMapByIds = userService.getUserMapByIds(authorIds);
        Map<Integer, Question> questionMapByIds = questionService.getQuestionMapByIds(questionIds);

        Set<Integer> userLikedNoteIds;
        Set<Integer> userCollectedNoteIds;

        if(requestScopeData.getUserId() != null ){
            Long currentUserId = requestScopeData.getUserId();
            userLikedNoteIds = noteLikeService.findUserLikedNoteIds(currentUserId, noteIds);
            userCollectedNoteIds = collectionNoteService.findUserCollectedNoteIds(currentUserId, noteIds);
        }else{
            userLikedNoteIds = Collections.emptySet();
            userCollectedNoteIds = Collections.emptySet();
        }

        try{
            List<NoteVO> noteVOS = notes.stream().map(note->{
                NoteVO noteVO = new NoteVO();
                BeanUtils.copyProperties(note, noteVO);

                User author = userMapByIds.get(note.getAuthorId());
                if (author != null) {
                    NoteVO.SimpleAuthorVO authorVO = new NoteVO.SimpleAuthorVO();
                    BeanUtils.copyProperties(author, authorVO);
                    noteVO.setAuthor(authorVO);
                }

                Question question = questionMapByIds.get(note.getQuestionId());
                if (question != null) {
                    NoteVO.SimpleQuestionVO questionVO = new NoteVO.SimpleQuestionVO();
                    BeanUtils.copyProperties(question, questionVO);
                    noteVO.setQuestion(questionVO);
                }

                NoteVO.UserActionsVO userActionsVO = new NoteVO.UserActionsVO();
                if (userLikedNoteIds != null && userLikedNoteIds.contains(note.getNoteId())) {
                    userActionsVO.setIsLiked(true);
                }
                if (userCollectedNoteIds != null && userCollectedNoteIds.contains(note.getNoteId())) {
                    userActionsVO.setIsCollected(true);
                }

                // 处理笔记内容折叠内容
                if (MarkdownUtil.needCollapsed(note.getContent())) {
                    noteVO.setNeedCollapsed(true);
                    noteVO.setDisplayContent(MarkdownUtil.extractIntroduction(note.getContent()));
                } else {
                    noteVO.setNeedCollapsed(false);
                }

                noteVO.setUserActions(userActionsVO);
                return noteVO;
            }).toList();
            return ApiResponseUtil.success("got notes", noteVOS, pagination);
        } catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
            return ApiResponseUtil.error("get notes failed");
        }
    }

    @Override
    @NeedLogin
    public ApiResponse<CreateNoteVO> createNote(CreateNoteRequest request){
        Long userId = requestScopeData.getUserId();
        Integer questionId = request.getQuestionId();

        Question question = questionService.findById(questionId);

        if(question == null){
            return ApiResponseUtil.error("the corresponding question doesnt exist");
        }

        Note note = new Note();
        BeanUtils.copyProperties(request, note);
        note.setAuthorId(userId);

        try {
            noteMapper.insert(note);
            CreateNoteVO createNoteVO = new CreateNoteVO();
            createNoteVO.setNoteId(note.getNoteId());
            return ApiResponseUtil.success("created note", createNoteVO);
        }catch (Exception e){
            return ApiResponseUtil.error("filed to create note");
        }
    }


    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> updateNote(Integer noteId, UpdateNoteRequest request){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);

        if(note == null){
            return  ApiResponseUtil.error("note doesnt exist");
        }

        if (!Objects.equals(userId, note.getAuthorId())){
            return  ApiResponseUtil.error("not authorized to update");
        }

        try{
            note.setContent((request.getContent()));
            noteMapper.update(note);
            return  ApiResponseUtil.success("updated");
        }
        catch (Exception e) {
            return ApiResponseUtil.error("update failed");
        }
    }


    @Override
    @NeedLogin
    public ApiResponse<EmptyVO> deleteNote(Integer noteId){
        Long userId = requestScopeData.getUserId();
        Note note = noteMapper.findById(noteId);

        if(note == null){
            return  ApiResponseUtil.error("note doesnt exist");
        }

        if (!Objects.equals(userId, note.getAuthorId())){
            return  ApiResponseUtil.error("not authorized to delete");
        }
        try{
            noteMapper.deleteById(noteId);
            return  ApiResponseUtil.success("deleted");
        }
        catch (Exception e) {
            return ApiResponseUtil.error("delete failed");
        }

    }

    @Override
    @NeedLogin
    public ApiResponse<DownloadNoteVO> downloadNote(){
        Long userId = requestScopeData.getUserId();
        List<Note> userNotes = noteMapper.findByAuthorId(userId);
        Map<Integer, Note>  questionNoteMap  = userNotes.stream().collect(Collectors.toMap(Note::getQuestionId, note -> note));

        if(userNotes.isEmpty()){
            return ApiResponseUtil.error("no notes");
        }

        List<CategoryVO> categoryTree = categoryService.buildCategoryTree();

        StringBuilder markdownContent = new StringBuilder();

        List<Integer> questionIds = userNotes.stream().map(Note::getQuestionId).toList();

        List<Question> questions = questionMapper.findByIdBatch(questionIds);
        for (CategoryVO categoryVO: categoryTree){
            boolean hasTopLevelToc = false;
            if (categoryVO.getChildren().isEmpty()){
                continue;
            }
            for(CategoryVO.ChildrenCategoryVO childrenCategoryVO : categoryVO.getChildren()){
                boolean hasSubLevelToc = false;
                Integer categoryId = childrenCategoryVO.getCategoryId();

                List<Question> categoryQuestionList = questions.stream().filter(question ->question.getCategoryId().equals(categoryId)).toList();

                if(categoryQuestionList.isEmpty()){
                    continue;
                }
                for(Question question: categoryQuestionList){
                    if(!hasTopLevelToc){
                        markdownContent.append("# ").append(categoryVO.getName()).append("\n");
                        hasTopLevelToc = true;
                    }
                    if (!hasSubLevelToc) {  // 设置二级标题
                        markdownContent.append("## ").append(childrenCategoryVO.getName()).append("\n");
                        hasSubLevelToc = true;
                    }


                    markdownContent.append("### [")
                            .append(question.getTitle())
                            .append("]")
                            .append("(https://coding_notes.com/questions/")
                            .append(question.getQuestionId())
                            .append(")\n");
                    Note note = questionNoteMap.get(question.getQuestionId());

                    markdownContent.append(note.getContent()).append("\n");

                }
            }
        }
        DownloadNoteVO downloadNoteVO = new DownloadNoteVO();
        downloadNoteVO.setMarkdown(markdownContent.toString());

        return ApiResponseUtil.success("download note success", downloadNoteVO);
    }

    @Override
    public ApiResponse<List<NoteRankListItem>> submitNoteRank(){
        return ApiResponseUtil.success("got notes ranking", noteMapper.submitNoteRank());
    }

    @Override
    @NeedLogin
    public  ApiResponse<List<NoteHeatMapItem>> submitNoteHeatMap(){
        Long userId = requestScopeData.getUserId();
        return ApiResponseUtil.success("got notes heat map", noteMapper.submitNoteHeatMap(userId));
    }

    @Override
    @NeedLogin
    public ApiResponse<Top3Count> submitNoteTop3Count(){
        Long userId = requestScopeData.getUserId();

        Top3Count top3Count = noteMapper.submitNoteTop3Count(userId);

        return ApiResponseUtil.success("got notes top3", top3Count);
    }
}
