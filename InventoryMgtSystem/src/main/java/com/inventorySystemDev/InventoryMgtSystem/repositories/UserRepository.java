package com.inventorySystemDev.InventoryMgtSystem.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventorySystemDev.InventoryMgtSystem.models.User;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email);
}
