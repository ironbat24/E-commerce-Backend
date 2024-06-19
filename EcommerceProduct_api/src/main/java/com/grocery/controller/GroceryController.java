package com.grocery.controller;

import com.grocery.dto.ProductDetailDTO;
import com.grocery.exception.ProductException;
import com.grocery.exception.ProductItemException;
import com.grocery.model.Category;
import com.grocery.model.Product;
import com.grocery.model.ProductItem;
import com.grocery.service.CategoryService;
import com.grocery.service.ProductItemService;
import com.grocery.service.ProductService;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GroceryController {
	
    private final ProductService productService;
    private final ProductItemService productItemService;
    private final CategoryService categoryService;
    
    public GroceryController(ProductService productService,
                             ProductItemService productItemService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.productItemService = productItemService;
        this.categoryService = categoryService;
    }

    // Product Endpoints

    @GetMapping("/products/details")
    @Cacheable("products")
    public List<ProductDetailDTO> getProductDetails() {
        return productService.getProductDetails();
    }
    
    @GetMapping("/products/getdetail/{productItemId}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Long productItemId) {
        Optional<ProductDetailDTO> productDetail = productService.getProductDetailByProductItemId(productItemId);
        return productDetail.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/products/create")
    public ResponseEntity<Product> createProductWithDetails(@RequestBody Product product) {
        Product createdProduct = productService.saveProductWithDetails(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    @PutMapping("/products/editproduct/{id}")
    public ResponseEntity<Product> editProduct(@PathVariable Long id, @RequestBody Product product) throws ProductException{
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    	
    }
    
    @GetMapping("/products/viewproducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/products/{id}")
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
    
    @PutMapping("/productitems/quantity/{productItemId}/{newQuantity}")
    public ProductItem updateQuantityInStock(@PathVariable Long productItemId, @PathVariable int newQuantity) throws ProductItemException {
        return productItemService.updateStockQuantity(productItemId, newQuantity);
    }

    
    
    // Category Endpoints
    @GetMapping("/categories/viewcategories")
    public ResponseEntity<List<Category>> getAllCategories() {
    	List<Category> categories = categoryService.findAllCategories();
    	return ResponseEntity.ok(categories);
    }
}
