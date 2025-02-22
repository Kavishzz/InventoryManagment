package com.inventorySystemDev.InventoryMgtSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inventorySystemDev.InventoryMgtSystem.models.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{

}
