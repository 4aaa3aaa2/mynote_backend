package com.aaa.notes.service.impl;

import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.mapper.UserMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.entity.Note;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.service.SearchService;
import com.aaa.notes.utils.ApiResponseUtil;
import com.aaa.notes.utils.SearchUtils;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.swing.event.ListDataEvent;
import java.security.Key;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.ErrorManager;

@Log4j2
@Service
public class SearchServiceImpl implements SearchService{
    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private static final String NOTE_SEARCH_CACHE_KEY = "search:note:%s:%d:%d";
    private static final String USER_SEARCH_CACHE_KEY = "search:user:%s:%d:%d";
    private static final String NOTE_TAG_SEARCH_CACHE_KEY = "search:note:tag:%s:%s:%d:%d";
    private static final long CACHE_EXPIRE_TIME = 30; // 分钟

    @Override
    public ApiResponse<List<Note>> searchNotes(String keyword, int page, int pageSize){
        try{
            String cacheKey = String.format(NOTE_SEARCH_CACHE_KEY,keyword, page, pageSize);
            List<Note> cacheResult = (List<Note>) redisTemplate.opsForValue().get(cacheKey);
            if(cacheResult != null){
                return  ApiResponseUtil.success("searched",cacheResult);
            }

            keyword = SearchUtils.preprocessKeyword(keyword);

            int offset = (page-1)*pageSize;
            List<Note> notes = noteMapper.searchNotes(keyword, pageSize, offset);
            redisTemplate.opsForValue().set(cacheKey, notes, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

            return ApiResponseUtil.success("searched",notes);
        }
        catch (Exception e) {
            log.error("search failed", e);
            return ApiResponseUtil.error("search failed");
        }
    }

    @Override
    public ApiResponse<List<User>> searchUsers(String keyword, int page, int pageSize) {
        try {
            String cacheKey = String.format(USER_SEARCH_CACHE_KEY, keyword, page, pageSize);

            // 尝试从缓存获取
            List<User> cachedResult = (List<User>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return ApiResponseUtil.success("user searched", cachedResult);
            }

            // 计算偏移量
            int offset = (page - 1) * pageSize;

            // 执行搜索
            List<User> users = userMapper.searchUsers(keyword, pageSize, offset);

            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, users, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

            return ApiResponseUtil.success("user searched", users);
        } catch (Exception e) {
            log.error("search failed", e);
            return ApiResponseUtil.error("search failed");
        }

    }

    @Override
    public ApiResponse<List<Note>> searchNotesByTag(String keyword, String tag, int page, int pageSize) {
        try{
            String cacheKey = String.format(NOTE_SEARCH_CACHE_KEY, keyword, tag, page, pageSize);
            List<Note> cacheResult = (List<Note>) redisTemplate.opsForValue().get(cacheKey);
            if(cacheResult!=null){
                return  ApiResponseUtil.success("searched", cacheResult);
            }

            keyword = SearchUtils.preprocessKeyword(keyword);
            int offset = (page-1)* pageSize;
            List<Note> notes = noteMapper.searchNotesByTag(keyword,tag,pageSize,offset);
            redisTemplate.opsForValue().set(cacheKey,notes,CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

            return  ApiResponseUtil.success("searched", notes);
        }catch (Exception e) {
            log.error("search failed", e);
            return ApiResponseUtil.error("search failed");
        }



    }


}
