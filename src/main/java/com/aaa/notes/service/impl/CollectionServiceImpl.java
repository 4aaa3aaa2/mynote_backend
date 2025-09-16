package com.aaa.notes.service.impl;

import com.aaa.notes.annotation.NeedLogin;
import com.aaa.notes.mapper.CollectionMapper;
import com.aaa.notes.mapper.CollectionNoteMapper;
import com.aaa.notes.mapper.NoteMapper;
import com.aaa.notes.model.base.ApiResponse;
import com.aaa.notes.model.base.EmptyVO;
import com.aaa.notes.model.dto.collection.CollectionQueryParams;
import com.aaa.notes.model.dto.collection.CreateCollectionBody;
import com.aaa.notes.model.dto.collection.UpdateCollectionBody;
import com.aaa.notes.model.entity.Collection;
import com.aaa.notes.model.entity.CollectionNote;
import com.aaa.notes.model.vo.collection.CollectionVO;
import com.aaa.notes.model.vo.collection.CreateCollectionVO;
import com.aaa.notes.scope.RequestScopeData;
import com.aaa.notes.service.CollectionService;
import com.aaa.notes.utils.ApiResponseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionNoteMapper collectionNoteMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public ApiResponse<List<CollectionVO>> getCollections(CollectionQueryParams queryParams){
        List<Collection> collections = collectionMapper.findByCreatorId((queryParams.getCreatorId()));
        List<Integer> collectionIds = collections.stream().map(Collection::getCollectionId).toList();
        final Set<Integer> collectedNoteIdCollectionIds;

        // 查看是否传入了 noteId
        if (queryParams.getNoteId() != null) {
            // 收藏了 noteId 的收藏夹列表
            collectedNoteIdCollectionIds = collectionNoteMapper.filterCollectionIdsByNoteId(queryParams.getNoteId(), collectionIds);
        } else {
            collectedNoteIdCollectionIds = Collections.emptySet();
        }

        List<CollectionVO> collectionVOList = collections.stream().map(collection -> {
            CollectionVO collectionVO = new CollectionVO();
            BeanUtils.copyProperties(collection, collectionVO);

            // 检查是否传入了 noteId 参数并且当前收藏夹收藏了该 note
            if (queryParams.getNoteId() == null) return collectionVO;

            // 设置收藏夹收藏笔记状态
            CollectionVO.NoteStatus noteStatus = new CollectionVO.NoteStatus();

            noteStatus.setIsCollected(collectedNoteIdCollectionIds.contains(collection.getCollectionId()));
            noteStatus.setNoteId(queryParams.getNoteId());
            collectionVO.setNoteStatus(noteStatus);

            return collectionVO;
        }).toList();
        return  ApiResponseUtil.success("got the collections list", collectionVOList);
    }

    @Override
    @NeedLogin
    public ApiResponse<CreateCollectionVO> createCollection(CreateCollectionBody requestBody){
        Long creatorId = requestScopeData.getUserId();

        Collection collection = new Collection();
        BeanUtils.copyProperties(requestBody, collection);
        collection.setCreatorId(creatorId);

        try{
            collectionMapper.insert(collection);
            CreateCollectionVO createCollectionVO = new CreateCollectionVO();
            createCollectionVO.setCollectionId(collection.getCollectionId());
            return ApiResponseUtil.success("create success", createCollectionVO);
        } catch (Exception e) {
            return ApiResponseUtil.error("create failed");
        }
    }

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<EmptyVO> deleteCollection(Integer collectionId){
        Long creatorId = requestScopeData.getUserId();
        Collection collection = collectionMapper.findByIdAndCreatorId(collectionId, creatorId);
        if (collection==null){
            return  ApiResponseUtil.error("no such collection or unable to delete");
        }try{
            collectionMapper.deleteById(collectionId);
            collectionNoteMapper.deleteByCollectionId(collectionId);
            return ApiResponseUtil.success("deleted");
        } catch (Exception e) {
            return  ApiResponseUtil.error("delete failed");
        }
    }

    @Override
    public ApiResponse<EmptyVO> batchModifyCollection(UpdateCollectionBody requestBody) {
        Long userId = requestScopeData.getUserId();
        Integer noteId = requestBody.getNoteId();

        UpdateCollectionBody.UpdateItem[] collections = requestBody.getCollections();
        for (UpdateCollectionBody.UpdateItem collection: collections){
            Integer collectionId = collection.getCollectionId();
            String action = collection.getAction();
            Collection collectionEntity = collectionMapper.findByIdAndCreatorId(collectionId, userId);
            if (collectionEntity == null){
                return ApiResponseUtil.error("no collection or not authenticated");
            }
            if ("create" .equals(action)){
                try{
                    if(collectionMapper.countByCreatorIdAndNoteId(userId,noteId)==0){
                        noteMapper.collectNote(noteId);
                    }

                    CollectionNote collectionNote = new CollectionNote();
                    collectionNote.setCollectionId(collectionId);
                    collectionNote.setNoteId(noteId);
                    collectionNoteMapper.insert(collectionNote);
                } catch (Exception e) {
                    return ApiResponseUtil.error("collect failed");
                }
            }

            if("delete".equals(action)) {
                try {
                    collectionNoteMapper.deleteByCollectionIdAndNoteId(collectionId, noteId);
                    if (collectionMapper.countByCreatorIdAndNoteId(userId, noteId) == 0) {
                        // 笔记不存在，给笔记减少收藏量
                        noteMapper.unCollectNote(noteId);
                    }
                } catch (Exception e) {
                    return ApiResponseUtil.error("取消收藏失败");
                }
            }
        }return ApiResponseUtil.success("succeed");
    }
}



