package com.example.demo.controller;

import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepo;

    // ➕ Create Product
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product, Authentication authentication) {
        String email = authentication.getName();
        System.out.println("🔐 Creating product as: " + email);

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        product.setUser(user);
        return ResponseEntity.ok(productRepo.save(product));
    }

    // 📋 Get All Products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // 📄 Get Single Product
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(404).body("Product not found");
        }
    }

    // ✏️ Update Product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct, Authentication authentication) {
        String email = authentication.getName();
        Optional<Product> existing = productRepo.findById(id);

        if (existing.isEmpty()) return ResponseEntity.status(404).body("Product not found");

        Product product = existing.get();
        if (!product.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("You are not the owner of this product");
        }

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());

        return ResponseEntity.ok(productRepo.save(product));
    }

    // ❌ Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Product> product = productRepo.findById(id);

        if (product.isEmpty()) return ResponseEntity.status(404).body("Product not found");

        if (!product.get().getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("You are not the owner of this product");
        }

        productRepo.delete(product.get());
        return ResponseEntity.ok("Product deleted successfully");
    }
}
