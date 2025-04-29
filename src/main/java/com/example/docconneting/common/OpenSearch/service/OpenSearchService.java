package com.example.docconneting.common.OpenSearch.service;

import com.example.docconneting.common.OpenSearch.dto.UpdateDto;
import com.example.docconneting.common.OpenSearch.entity.IndexData;
import com.example.docconneting.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class OpenSearchService {
//
//    private final OpenSearchClient client;
//
//    //인덱스 생성
//    public void createIndex() throws Exception {
//        String index = "sample-index";
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
//        client.indices().create(createIndexRequest);
//
//        IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
//        PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder().index(index).settings(indexSettings).build();
//        client.indices().putSettings(putIndicesSettingsRequest);
//    }
//
//    //OpenSearch에 인덱싱(저장)
//    public void indexDocumentUsingDTO(User user) throws IOException {
//        //저장할 인덱스 이름
//        String index = "sample-index";
//
//        //내용
//        IndexData indexData = IndexData.of("first_name", "Bruce");
//
//        //저장
//        IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("1").document(indexData).build();
//        client.index(indexRequest);
//    }
//
//    //OpenSearch에 전체 업데이트
//    public void overwriteDocument(User user) throws IOException {
//        String index = "sample-index";
//        String id = "1";
//
//        // 새로 덮어쓸 문서 데이터 (예시)
//        IndexData updatedData = IndexData.of("updated_name", "Tony");
//
//        // 덮어쓰기 요청 (기존 문서가 동일 ID로 존재하면 삭제 후 재작성)
//        IndexRequest<IndexData> request = new IndexRequest.Builder<IndexData>()
//                .index(index)
//                .id(id)
//                .document(updatedData)
//                .build();
//
//        // 실행
//        IndexResponse response = client.index(request);
//        System.out.println("Overwritten document ID: " + response.id());
//    }
//
//    // 부분 업데이트: firstName만 변경
//    public void updateDocumentPartially(String id) throws IOException {
//        String index = "sample-index";
//
//        // 일부만 수정할 필드만 채워서 전달
//        UpdateDto partialData = new UpdateDto("UpdatedFirstName", null);
//
//        // UpdateRequest 구성
//        UpdateRequest<IndexData, UpdateDto> request = new UpdateRequest.Builder<IndexData, UpdateDto>()
//                .index(index)
//                .id(id)
//                .doc(partialData)
//                .build();
//
//        // 실행
//        UpdateResponse<IndexData> response = client.update(request, IndexData.class);
//        System.out.println("✅ Updated document ID: " + response.id());
//    }
//
//
//    //OpenSearch에서 삭제
//    public void deleteDocumentById(Long id) {
//        String indexName = "sample-index-name";
//        try {
//            DeleteRequest deleteRequest = DeleteRequest.of(builder ->
//                    builder.index(indexName)
//                            .id(String.valueOf(id))
//            );
//            DeleteResponse response = client.delete(deleteRequest);
//            System.out.println("Delete Response: " + response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}