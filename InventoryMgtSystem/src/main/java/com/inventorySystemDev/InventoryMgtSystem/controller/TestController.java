package com.inventorySystemDev.InventoryMgtSystem.controller;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final ModelMapper modelMapper;

    @GetMapping("/test-mapper")
    public String testModelMapper() {
        // Test if ModelMapper works
        String testResult = modelMapper.toString();
        return "ModelMapper initialized: " + testResult;
    }
}

