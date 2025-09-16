package com.aaa.notes.model.dto.note;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//笔记查询参数dto
@Data
public class NoteQueryParams {

    /*
     * 问题ID
     * 必须是正整数
     */
    @Min(value = 1, message = "id must be integer")
    private Integer questionId;

    /*
     * 作者ID
     * 必须是正整数且符合系统生成的范围
     */
    @Min(value = 1, message = "author id must be integer")
    private Long authorId;

    /*
     * 收藏夹ID
     * 必须是正整数
     */
    @Min(value = 1, message = "collection id must be integer")
    private Integer collectionId;

    /*
     * 排序字段
     * 只能是固定的枚举值（比如 "create", "update"）。
     */
    @Pattern(
            regexp = "create",
            message = "create"
    )
    private String sort;

    /*
     * 排序方向
     * 只能是 "asc" 或 "desc"，区分大小写。
     */
    @Pattern(
            regexp = "asc|desc",
            message = "asc or desc"
    )
    private String order;

    /*
     * 最近天数
     * 必须是1到365之间的整数，默认限制为一年内。
     */
    @Min(value = 1, message = "at least 1day")
    @Max(value = 365, message = "<365days")
    private Integer recentDays;

    /*
     * 当前页码
     * 必须是正整数，默认为1。
     */
    @NotNull(message = "page not empty")
    @Min(value = 1, message = "page >=1")
    @Max(value = 10000, message = "page < 10000")
    private Integer page = 1;

    /*
     * 每页大小
     * 必须是正整数，限制范围在 1到100之间。
     */
    @NotNull(message = "page not empty")
    @Min(value = 1, message = "pagesize>=1")
    @Max(value = 200, message = "pagesize<100")
    private Integer pageSize = 10;

}
