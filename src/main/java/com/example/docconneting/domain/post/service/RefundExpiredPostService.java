package com.example.docconneting.domain.post.service;

import com.example.docconneting.domain.point.service.PointRefundService;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.enums.PayType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundExpiredPostService {

    private static final int REFUND_POINT = 1000;

    private final FindExpiredPostService findExpiredPostService;
    private final PointRefundService pointRefundService;

    @Transactional
    public void postRefund() {
        // 만료된 유료 게시판 조회
        List<Post> expiredPost = findExpiredPostService.findExpiredPosts();

        for (Post post : expiredPost) {
            if (!post.getIsReplied()) {
                if(post.getPayType().equals(PayType.POINT)) {
                    // 환불
                    pointRefundService.refundPoint(post.getPatient().getId(), post.getId(), REFUND_POINT);
                    log.info("Refunded postId = {}, userId = {}, refunded = {} point", post.getId(), post.getPatient().getId(), REFUND_POINT);
                }
                // 무료 게시판으로 전환
                post.changePayTypeAndDeadline();
            }
        }
    }
}
