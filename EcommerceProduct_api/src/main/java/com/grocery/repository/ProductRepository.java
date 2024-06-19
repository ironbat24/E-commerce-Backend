package com.grocery.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grocery.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	@Query(value = "SELECT pc.name AS Category_name, p.product_id, p.prod_name, p.product_img, p.brand, p.description, " + 
            "pi.product_item_id, pi.price, pi.sale_price, ((pi.price - pi.sale_price) / pi.price * 100) AS discount_percentage, " +
            "pi.qty_in_stock, u.unit_id, u.unit_name " +
            "FROM product p " +
            "JOIN product_item pi ON p.product_id = pi.product_id " + 
            "JOIN unit u ON u.unit_id = pi.unit_id " +
            "JOIN product_category pc ON pc.category_id = p.category_id", nativeQuery = true)
    List<Object[]> findProductDetails();
    
	@Query(value = "SELECT pc.name AS Category_name, p.product_id, p.prod_name, p.product_img, p.brand, p.description, " + 
            "pi.product_item_id, pi.price, pi.sale_price, ((pi.price - pi.sale_price) / pi.price * 100) AS discount_percentage, " +
            "pi.qty_in_stock, u.unit_id, u.unit_name " +
            "FROM product p " +
            "JOIN product_item pi ON p.product_id = pi.product_id " + 
            "JOIN unit u ON u.unit_id = pi.unit_id " +
            "JOIN product_category pc ON pc.category_id = p.category_id " +
            "WHERE pi.product_item_id = :productItemId", nativeQuery = true)
    List<Object[]> findProductDetailByProductItemId(@Param("productItemId") Long productItemId);

	@Query(value = "SELECT pc.name AS Category_name, p.product_id, p.prod_name, p.product_img, p.brand, p.description, " + 
            "pi.product_item_id, pi.price, pi.sale_price, ((pi.price - pi.sale_price) / pi.price * 100) AS discount_percentage, " +
            "pi.qty_in_stock, u.unit_id, u.unit_name " +
            "FROM product p " +
            "JOIN product_item pi ON p.product_id = pi.product_id " + 
            "JOIN unit u ON u.unit_id = pi.unit_id " +
            "JOIN product_category pc ON pc.category_id = p.category_id "+ 
            "WHERE LOWER(pc.name) = :category", nativeQuery = true)
	public List<Object[]> findByCategory(@Param("category") String category);
	
}
