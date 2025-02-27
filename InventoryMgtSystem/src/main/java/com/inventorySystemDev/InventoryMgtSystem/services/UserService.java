package com.inventorySystemDev.InventoryMgtSystem.services;

import com.inventorySystemDev.InventoryMgtSystem.dtos.LoginRequest;
import com.inventorySystemDev.InventoryMgtSystem.dtos.RegisterRequest;
import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;
import com.inventorySystemDev.InventoryMgtSystem.dtos.UserDTO;
import com.inventorySystemDev.InventoryMgtSystem.models.User;

public interface UserService {

    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();
    
    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);
}