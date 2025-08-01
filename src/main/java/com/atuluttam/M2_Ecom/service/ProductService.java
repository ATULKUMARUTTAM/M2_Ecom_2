package com.atuluttam.M2_Ecom.service;

import com.atuluttam.M2_Ecom.dto.ProductRequest;
import com.atuluttam.M2_Ecom.dto.ProductResponse;
import com.atuluttam.M2_Ecom.model.Product;
import com.atuluttam.M2_Ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product, productRequest);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    private ProductResponse mapToProductResponse(Product savedProduct) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(savedProduct.getId());
        productResponse.setName(savedProduct.getName());
        productResponse.setActive(savedProduct.getActive());
        productResponse.setPrice(savedProduct.getPrice());
        productResponse.setCategory(savedProduct.getCategory());
        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setImageUrl(savedProduct.getImageUrl());
        productResponse.setStockQunatity(savedProduct.getStockQunatity());

        return productResponse;
    }

    private void updateProductFromRequest(Product product, ProductRequest productRequest) {

        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQunatity(productRequest.getStockQunatity());
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {

        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFromRequest(existingProduct, productRequest);
                    Product saveProduct = productRepository.save(existingProduct);
                    return mapToProductResponse(saveProduct);
                });


    }

    public List<ProductResponse> getAllProducts() {
    return productRepository.findByActiveTrue().stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
    }

//    public void deleteProduct(Long id) {
//
//    Optional<Product> product = productRepository.findById(id);
//    if(product.isPresent())
//    {
//        product.get().setActive(false);
//        productRepository.save(product.get());
//    }
//    else
//        throw new RuntimeException("Product Not Found");
//    }


    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(false);
            productRepository.save(product);
            return true;
        }
        return false;
        }


    public List<ProductResponse> searchProduct(String keyword) {
        return productRepository.searchproduct(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

    }
}