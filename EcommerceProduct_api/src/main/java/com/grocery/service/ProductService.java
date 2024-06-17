package com.grocery.service;

import com.grocery.dto.ProductDetailDTO;
import com.grocery.exception.ProductException;
import com.grocery.model.Category;
import com.grocery.model.Product;
import com.grocery.model.ProductItem;
import com.grocery.model.Units;
import com.grocery.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UnitsService unitService;
    
    @Autowired
    private EntityManager entityManager;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    public Product findProduct(Long id) throws ProductException{
    	return productRepository.findById(id).orElseThrow(() -> new ProductException("Product not found with id " + id));
    }

    @Transactional
    public Product saveProduct(Product product) {
        if (product.getCategory() != null) {
            Category category = categoryService.findByName(product.getCategory().getName())
                    .orElseGet(() -> categoryService.saveCategory(product.getCategory()));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product saveProductWithDetails(Product product) {
        logger.info("Received product: {}", product);
        
        // Save or find category
        if (product.getCategory() != null) {
            Category category = categoryService.findByName(product.getCategory().getName())
                    .orElseGet(() -> categoryService.saveCategory(product.getCategory()));
            product.setCategory(category);
        }

        // Save product items and units
        if (product.getProductItems() != null) {
            for (ProductItem item : product.getProductItems()) {
                Units unit = item.getUnit();
                if (unit != null) {
                    Optional<Units> existingUnit = unitService.findByUnitName(unit.getUnitName());
                    if (existingUnit.isPresent()) {
                        item.setUnit(existingUnit.get());
                    } else {
                        unit = unitService.save(unit);
                        item.setUnit(unit);
                    }
                }
                item.setProduct(product);
            }
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Transactional
    public void deleteProductIfNoItemsLeft(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && product.getProductItems().isEmpty()) {
            productRepository.delete(product);
        }
    }
    
//    public List<Product> getDetails() {
//        return productRepository.getDetails();
//    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) throws ProductException{
        Optional<Product> existingProductOpt = Optional.ofNullable(productRepository.findById(id)
        		.orElseThrow(() -> new ProductException("Product not found with id " + id)));
        Product existingProduct = existingProductOpt.get();

        // Update product details
        existingProduct.setProdName(updatedProduct.getProdName());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        // Update or find category
        if (updatedProduct.getCategory() != null) {
            Category category = categoryService.findByName(updatedProduct.getCategory().getName())
                    .orElseGet(() -> categoryService.saveCategory(updatedProduct.getCategory()));
            existingProduct.setCategory(category);
        }

        // Update product items
        if (updatedProduct.getProductItems() != null) {
            // Remove existing product items
            existingProduct.getProductItems().clear();

            // Add updated product items
            for (ProductItem item : updatedProduct.getProductItems()) {
                Units unit = item.getUnit();
                if (unit != null) {
                    Optional<Units> existingUnit = unitService.findByUnitName(unit.getUnitName());
                    if (existingUnit.isPresent()) {
                        item.setUnit(existingUnit.get());
                    } else {
                        unit = unitService.save(unit);
                        item.setUnit(unit);
                    }
                }
                item.setProduct(existingProduct);
                existingProduct.getProductItems().add(item);
            }
        }

        return productRepository.save(existingProduct);
    }

    public List<ProductDetailDTO> getProductDetails() {
        String sql = "SELECT p.product_id, p.prod_name, p.product_img, p.brand, p.description, " +
                     "pi.product_item_id, pi.price, pi.sale_price, (pi.sale_price/pi.price*100) AS discount_percentage, " +
                     "pi.qty_in_stock, u.unit_id, u.unit_name " +
                     "FROM product p " +
                     "JOIN product_item pi ON p.product_id = pi.product_id " +
                     "JOIN unit u ON u.unit_id = pi.unit_id";
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ProductDetailDTO convertToDTO(Object[] result) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(((Number) result[0]).longValue());
        dto.setProdName((String) result[1]);
        dto.setProductImg((String) result[2]);
        dto.setBrand((String) result[3]);
        dto.setDescription((String) result[4]);

        dto.setProductItemId(((Number) result[5]).longValue());
        dto.setPrice(((Number) result[6]).doubleValue());
        dto.setSalePrice(((Number) result[7]).doubleValue());
        dto.setDiscountPercentage(((Number) result[8]).doubleValue());
        dto.setQuantityInStock((int) result[9]);

        dto.setUnitId(((Number) result[10]).longValue());
        dto.setUnitName((String) result[11]);

        return dto;
    }
}
