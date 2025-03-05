package com.inventorySystemDev.InventoryMgtSystem.services.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inventorySystemDev.InventoryMgtSystem.dtos.ProductDTO;
import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;
import com.inventorySystemDev.InventoryMgtSystem.exceptions.NotFoundException;
import com.inventorySystemDev.InventoryMgtSystem.models.Category;
import com.inventorySystemDev.InventoryMgtSystem.models.Product;
import com.inventorySystemDev.InventoryMgtSystem.repositories.CategoryRepository;
import com.inventorySystemDev.InventoryMgtSystem.repositories.ProductRepository;
import com.inventorySystemDev.InventoryMgtSystem.services.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-image/";
    
    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imagFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                            .orElseThrow(() -> new NotFoundException("Category Not Found"));

        // map our dto to product entity                   
        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .description(productDTO.getDescription())
                .category(category)
                .build();

        if(imagFile != null && !imagFile.isEmpty()){
            log.info("Image file exists");
            String imagePath = saveImage(imagFile);
            productToSave.setImageUrl(imagePath);
        }

        //save the product entity
        productRepository.save(productToSave);

        return Response.builder()
                    .status(200)
                    .message("Product successfully saved")
                    .build();
    }

    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imagFile) {
        //check if product exist
        Product exisProduct = productRepository.findById(productDTO.getProductId())
                        .orElseThrow(() -> new NotFoundException("Product Not Found"));

        //check if image is associated with the product to update and upload
        if(imagFile != null && !imagFile.isEmpty()){
            String imagePath = saveImage(imagFile);
            exisProduct.setImageUrl(imagePath);
        }

        //cehck if category is to be changed for the products
        if(productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0){
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                                .orElseThrow(() -> new NotFoundException("Category Not Found"));
            exisProduct.setCategory(category);
        }

        //check if product fields need to be changed and updated
        if(productDTO.getName() != null && !productDTO.getName().isBlank()){
            exisProduct.setName(productDTO.getName());
        }
        if(productDTO.getSku() != null && !productDTO.getSku().isBlank()){
            exisProduct.setSku(productDTO.getSku());
        }
        if(productDTO.getDescription() != null && !productDTO.getDescription().isBlank()){
            exisProduct.setDescription(productDTO.getDescription());
        }
        if(productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0){
            exisProduct.setPrice(productDTO.getPrice());
        }
        if(productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0){
            exisProduct.setStockQuantity(productDTO.getStockQuantity());
        }

        //update the product
        productRepository.save(exisProduct);

        //Build our response
        return Response.builder()
                .status(200)
                .message("Product Updated Successfully")
                .build();
    }

    @Override
    public Response getAllProducts() {
        log.info("Request came to getAllProducts...");
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        log.info("Product list size: {}", productList.size()); // log to check if product list is empty

        if (productList.isEmpty()) {
            log.error("No products found in the database!");
        }

        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();
    }

    @Override
    public Response getProductById(Long id) {

        Product product = productRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product Not Found"));


        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    @Override
    public Response deleteProduct(Long id) {
        productRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product Not Found"));

        productRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Product Deleted Successfully")
                .build();
    }

    @Override
    public Response searchProduct(String input) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);

        if(products.isEmpty()){
            throw new NotFoundException("Product Not Found");
        }

        List<ProductDTO> productDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOList)
                .build();

    }


    @SuppressWarnings("null")
    private String saveImage(MultipartFile imageFile){
        //validate image and check if it is greater than 1GIG
        if(!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024 * 1024 * 1024){
            throw new IllegalArgumentException("Only image file under 1GIG is allowed");
        }

        //create the directory if it doesn't exist
        File directory = new File(IMAGE_DIRECTORY);

        if(!directory.exists()){
            directory.mkdir();
            log.info("Directory was created");
        }

        //generate unique file name for the image
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        //Get the absolute path of the image
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try{
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile); //we are wrting the image to this folder 
        }catch(IOException e){
            throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
        }  

        return imagePath;
    }

    

}
