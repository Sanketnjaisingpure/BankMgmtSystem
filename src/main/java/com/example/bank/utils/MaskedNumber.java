package com.example.bank.utils;

import org.springframework.stereotype.Component;

@Component
public class MaskedNumber {

    public String maskNumber(String number) {
        if (number == null || number.length() < 4) {
            return number; // Return the original number if it's null or too short
        }
        int length = number.length();
        // Mask all but the last 4 digits
        return "*".repeat(length - 4) +
                number.substring(length - 4);
    }
}
