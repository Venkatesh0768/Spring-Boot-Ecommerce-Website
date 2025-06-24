package com.venky.ecomerce.controllers;

import com.venky.ecomerce.models.Product;
import com.venky.ecomerce.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService= productService;
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(){
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/product/{prodId}")
    public ResponseEntity<?> getProductId(@PathVariable int prodId){
        Product product = productService.getProductById(prodId);
        if(product != null){
            return new ResponseEntity<>(product ,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProducts(@RequestPart Product product , @RequestPart MultipartFile imageFile){
        try {
            Product product1 = productService.addProducts(product, imageFile);
            return new ResponseEntity<>(product1 , HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId) {
        Product product = productService.getProductById(productId);

        if (product == null || product.getImageData() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // or a custom error message
        }

        byte[] imageFile = product.getImageData();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType())) // "image/png", "image/jpeg", etc.
                .body(imageFile);
    }


    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id , @RequestPart Product product , @RequestPart MultipartFile imageFile) {
        Product product1 =  null;
        try {
            product1 = productService.updateProduct(id , product, imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(product1 == null){
            return new ResponseEntity<>("Product is updated" , HttpStatus.OK);
        }
        return  new ResponseEntity<>("Fail to update" , HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable int id){
        Product product = productService.getProductById(id);
        if (product != null){
            productService.deleteProduct(id);
            return new ResponseEntity<>("Product Deleted" , HttpStatus.OK);
        }

        return  new ResponseEntity<>("Product Not Found" , HttpStatus.NOT_FOUND);
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(String keyword){
        List<Product> products = productService.searchProducts(keyword);
        return new ResponseEntity<>(products , HttpStatus.OK);
    }
}
