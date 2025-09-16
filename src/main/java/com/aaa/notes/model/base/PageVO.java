package com.aaa.notes.model.base;

import lombok.Data;
import java.util.List;

//把总帖子数根据每页展示条数进行分页
@Data
public class PageVO<T> {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    private Integer totalPages;
    private List<T> list;

    public PageVO(Integer page, Integer pageSize, Integer total,List<T> list){
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;        
    }


    /**
     * 创建分页结果
     *
     * @param list 数据列表
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param total 总记录数
     * @return 分页结果
     */
    public static <T>PageVO<T> of (List<T> list, Integer page, Integer pageSize, Integer total){
        PageVO<T> pageVO = new PageVO<>(page, pageSize, total, list);
        pageVO.setTotalPages((total + pageSize -1)/pageSize);
        return pageVO;
    }



}
