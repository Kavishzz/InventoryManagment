package com.inventorySystemDev.InventoryMgtSystem.services;

import com.inventorySystemDev.InventoryMgtSystem.dtos.CategoryDTO;
import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategories();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response deleteCategory(Long id);

}
