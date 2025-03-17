package com.choongang.auction.streamingauction.domain.category.dto;

import com.choongang.auction.streamingauction.domain.category.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private CategoryType categoryType;
}