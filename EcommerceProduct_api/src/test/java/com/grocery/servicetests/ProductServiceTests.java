package com.grocery.servicetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.grocery.exception.ProductException;
import com.grocery.model.Category;
import com.grocery.model.Product;
import com.grocery.model.ProductItem;
import com.grocery.repository.ProductRepository;
import com.grocery.service.ProductService;

@ExtendWith(MockitoExtension.class) 
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setProdName("Test Product 1");
        product1.setImageUrl("URL 1");
        product1.setDescription("This is a test product 1");
        product1.setBrand("Test Brand 1");
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Test Category 1");
        ProductItem productItem1 = new ProductItem();
        productItem1.setId(1L);
        productItem1.setQuantityInStock(10);
        productItem1.setPrice(100.0);
        productItem1.setProduct(product1);
        product1.setCategory(category1);
        product1.setProductItems(Collections.singletonList(productItem1));
        
        product2 = new Product();
        product2.setId(2L);
        product2.setProdName("Test Product 2");
        product2.setImageUrl("URL 2");
        product2.setDescription("This is a test product 2");
        product2.setBrand("Test Brand 2");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Test Category 2");
        ProductItem productItem2 = new ProductItem();
        productItem2.setId(2L);
        productItem2.setQuantityInStock(10);
        productItem2.setPrice(100.0);
        productItem2.setProduct(product2);
        product2.setCategory(category2);
        product2.setProductItems(Collections.singletonList(productItem2));
    }

    @Test
    void testProductInitialization() {
        assertNotNull(product1);
        assertNotNull(product1.getId());
        assertNotNull(product1.getProdName());
        assertNotNull(product1.getImageUrl());
        assertNotNull(product1.getDescription());
        assertNotNull(product1.getBrand());
        assertNotNull(product1.getCategory());
        assertNotNull(product1.getProductItems());
    }
    
    @Test
    void testFindAllProducts() {
        // Mock repository behavior
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Call service method
        List<Product> foundProducts = productService.findAllProducts();

        // Assertions
        // Assert product1 details
        Product foundProduct1 = foundProducts.get(0);
        assertEquals(product1.getId(), foundProduct1.getId());
        assertEquals(product1.getProdName(), foundProduct1.getProdName());
        assertEquals(product1.getImageUrl(), foundProduct1.getImageUrl());
        assertEquals(product1.getDescription(), foundProduct1.getDescription());
        assertEquals(product1.getBrand(), foundProduct1.getBrand());
        assertEquals(product1.getCategory().getId(), foundProduct1.getCategory().getId());
        assertEquals(product1.getCategory().getName(), foundProduct1.getCategory().getName());

        // Assert product2 details
        Product foundProduct2 = foundProducts.get(1);
        assertEquals(product2.getId(), foundProduct2.getId());
        assertEquals(product2.getProdName(), foundProduct2.getProdName());
        assertEquals(product2.getImageUrl(), foundProduct2.getImageUrl());
        assertEquals(product2.getDescription(), foundProduct2.getDescription());
        assertEquals(product2.getBrand(), foundProduct2.getBrand());
        assertEquals(product2.getCategory().getId(), foundProduct2.getCategory().getId());
        assertEquals(product2.getCategory().getName(), foundProduct2.getCategory().getName());

        // Additional assertions for ProductItems if needed
        assertEquals(1, foundProduct1.getProductItems().size());
        assertEquals(1L, foundProduct1.getProductItems().get(0).getId());
        assertEquals(10, foundProduct1.getProductItems().get(0).getQuantityInStock());
        assertEquals(100.0, foundProduct1.getProductItems().get(0).getPrice());
        assertEquals(product1, foundProduct1);

        assertEquals(1, foundProduct2.getProductItems().size());
        assertEquals(2L, foundProduct2.getProductItems().get(0).getId());
        assertEquals(10, foundProduct2.getProductItems().get(0).getQuantityInStock());
        assertEquals(100.0, foundProduct2.getProductItems().get(0).getPrice());
        assertEquals(product2, foundProduct2);

    }

    
    @Test
    void testFindProductById() throws ProductException {
        // Mocking behavior of the productRepository.findById(id)
        Long productId = 1L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product1));

        // Calling the method under test
        Product foundProduct = productService.findProduct(productId);

        // Verifying the result
        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());
        assertEquals("Test Product 1", foundProduct.getProdName());
        assertEquals("URL 1", foundProduct.getImageUrl());
        assertEquals("This is a test product 1", foundProduct.getDescription());
        assertEquals("Test Brand 1", foundProduct.getBrand());
        assertNotNull(foundProduct.getCategory());
        assertNotNull(foundProduct.getProductItems());
        assertEquals(1, foundProduct.getProductItems().size());

        // Verifying that productRepository.findById(id) was called once
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testFindProductByIdNotFound() {
        // Mocking behavior of the productRepository.findById(id)
        Long productId = 2L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        ProductException exception = assertThrows(ProductException.class,
                () -> productService.findProduct(productId));

        assertEquals("Product not found with id " + productId, exception.getMessage());

        // Verifying that productRepository.findById(id) was called once
        verify(productRepository, times(1)).findById(productId);
    }
    
    
}
