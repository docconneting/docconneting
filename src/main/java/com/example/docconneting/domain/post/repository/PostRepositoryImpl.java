package com.example.docconneting.domain.post.repository;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.post.entity.Post;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.docconneting.domain.post.entity.QPost.post;
import static com.example.docconneting.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Post> findPosts(Pageable pageable, String title ,String major) {
        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.patient, user).fetchJoin()
                .where(searchByTitle(title), searchByMajor(major), post.isDeleted.isFalse())
                .orderBy(orderBy(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(post.count())
                .from(post)
                .where(searchByMajor(major))
                .fetchOne();

        return new PageImpl<>(posts, pageable, total);
    }

    private BooleanExpression searchByMajor(String major){
        if(major == null) {
            return null;
        }

        return post.major.eq(Major.of(major));
    }

    private BooleanExpression searchByTitle(String title){
        if(title == null) {
            return null;
        }

        return post.title.contains(title);
    }

    private OrderSpecifier orderBy(Pageable pageable){
        Sort sort = pageable.getSort();

        if(sort.isSorted()){
            Sort.Order order = sort.iterator().next();
            String fieldName = order.getProperty();

            if(fieldName.equals("createdAt")){
                return new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        post.createdAt
                );
            }
        }
        return post.createdAt.desc();
    }
}
