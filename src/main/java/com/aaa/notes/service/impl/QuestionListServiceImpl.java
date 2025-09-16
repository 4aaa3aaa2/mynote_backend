package com.aaa.notes.service.impl;


import com.aaa.notes.mapper.QuestionListItemMapper;
import com.aaa.notes.mapper.QuestionListMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.dto.questionList.CreateQuestionListBody;
import com.aaa.notes.model.dto.questionList.UpdateQuestionListBody;
import com.aaa.notes.model.entity.Question;
import com.aaa.notes.model.entity.QuestionList;
import com.aaa.notes.model.vo.questionList.CreateQuestionListVO;
import com.aaa.notes.service.QuestionListService;
import com.aaa.notes.utils.ApiResponseUtil;
import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionListServiceImpl implements  QuestionListService{

    @Autowired
    private QuestionListMapper questionListMapper;

    @Autowired
    private QuestionListItemMapper questionListItemMapper;

    @Override
    public ApiResponse<QuestionList> getQuestionList(Integer questionListId) {
        return ApiResponseUtil.success("got question list", questionListMapper.findById(questionListId));
    }

    @Override
    public ApiResponse<List<QuestionList>> getQuestionLists() {
        return ApiResponseUtil.success("got all the question list", questionListMapper.findAll());
    }

    @Override
    public  ApiResponse<CreateQuestionListVO> createQuestionList(CreateQuestionListBody body){
        QuestionList questionList = new QuestionList();
        BeanUtils.copyProperties(body,questionList);

        try{
            questionListMapper.insert(questionList);
            CreateQuestionListVO questionListVO = new CreateQuestionListVO();
            questionListVO.setQuestionListId(questionList.getQuestionListId());
            return ApiResponseUtil.success("created question list", questionListVO);
        }catch (Exception e){
            return  ApiResponseUtil.error("create question list failed");
        }
    }


    @Override
    public ApiResponse<EmptyVO> deleteQuestionList(Integer questionListId){
        // 删除题单，还需要删除题单对应的题单项目
        QuestionList questionList = questionListMapper.findById(questionListId);

        if (questionList == null) {
            return ApiResponseUtil.error("question list doesnt exist");
        }

        try {
            questionListMapper.deleteById(questionListId);
            // 删除题单对应的所有题单项
            questionListItemMapper.deleteByQuestionListId(questionListId);
            return ApiResponseUtil.success("delete success");
        } catch (Exception e) {
            return ApiResponseUtil.error("delete failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> updateQuestionList(Integer questionListId, UpdateQuestionListBody body){
        QuestionList questionList = new QuestionList();
        BeanUtils.copyProperties(body, questionList);
        questionList.setQuestionListId(questionListId);

        System.out.println(questionList);

        try {
            questionListMapper.update(questionList);
            return ApiResponseUtil.success("update success");
        } catch (Exception e) {
            return ApiResponseUtil.error("update failed");
        }
    }

}
