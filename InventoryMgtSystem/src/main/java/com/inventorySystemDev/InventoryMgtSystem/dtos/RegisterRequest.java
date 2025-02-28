package com.inventorySystemDev.InventoryMgtSystem.dtos;

import com.inventorySystemDev.InventoryMgtSystem.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    private UserRole role;
}
