package com.choongang.auction.streamingauction.repository.custom;


import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.member.entity.QMember;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersToSuggest(Long currentUserId, int limit) {
        QMember member = QMember.member;


        NumberTemplate<Double> rand = Expressions.numberTemplate(Double.class, "function('rand')");

        return queryFactory
                .selectFrom(member)
                .where(
                        member.id.ne(currentUserId)

                )
                .orderBy(
                        member.createdAt.desc(),
                      
                        rand.asc()
                )
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Member> searchMembers(String keyword) {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(member.username.containsIgnoreCase(keyword))
                .orderBy(member.username.asc())
                .limit(5)
                .fetch();
    }

    @Override
    public void updateProfileImage(String imageUrl, String username) {
        QMember member = QMember.member;

        queryFactory
                .update(member)

                .set(member.updatedAt, LocalDateTime.now())
                .where(member.username.eq(username))
                .execute();
    }
}