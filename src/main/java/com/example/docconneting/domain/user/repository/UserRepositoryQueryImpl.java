package com.example.docconneting.domain.user.repository;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.docconneting.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> findDoctors(Pageable pageable, String category, String name) {
        List<User> users = jpaQueryFactory
                    .select(user)
                    .from(user)
                    .where(categoryEq(category), isDeletedEq(), nameEq(name))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(user.count())
                    .from(user)
                    .where(categoryEq(category), isDeletedEq(), nameEq(name));

        return PageableExecutionUtils.getPage(users, pageable, countQuery::fetchOne);
    }

    BooleanExpression isDeletedEq() {
        return user.isDeleted.eq(Boolean.FALSE);
    }

    BooleanExpression categoryEq(String category) {
        if (category == null) {
            return null;
        }
        return user.major.eq(Major.of(category));
    }

    BooleanExpression nameEq(String name) {
        if (name == null) {
            return null;
        }
        return user.username.contains(name);
    }
}
