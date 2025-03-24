package com.choongang.auction.streamingauction.domain.member.mapper;

import com.choongang.auction.streamingauction.domain.member.dto.response.MemberDTO;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {

    public MemberDTO toDto(Member member) {
        if (member == null) {
            return null;
        }

        // 상품 ID 목록 추출
        List<Long> productIds = member.getProducts().stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());

        return MemberDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .role(member.getRole())
                .productIds(productIds)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .lastLoginAt(member.getLastLoginAt())
                .build();
    }

    public List<MemberDTO> toDtoList(List<Member> members) {
        return members.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}