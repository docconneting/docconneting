package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.entity.ElasticsearchMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchMessageRepository extends ElasticsearchRepository<ElasticsearchMessage, String> {
    @Query("""
            {
                "match": {
                    "contents": "?0"
                }
            }
            """)
    Page<ElasticsearchMessage> findMessagesByKeyword(String keyword, Pageable pageable);
}
