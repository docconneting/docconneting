package com.example.docconneting.domain.post.scheduler;

import com.example.docconneting.domain.post.service.RefundExpiredPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final RefundExpiredPostService refundExpiredPostService;

    @Scheduled(fixedRate = 60 * 1000)  // 1분마다 실행
    public void processExpiredPosts() {
        refundExpiredPostService.postRefund();
    }
}
