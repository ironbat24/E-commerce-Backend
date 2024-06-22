package com.grocery;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.grocery.service.AddressService;

@SpringBootTest
class ECommerceApplicationTests {

    @Autowired
    private AddressService addressService;

    @Test
    void contextLoads() {
        assertThat(addressService).isNotNull();
    }
}