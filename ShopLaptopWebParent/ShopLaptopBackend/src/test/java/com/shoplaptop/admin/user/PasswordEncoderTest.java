package com.shoplaptop.admin.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    @DisplayName("Test encode password")
    public void testEncodePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "nam2020";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println(encodedPassword);
        Assertions.assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
