package com.choongang.auction.streamingauction.domain.product.mapper;

import com.choongang.auction.streamingauction.domain.category.dto.CategoryDTO;
import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductImageDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.domain.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .memberId(product.getMember().getId())
                .description(product.getDescription())
                .startingPrice(product.getStartingPrice())
                .bidIncrease(product.getBidIncrease())
                .buyNowPrice(product.getBuyNowPrice())
                .categoryType(product.getCategoryName())
                .sellerUsername(product.getMember() != null ? product.getMember().getUsername() : null)
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductDTO> toDtoList(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductImageDTO toImageDto(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }

        return ProductImageDTO.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .productId(productImage.getProduct() != null ? productImage.getProduct().getProductId() : null)
                .uploadedAt(productImage.getUploadedAt())
                .build();
    }

    public List<ProductImageDTO> toImageDtoList(List<ProductImage> images) {
        return images.stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryType(category.getCategoryType())
                .build();
    }
}