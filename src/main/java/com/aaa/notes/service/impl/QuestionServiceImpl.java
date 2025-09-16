package com.aaa.notes.service.impl;


import com.aaa.notes.mapper.CategoryMapper;
import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.mapper.QuestionMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.base.PageVO;
import com.aaa.notes.model.base.Pagination;
import com.aaa.notes.model.dto.question.*;

import com.aaa.notes.model.dto.questionList.CreateQuestionListBody;
import com.aaa.notes.model.dto.questionList.UpdateQuestionListBody;
import com.aaa.notes.model.entity.*;
import com.aaa.notes.model.vo.question.CreateQuestionVO;
import com.aaa.notes.model.vo.question.QuestionNoteVO;
import com.aaa.notes.model.vo.question.QuestionUserVO;
import com.aaa.notes.model.vo.question.QuestionVO;
import com.aaa.notes.model.vo.questionList.CreateQuestionListVO;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.CategoryService;
import com.aaa.notes.service.QuestionListService;
import com.aaa.notes.service.QuestionService;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.MarkdownAST;
import com.aaa.notes.utils.PaginationUtils;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Question findById(Integer questionId){
        return questionMapper.findById(questionId);
    }



    // -------------------------------
    // 正则：匹配形如：
    //   (考点: XXX) 或 （考点：XXX）
    private static final Pattern POINT_PATTERN =
            Pattern.compile("[（(]考点\\s*[：:]\\s*(.*?)[)）]");
    // 正则：匹配形如：
    //   【简单】、【中等】、【困难】
    private static final Pattern LEVEL_PATTERN =
            Pattern.compile("【(.*?)】");
    // -------------------------------


    @Override
    public Map<Integer, Question> getQuestionMapByIds(List<Integer> questionIds){
        if (questionIds.isEmpty()){
            return Collections.emptyMap();
        }
        List<Question> questions = questionMapper.findByIdBatch(questionIds);
        return questions.stream().collect(Collectors.toMap(Question::getQuestionId,question -> question));
    }

    @Override
    public ApiResponse<List<QuestionVO>> getQuestions(QuestionQueryParam queryParams){
        int offset = PaginationUtils.calculateOffset(queryParams.getPage(),queryParams.getPageSize());
        int total = questionMapper.countByQueryParam(queryParams);

        Pagination pagination = new Pagination(queryParams.getPage(),queryParams.getPageSize(),total);
        List<Question> questions = questionMapper.findByQueryParam(queryParams, offset, queryParams.getPageSize());

        List<QuestionVO> questionVOs = questions.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).toList();
        return ApiResponseUtil.success("got question list", questionVOs,pagination);
    }

    @Override
    public ApiResponse<CreateQuestionVO> createQuestion(CreateQuestionBody createQuestionBody){
        Category category = categoryMapper.findById(createQuestionBody.getCategoryId());
        if (category == null){
            return  ApiResponseUtil.error("invalid Id");
        }

        Question question = new Question();
        BeanUtils.copyProperties(createQuestionBody, question);
        try {
            questionMapper.insert(question);
            CreateQuestionVO createQuestionVO = new CreateQuestionVO();
            createQuestionVO.setQuestionId(question.getQuestionId());
            return ApiResponseUtil.success("create question succeed", createQuestionVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("create question fail");
        }
    }

    @Override
    public ApiResponse<EmptyVO> updateQuestion(Integer questionId, UpdateQuestionBody updateQuestionBody){
        Question question = new Question();
        BeanUtils.copyProperties(updateQuestionBody,question);
        question.setQuestionId(questionId);
        try{
            questionMapper.update(question);
            return ApiResponseUtil.success("update success");
        } catch (Exception e) {
            return ApiResponseUtil.error("update fail");
        }
    }

    @Override
    public ApiResponse<EmptyVO> deleteQuestion(Integer questionId){
        if (questionMapper.deleteById(questionId) > 0) {
            return ApiResponseUtil.success("delete question succeed");
        } else {
            return ApiResponseUtil.error("delete question fail");
        }
    }


    @Override
    public ApiResponse<List<QuestionUserVO>> userGetQuestions(QuestionQueryParam queryParams){
        int offset = PaginationUtils.calculateOffset(queryParams.getPage(),queryParams.getPageSize());
        int total = questionMapper.countByQueryParam(queryParams);
        Pagination pagination = new Pagination(queryParams.getPage(),queryParams.getPageSize(),total);

        List<Question> questions = questionMapper.findByQueryParam(queryParams,offset,queryParams.getPageSize());
        List<Integer> questionIds = questions.stream().map(Question::getQuestionId).toList();

        Set<Integer> userFinishedQuestionIds;

        if(requestScopeData.isLogin() && requestScopeData.getUserId()!=null){
            userFinishedQuestionIds = noteMapper.filterFinishedQuestionIdsByUser(requestScopeData.getUserId(),questionIds);
        }else {
            userFinishedQuestionIds = Collections.emptySet();
        }

        List<QuestionUserVO> questionUserVOS = questions.stream().map(question -> {
            QuestionUserVO questionUserVO = new QuestionUserVO();
            QuestionUserVO.UserQuestionStatus userQuestionStatus = new QuestionUserVO.UserQuestionStatus();

            // 判断用户是否完成该道题目
            if (userFinishedQuestionIds != null && userFinishedQuestionIds.contains(question.getQuestionId())) {
                userQuestionStatus.setFinished(true);  // 用户完成了该道题目
            }

            BeanUtils.copyProperties(question, questionUserVO);

            // 设置用户完成状态
            questionUserVO.setUserQuestionStatus(userQuestionStatus);
            return questionUserVO;
        }).toList();
        return ApiResponseUtil.success("got question list", questionUserVOS, pagination);
    }

    @Override
    public ApiResponse<QuestionNoteVO> userGetQuestion(Integer questionId){
        // 验证 question 是否存在
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            return ApiResponseUtil.error("questionId 非法");
        }

        QuestionNoteVO questionNoteVO = new QuestionNoteVO();
        QuestionNoteVO.UserNote userNote = new QuestionNoteVO.UserNote();

        // 如果是登录状态，则查询出当前用户笔记
        if (requestScopeData.isLogin() && requestScopeData.getUserId() != null) {
            Note note = noteMapper.findByAuthorIdAndQuestionId(requestScopeData.getUserId(), questionId);
            if (note != null) {
                userNote.setFinished(true);
                BeanUtils.copyProperties(note, userNote);
            }
        }

        BeanUtils.copyProperties(question, questionNoteVO);
        questionNoteVO.setUserNote(userNote);

        // 增加问题的点击量
        // TODO: 有待优化
        questionMapper.incrementViewCount(questionId);

        return ApiResponseUtil.success("got question", questionNoteVO);
    }

    @Override
    public ApiResponse<List<QuestionVO>> searchQuestions(SearchQuestionBody body){
        String keyword = body.getKeyword();

        // TODO: 简单实现搜索问题功能，后续需要优化
        List<Question> questionList = questionMapper.findByKeyword(keyword);

        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            return questionVO;
        }).toList();

        return ApiResponseUtil.success("search succeed", questionVOList);
    }



    /**
     * 批量创建问题
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<EmptyVO> createQuestionBatch(CreateQuestionBatchBody createQuestionBatchBody) {
        // 获取 markdown 文本
        String markdown = createQuestionBatchBody.getMarkdown();

        MarkdownAST markdownAST = new MarkdownAST(markdown);
        Document document = markdownAST.getMarkdownAST();

        // 遍历节点
        for (Node child = document.getFirstChild(); child != null; child = child.getNext()) {
            if (child instanceof Heading parentHeading) {  // 查询到 heading 节点
                if (parentHeading.getLevel() == 1) {  // 如果是一级标题
                    // 查看是否存在对应的一级标题分类
                    String parentCategoryName = markdownAST.getHeadingText(parentHeading);
                    Category parentCategory = categoryService.findOrCreateCategory(parentCategoryName);

                    Node childCategory = parentHeading.getNext();

                    for (; childCategory != null; childCategory = childCategory.getNext()) {
                        // 碰到下一个父分类 (Heading level=1)，跳出当前循环
                        if (childCategory instanceof Heading nextParent) {
                            if (nextParent.getLevel() == 1) {
                                break;
                            }
                        }

                        // 如果是二级标题（子分类 Heading level = 2）
                        if (childCategory instanceof Heading subHeading) {
                            if (subHeading.getLevel() == 2) {
                                String subCategoryName = markdownAST.getHeadingText(subHeading);

                                Category subCategory = categoryService.findOrCreateCategory(
                                        subCategoryName,
                                        parentCategory.getCategoryId()
                                );

                                Node listBlockNode = subHeading.getNext();
                                if (!(listBlockNode instanceof BulletList) &&
                                        !(listBlockNode instanceof OrderedList)) {
                                    // 没有找到列表，则抛错也好，或者直接跳过
                                    continue;
                                }

                                for (Node listItem = listBlockNode.getFirstChild();
                                     listItem != null;
                                     listItem = listItem.getNext()) {

                                    if (listItem instanceof ListItem listItem2) {
                                        String listItemText = markdownAST.getListItemText(listItem2);

                                        // 解析考点
                                        String examPoint = "";
                                        Matcher matchPoint = POINT_PATTERN.matcher(listItemText);
                                        if (!matchPoint.find()) {
                                            throw new RuntimeException("解析考点失败");
                                        }
                                        examPoint = matchPoint.group(1);

                                        // 解析难度
                                        String difficultyStr = "";
                                        Matcher matchLevel = LEVEL_PATTERN.matcher(listItemText);
                                        if (!matchLevel.find()) {
                                            throw new RuntimeException("解析难度失败");
                                        }
                                        difficultyStr = matchLevel.group(1);

                                        // 解析题目
                                        String title = listItemText
                                                .replaceAll(POINT_PATTERN.pattern(), "")
                                                .replaceAll(LEVEL_PATTERN.pattern(), "")
                                                .trim();

                                        // 查看题目是否存在
                                        Question question = questionMapper.findByTitle(title);

                                        if (question != null) {
                                            throw new RuntimeException("题目已存在");
                                        }

                                        // 难度映射表
                                        Map<String, Integer> difficultyMap = new HashMap<>();
                                        difficultyMap.put("简单", 1);
                                        difficultyMap.put("中等", 2);
                                        difficultyMap.put("困难", 3);

                                        Integer difficultyVal = difficultyMap.get(difficultyStr);
                                        if (difficultyVal == null) {
                                            throw new RuntimeException("难度解析失败");
                                        }

                                        // 创建问题
                                        Question addQuestion = new Question();

                                        addQuestion.setTitle(title);
                                        addQuestion.setCategoryId(subCategory.getCategoryId());
                                        addQuestion.setExamPoint(examPoint);
                                        addQuestion.setDifficulty(difficultyVal);

                                        try {
                                            questionMapper.insert(addQuestion);
                                        } catch (Exception e) {
                                            throw new RuntimeException("创建问题失败: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ApiResponseUtil.success("创建问题成功");
    }

}
