package com.aaa.notes.controller;

import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.entity.Note;
import com.aaa.notes.model.entity.User;
import com.aaa.notes.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;


/**
 * 根据关键词或者tag搜索，返回搜索结果列表
 * 搜索结果列表根据分页需求分页
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * 搜索笔记，返回笔记列表

     */
    @GetMapping("/notes")
    public ApiResponse<List<Note>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchNotes(keyword, page, pageSize);
    }

    /**
     * 搜索用户，返回用户列表
     */
    @GetMapping("/users")
    public ApiResponse<List<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchUsers(keyword, page, pageSize);
    }

    /**
     * 根据标签搜索笔记，返回笔记列表
     */
    @GetMapping("/notes/tag")
    public ApiResponse<List<Note>> searchNotesByTag(
            @RequestParam String keyword,
            @RequestParam String tag,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return searchService.searchNotesByTag(keyword, tag, page, pageSize);
    }
}
