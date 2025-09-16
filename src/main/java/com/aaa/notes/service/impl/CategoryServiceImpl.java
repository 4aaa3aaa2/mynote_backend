package com.aaa.notes.service.impl;


import com.aaa.notes.mapper.CategoryMapper;
import com.aaa.notes.mapper.QuestionMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.dto.category.CreateCategoryBody;
import com.aaa.notes.model.dto.category.UpdateCategoryBody;
import com.aaa.notes.model.entity.Category;
import com.aaa.notes.model.vo.category.CategoryVO;
import com.aaa.notes.model.vo.category.CreateCategoryVO;
import com.aaa.notes.service.CategoryService;
import com.aaa.notes.utils.ApiResponseUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements  CategoryService{

    @Autowired
    private  CategoryMapper categoryMapper;
    @Autowired
    private QuestionMapper QuestionMapper;

    public List<CategoryVO> buildCategoryTree(){
        //获取所有分类
        List<Category> categories  = categoryMapper.categoryList();

        Map<Integer,CategoryVO> categoryMap = new HashMap<>();

        categories.forEach(category -> {
            if(category.getParentCategoryId()==0){
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVO.setChildren(new ArrayList<>());
                categoryMap.put(category.getCategoryId(), categoryVO);
            } else {
                // 子分类
                CategoryVO.ChildrenCategoryVO childrenCategoryVO = new CategoryVO.ChildrenCategoryVO();
                BeanUtils.copyProperties(category,childrenCategoryVO);
                CategoryVO parentCategory = categoryMap.get(category.getParentCategoryId());
                if (parentCategory!=null){
                    parentCategory.getChildren().add(childrenCategoryVO);
                }
            }
        });
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public ApiResponse<List<CategoryVO>> categoryList(){
        return ApiResponseUtil.success("successfully get category lists", buildCategoryTree());

    }

    @Override
    @Transactional
    public ApiResponse<EmptyVO> deleteCategory(Integer categoryId) throws RuntimeException {
        List<Category> categories = categoryMapper.findByIdOrParentId(categoryId);
        if (categories.isEmpty()){
            return ApiResponseUtil.error("invalid category id");
        }

        List<Integer> categoryIds = categories.stream().map(Category::getCategoryId).toList();

        try{
            int deleteCount = categoryMapper.deleteByIdBatch(categoryIds);
            if (deleteCount != categoryIds.size()){
                throw  new RuntimeException("delete category failed");
            }
            // 删除这些分类下的所有题目
            // TODO: 如果用户做了笔记，笔记和问题是对应的，删除了问题，笔记对应的问题就不存在了
            //   需要额外考虑讨论在删除分类的时候是否需要删除对应的笔记信息
            QuestionMapper.deleteByCategoryIdBatch(categoryIds);
            return ApiResponseUtil.success("删除分类成功");
        } catch (Exception e) {
            throw new RuntimeException("delete category failed");
        }
    }

    @Override
    public ApiResponse<CreateCategoryVO> createCategory(CreateCategoryBody categoryBody) {

        if (categoryBody.getParentCategoryId() != 0) {
            Category parent = categoryMapper.findById(categoryBody.getParentCategoryId());
            if (parent == null) {
                return ApiResponseUtil.error("父分类 Id 不存在");
            }
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryBody, category);

        // 插入分类
        try {
            categoryMapper.insert(category);
            CreateCategoryVO createCategoryVO = new CreateCategoryVO();
            createCategoryVO.setCategoryId(category.getCategoryId());
            return ApiResponseUtil.success("successfully created category", createCategoryVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("create category failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> updateCategory(Integer categoryId, UpdateCategoryBody categoryBody){
        Category category = categoryMapper.findById(categoryId);
        if (category == null){
            return ApiResponseUtil.error("category id not exists");
        }

        category.setName(categoryBody.getName());
        try{
            categoryMapper.update(category);
            return ApiResponseUtil.success("successfully updated category");
        } catch (Exception e) {
            return ApiResponseUtil.error("update category failed");
        }
    }

    @Override
    public Category findOrCreateCategory(String categoryName) {
        Category category = categoryMapper.findByName(categoryName.trim());
        if (category != null) return category;

        try {
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(0);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("create failed");
        }
    }

    @Override
    public Category findOrCreateCategory(String categoryName, Integer parentCategoryId) {
        Category category = categoryMapper.findByName(categoryName.trim());
        if (category != null) return category;
        try {
            Category category2 = new Category();
            category2.setName(categoryName.trim());
            category2.setParentCategoryId(parentCategoryId);
            categoryMapper.insert(category2);
            return category2;
        } catch (Exception e) {
            throw new RuntimeException("create failed");
        }
    }



}
