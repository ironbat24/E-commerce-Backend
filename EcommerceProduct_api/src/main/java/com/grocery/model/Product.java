package com.grocery.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "prod_name", nullable = false)
    private String prodName;

    @Column(name = "product_img")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "brand")
    private String brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductItem> productItems;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<ProductItem> getProductItems() {
		return productItems;
	}

	public void setProductItems(List<ProductItem> productItems) {
		this.productItems = productItems;
	}

	public Product() {
		super();
	}

	public Product(Long id, String prodName, String imageUrl, String description, String brand, Category category,
			List<ProductItem> productItems) {
		super();
		this.id = id;
		this.prodName = prodName;
		this.imageUrl = imageUrl;
		this.description = description;
		this.brand = brand;
		this.category = category;
		this.productItems = productItems;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", prodName=" + prodName + ", imageUrl=" + imageUrl + ", description="
				+ description + ", brand=" + brand + ", category=" + category + ", productItems=" + productItems + "]";
	}
    
    
}
