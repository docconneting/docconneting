package com.example.docconneting.domain.user.repostiory;

import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PessimisticLockWithPointTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = User.of(
                "test@example.com",
                "password",
                "username",
                1000,
                false,
                UserRole.PATIENT);
        userRepository.save(user);
        userId = user.getId();
    }

    @DisplayName("유저에 대해 동시에 접근했을 때 포인트 정상처리 및 락 동작 확인")
    @Test
    void pessimisticLockTest() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);

        // 첫 번째 트랜잭션: 포인트 차감 후 5초 유지
        Thread t1 = new Thread(() -> {
            TransactionTemplate txTemplate = new TransactionTemplate(
                    new JpaTransactionManager(entityManager.getEntityManagerFactory()));

            txTemplate.executeWithoutResult(transactionStatus -> {
                User user = userRepository.findUserByIdAndUserRoleWithPessimisticLock(userId, UserRole.PATIENT).orElseThrow();
                user.decreasePoint(1000); // 포인트 차감

                System.out.println("[T1] 포인트 차감 완료");
                System.out.println("현재 포인트: " + user.getPoint());

                try {
                    latch.countDown(); // 두 번째 스레드 시작 허용
                    Thread.sleep(5000); // 락 점유 유지
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        // 두 번째 트랜잭션: 락 대기 후 포인트 차감
        Thread t2 = new Thread(() -> {
            try {
                latch.await(); // t1이 countDown 할 때까지 대기

                long start = System.currentTimeMillis();

                TransactionTemplate txTemplate = new TransactionTemplate(
                        new JpaTransactionManager(entityManager.getEntityManagerFactory()));

                txTemplate.executeWithoutResult(transactionStatus -> {
                    User user = userRepository.findUserByIdAndUserRoleWithPessimisticLock(userId, UserRole.PATIENT).orElseThrow();
                    user.refundPoint(1000); // 포인트 환불

                    System.out.println("[T2] 포인트 환불 완료");
                    System.out.println("현재 포인트: " + user.getPoint());
                });

                long duration = System.currentTimeMillis() - start;
                System.out.println("T2 락 대기 시간: " + duration + "ms");
                assertThat(duration).isGreaterThanOrEqualTo(5000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 최종 포인트 확인
        User user = userRepository.findById(userId).orElseThrow();
        System.out.println("최종 포인트: " + user.getPoint());
        assertThat(user.getPoint()).isEqualTo(1000);
    }

}
