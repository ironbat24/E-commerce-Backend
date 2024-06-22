package com.grocery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.dto.ProductDetailDTO;
import com.grocery.exception.ProductException;
import com.grocery.exception.ProductItemException;
import com.grocery.model.Category;
import com.grocery.model.Product;
import com.grocery.model.ProductItem;
import com.grocery.service.CategoryService;
import com.grocery.service.ProductItemService;
import com.grocery.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProductController {
	
    private ProductService productService;
    private ProductItemService productItemService;
    private CategoryService categoryService;
    
    public ProductController(ProductService productService,
                             ProductItemService productItemService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.productItemService = productItemService;
        this.categoryService = categoryService;
    }

    // Product Endpoints

    @GetMapping("/products/details")
    public List<ProductDetailDTO> getProductDetails() {
        return productService.getProductDetails();
    }
    
    @GetMapping("/products/getdiscountedproducts")
    public List<ProductDetailDTO> getTopDiscountedProducts() {
        return productService.displayProductsByDiscount();
    }
    
    @GetMapping("/products/search/{query}")
    public List<ProductDetailDTO> searchProduct(@PathVariable String query){
    	return productService.searchProductDetails(query);
    }
    
    @GetMapping("/products/getbycategory/{categoryName}")
    @Cacheable("products")
    public List<ProductDetailDTO> getProductsByCategory(@PathVariable String categoryName) {
    	return productService.getProductDetailsByCategory(categoryName);
    }
    
    @GetMapping("/products/getdetail/{productItemId}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long productItemId) {
        Optional<ProductDetailDTO> productDetail = productService.getProductDetailByProductItemId(productItemId);
        return productDetail.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/products/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@RequestPart("product") String productJson,
            @RequestPart(value = "categoryImageFile", required = false) MultipartFile categoryImageFile,
            @RequestPart(value = "productImageFile", required = false) MultipartFile productImageFile) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);
        
        Product createdProduct = productService.saveProduct(product, categoryImageFile, productImageFile);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping(value = "/products/editproduct/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> editProduct(@PathVariable Long id,@RequestPart("product") String productJson,
            @RequestPart(value = "categoryImageFile", required = false) MultipartFile categoryImageFile,
            @RequestPart(value = "productImageFile", required = false) MultipartFile productImageFile) throws ProductException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);

        Product updatedProduct = productService.updateProduct(id, product, categoryImageFile, productImageFile);
        return ResponseEntity.ok(updatedProduct);
    }

    
    @GetMapping("/products/viewproducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/products/{id}")
    @CacheEvict(value = "products", key = "#product.id")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    
    
    
    // Product Item Endpoints

    @PostMapping("/productitems")
    public ResponseEntity<ProductItem> createProductItem(@RequestBody ProductItem productItem) {
        ProductItem createdProductItem = productItemService.saveProductItem(productItem);
        return ResponseEntity.ok(createdProductItem);
    }

    @DeleteMapping("/productitems/delete/{id}")
    public void deleteProductItem(@PathVariable Long id) {
        productItemService.deleteProductItem(id);
    }
    
    @GetMapping("/productitems")
    public ResponseEntity<List<ProductDetailDTO>> getAllProductItems() {
        List<ProductDetailDTO> productItems = productService.getProductDetails();
        return ResponseEntity.ok(productItems);
    }
    
    @GetMapping("/productitems/{prodId}")
    public ResponseEntity<List<ProductDetailDTO>> getProductsByProdId(@PathVariable Long prodId) {
        List<ProductDetailDTO> productItems = productService.getProductDetailsByProdId(prodId);
        return ResponseEntity.ok(productItems);
    }
    
    @PutMapping("/productitems/quantity/{productItemId}/{newQuantity}")
    public ProductItem updateQuantityInStock(@PathVariable Long productItemId, @PathVariable int newQuantity) throws ProductItemException {
        return productItemService.updateStockQuantity(productItemId, newQuantity);
    }

    
    
    // Category Endpoints
    @GetMapping("/categories/viewcategories")
    @Cacheable(value = "categories")
    public ResponseEntity<List<Category>> getAllCategories() {
    	List<Category> categories = categoryService.findAllCategories();
    	return ResponseEntity.ok(categories);
    }
}
