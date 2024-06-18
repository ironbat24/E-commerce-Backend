package com.grocery.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grocery.dto.ProductDetailDTO;
import com.grocery.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
//	@Query(nativeQuery = true ,value="select * from product p join product_item pi on p.prod_id=pi.product_id;")
//	public List<Product> getProdDetails();
	

    @Query(value = "SELECT p.product_id, p.prod_name, p.product_img, p.brand, p.description, " +
                   "pi.product_item_id, pi.price, pi.sale_price, (pi.sale_price/pi.price*100) AS discount_percentage, " +
                   "pi.qty_in_stock, u.unit_id, u.unit_name " +
                   "FROM product p " +
                   "JOIN product_item pi ON p.product_id = pi.product_id " +
                   "JOIN unit u ON u.unit_id = pi.unit_id", nativeQuery = true)
    List<Object[]> findProductDetails();
}
