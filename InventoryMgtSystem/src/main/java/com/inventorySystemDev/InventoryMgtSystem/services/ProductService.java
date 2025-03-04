package com.inventorySystemDev.InventoryMgtSystem.services;

import org.springframework.web.multipart.MultipartFile;

import com.inventorySystemDev.InventoryMgtSystem.dtos.ProductDTO;
import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;

public interface ProductService {

    Response saveProduct(ProductDTO productDTO, MultipartFile imagFile); 

    Response updateProduct(ProductDTO productDTO, MultipartFile imagFile);

    Response getAllProducts();

    Response getProductById(Long id);

    Response deleteProduct(Long id);

    Response searchProduct(String input);
}
