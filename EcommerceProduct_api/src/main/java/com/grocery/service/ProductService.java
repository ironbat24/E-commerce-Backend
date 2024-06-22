package com.grocery.service;

import com.grocery.dto.ProductDetailDTO;
import com.grocery.exception.ProductException;
import com.grocery.model.Category;
import com.grocery.model.Product;
import com.grocery.model.ProductItem;
import com.grocery.model.Units;
import com.grocery.repository.ProductRepository;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
	
    private ProductRepository productRepository;
    private CategoryService categoryService;
    private UnitsService unitService;
    private ImageStorageService imageStorageService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService,
                          UnitsService unitService, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.unitService = unitService;
        this.imageStorageService = imageStorageService;
    }
    
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    public Product findProduct(Long id) throws ProductException{
    	return productRepository.findById(id).orElseThrow(() -> new ProductException("Product not found with id " + id));
    }
    


    @Transactional
    public Product saveProduct(Product product, MultipartFile categoryImageFile, MultipartFile productImageFile) {
        logger.info("Received product: {}", product);

        // Save or find category and its image
        if (product.getCategory() != null) {
            Category category = product.getCategory();
            if (categoryImageFile != null && !categoryImageFile.isEmpty()) {
                String categoryImageUrl = imageStorageService.storeFile(categoryImageFile);
                category.setImageUrl(categoryImageUrl);
            }
            Optional<Category> existingCategory = categoryService.findByName(category.getName());
            Category savedCategory = existingCategory.orElseGet(() -> {
                categoryService.saveCategory(category, categoryImageFile);
                return category;
            });
            product.setCategory(savedCategory);
        }

        // Store the product image file
        if (productImageFile != null && !productImageFile.isEmpty()) {
            String productImageUrl = imageStorageService.storeFile(productImageFile);
            product.setImageUrl(productImageUrl);
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


    @Transactional
    public Product updateProduct(Long id, Product updatedProduct, MultipartFile categoryImageFile, MultipartFile productImageFile) throws ProductException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id " + id));


        existingProduct.setProdName(updatedProduct.getProdName());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setDescription(updatedProduct.getDescription());
        

        if (productImageFile != null && !productImageFile.isEmpty()) {
            String productImageUrl = imageStorageService.storeFile(productImageFile);
            existingProduct.setImageUrl(productImageUrl);
        }


        if (updatedProduct.getCategory() != null) {
            Category category = updatedProduct.getCategory();
            if (categoryImageFile != null && !categoryImageFile.isEmpty()) {
                String categoryImageUrl = imageStorageService.storeFile(categoryImageFile);
                category.setImageUrl(categoryImageUrl);
            }
            Optional<Category> existingCategory = categoryService.findByName(category.getName());
            Category savedCategory = existingCategory.orElseGet(() -> {
                categoryService.saveCategory(category, categoryImageFile);
                return category;
            });
            existingProduct.setCategory(savedCategory);
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
        List<Object[]> results = productRepository.findProductDetails();
        return results.stream().map(this::convertToDTO).toList();
    }
    
    
    @Transactional(readOnly = true)
    public Optional<ProductDetailDTO> getProductDetailByProductItemId(Long productItemId) {
        List<Object[]> results = productRepository.findProductDetailByProductItemId(productItemId);
        if (results != null && !results.isEmpty()) {
            return Optional.of(convertToDTO(results.get(0)));
        } else {
            return Optional.empty();
        }
    }
    
    public List<ProductDetailDTO> searchProductDetails(String query) {
        List<Object[]> results = productRepository.searchProduct(query);
        return results.stream().map(this::convertToDTO).toList();
    }

    
    public List<ProductDetailDTO> getProductDetailsByCategory(String categoryName) {
        List<Object[]> results = productRepository.findByCategory(categoryName);
        return results.stream().map(this::convertToDTO).toList();
    }
    
    public List<ProductDetailDTO> displayProductsByDiscount() {
        List<Object[]> results = productRepository.displaytopdiscounts();
        return results.stream().map(this::convertToDTO).toList();
    }
    
    public List<ProductDetailDTO> getProductDetailsByProdId(Long productId){
    	List<Object[]> results = productRepository.displayProductsById(productId);
    	return results.stream().map(this::convertToDTO).toList();
    }

    private ProductDetailDTO convertToDTO(Object[] result) {
        ProductDetailDTO dto = new ProductDetailDTO();
            dto.setCategoryName((String) result[0]); // Category_name
            dto.setProductId(((Number) result[1]).longValue()); // product_id
            dto.setProdName((String) result[2]); // prod_name
            dto.setProductImg((String) result[3]); // product_img
            dto.setBrand((String) result[4]); // brand
            dto.setDescription((String) result[5]); // description

            dto.setProductItemId(((Number) result[6]).longValue()); // product_item_id
            dto.setPrice(((Number) result[7]).doubleValue()); // price
            dto.setSalePrice(((Number) result[8]).doubleValue()); // sale_price
            dto.setDiscountPercentage(((Number) result[9]).doubleValue()); // discount_percentage
            dto.setQuantityInStock(((Number) result[10]).intValue()); // qty_in_stock

            dto.setUnitId(((Number) result[11]).longValue()); // unit_id
            dto.setUnitName((String) result[12]); // unit_name

        return dto;
    }


}
