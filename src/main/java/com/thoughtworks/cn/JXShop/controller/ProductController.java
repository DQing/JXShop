package com.thoughtworks.cn.JXShop.controller;

import com.thoughtworks.cn.JXShop.entity.Inventory;
import com.thoughtworks.cn.JXShop.entity.Product;
import com.thoughtworks.cn.JXShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Inventory inventory = new Inventory(0,0);

        product.setInventory(inventory);

        Product createdProduct = productRepository.save(product);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdProduct.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@RequestBody Product input, @PathVariable("id") Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if(!productOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Product product = productOptional.get();
        product.setDescription(input.getDescription());
        product.setName(input.getName());
        product.setPrice(input.getPrice());

        productRepository.save(product);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(name = "name", required = false) String name, @RequestParam(name = "description", required = false) String description) {

        if (name == null && description == null) {
            return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
        }

        if (name == null && description != null) {
            return new ResponseEntity<>(productRepository.findByDescriptionContaining(description), HttpStatus.OK);
        }

        if (name != null && description == null) {
            return new ResponseEntity<>(productRepository.findByName(name), HttpStatus.OK);
        }

        return new ResponseEntity<>(productRepository.findByDescriptionContainingAndName(description, name), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable("id") String id) {
        Optional<Product> product = productRepository.findById(Long.valueOf(id));

        return product.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
