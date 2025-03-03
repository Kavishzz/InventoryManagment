package com.inventorySystemDev.InventoryMgtSystem.services;

import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;
import com.inventorySystemDev.InventoryMgtSystem.dtos.SupplierDTO;

public interface SupplierService {

    Response addSupplier(SupplierDTO supplierDTO);

    Response updateSupplier(Long id, SupplierDTO supplierDTO);

    Response getAllSupplier();

    Response getSupplierById(Long id);
    
    Response deleteSupplier(Long id);

}
