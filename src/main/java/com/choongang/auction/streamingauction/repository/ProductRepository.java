package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
