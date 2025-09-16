package com.aaa.notes.service.impl;

import com.aaa.notes.mapper.StatisticMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.Pagination;
import com.aaa.notes.model.dto.statistic.StatisticQueryParam;
import com.aaa.notes.model.entity.Statistic;
import com.aaa.notes.service.StatisticService;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    private StatisticMapper statisticMapper;

    @Override
    public ApiResponse<List<Statistic>> getStatistic(StatisticQueryParam queryParam) {
        Integer page = queryParam.getPage();
        Integer pageSize = queryParam.getPageSize();
        int offset = PaginationUtils.calculateOffset(page, pageSize);
        int total  = statisticMapper.countStatistic();
        Pagination pagination = new Pagination(page, pageSize, total);

        try{
            List<Statistic> statistics = statisticMapper.findByPage(pageSize,offset);
            return ApiResponseUtil.success("got statistic list", statistics, pagination);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
}
