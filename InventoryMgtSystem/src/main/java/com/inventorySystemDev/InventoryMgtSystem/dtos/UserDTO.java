package com.inventorySystemDev.InventoryMgtSystem.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.inventorySystemDev.InventoryMgtSystem.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private long id;

    private String name;

   
    private String email;

    @JsonIgnore
    private String password;

    private String phoneNumber;

    private UserRole role;

    private List<TransactionDTO> transactions;

    private LocalDateTime createdAt;

     

}
