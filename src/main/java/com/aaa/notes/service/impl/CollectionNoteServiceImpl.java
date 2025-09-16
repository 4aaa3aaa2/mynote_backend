package com.aaa.notes.service.impl;



import com.aaa.notes.mapper.CollectionNoteMapper;
import com.aaa.notes.model.entity.CollectionNote;
import com.aaa.notes.service.CollectionNoteService;
import com.aaa.notes.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CollectionNoteServiceImpl implements CollectionNoteService {
    @Autowired
    private CollectionNoteMapper collectionNoteMapper;

    @Override
    public Set<Integer> findUserCollectedNoteIds(Long userId, List<Integer> noteIds){
        List<Integer> userCollectedNoteIds = collectionNoteMapper.findUserCollectedNoteIds(userId,noteIds);
        return new HashSet<>(userCollectedNoteIds);
    }



}
