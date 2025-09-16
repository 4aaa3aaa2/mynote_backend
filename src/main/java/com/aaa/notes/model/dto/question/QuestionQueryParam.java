package com.aaa.notes.model.dto.question;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


import lombok.Data;


@Data
public class QuestionQueryParam {

    @Min(value = 1, message = "categoryId 必须为正整数")
    private Integer categoryId;

    @Pattern(regexp = "^(view|difficulty)$", message = "sort 必须为 view 或 difficulty")
    private String sort;

    @Pattern(regexp = "^(asc|desc)$", message = "order 必须为 asc 或 desc")
    private String order;

    @NotNull(message = "page 不能为空")
    @Min(value = 1, message = "page 必须为正整数")
    private Integer page;

    @NotNull(message = "pageSize 不能为空")
    @Min(value = 1, message = "pageSize 必须为正整数")
    @Max(value = 200, message = "pageSize 不能超过 200")
    private Integer pageSize;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
