package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.TradeRecordDTO;
import com.choongang.auction.streamingauction.domain.entity.QTradeRecord;
import com.choongang.auction.streamingauction.domain.member.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeRecordService {

    private final JPAQueryFactory queryFactory;

    public TradeRecordService(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // queryFactory.select()는 JPAQuery<T> 타입을 반환 하는데
    // switch 문에서 동적으로 정렬 조건을 추가하려면 JPAQuery 객체가 필요

    public List<TradeRecordDTO> getSales(Long userId, String sortBy) {
        QTradeRecord tr = QTradeRecord.tradeRecord;
        QMember b = QMember.member;

        var query = queryFactory
                .select(Projections.constructor(TradeRecordDTO.class,
                        tr.tradeId, tr.itemName, tr.amount, tr.productId, tr.createdAt,
                        b.username)) // DTO 순서에 맞춤
                .from(tr)
                .join(b).on(tr.buyer.eq(b.id))
                .where(tr.seller.eq(userId));

        switch (sortBy) {
            case "latest" -> query.orderBy(tr.createdAt.desc());
            case "priceHigh" -> query.orderBy(tr.amount.desc());
            case "priceLow" -> query.orderBy(tr.amount.asc());
            default -> query.orderBy(tr.createdAt.desc());
        }

        return query.fetch();
    }

    public List<TradeRecordDTO> getPurchases(Long userId, String sortBy) {
        QTradeRecord tr = QTradeRecord.tradeRecord;
        QMember s = QMember.member;

        var query = queryFactory
                .select(Projections.constructor(TradeRecordDTO.class,
                        tr.tradeId, tr.itemName, tr.amount, tr.productId, tr.createdAt,
                        s.username)) // DTO 순서에 맞춤
                .from(tr)
                .join(s).on(tr.seller.eq(s.id))
                .where(tr.buyer.eq(userId));

        switch (sortBy) {
            case "latest" -> query.orderBy(tr.createdAt.desc());
            case "priceHigh" -> query.orderBy(tr.amount.desc());
            case "priceLow" -> query.orderBy(tr.amount.asc());
            default -> query.orderBy(tr.createdAt.desc());
        }

        return query.fetch();
    }
}