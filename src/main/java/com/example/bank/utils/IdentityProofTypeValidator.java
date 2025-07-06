package com.example.bank.utils;

import com.example.bank.Enum.IdentityProofType;
import org.springframework.stereotype.Component;

@Component
public class IdentityProofTypeValidator {


    public boolean nameValidate(String name) {
        // Check if the name is not null and contains only letters and spaces
        return name != null && name.matches("[a-zA-Z ]+");
    }

    public boolean isValid(IdentityProofType identityProofType, String identityProofId) {
        switch (identityProofType) {
            case AADHAAR_CARD:
                return identityProofId.matches("\\d{12}"); // exactly 12 digits

            case PAN_CARD:
                return identityProofId.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}"); // e.g., ABCDE1234F

            case VOTER_ID:
                return identityProofId.matches("[A-Z]{3}[0-9]{7}"); // example pattern: ABC1234567

            case PASSPORT:
                return identityProofId.matches("[A-Z]{1}[0-9]{7}"); // e.g., A1234567

            case  DRIVING_LICENCE:
                return identityProofId.matches("[A-Z]{2}[0-9]{2}[0-9]{11}"); // e.g., MH12XXXXXXX

            default:
                return false; // invalid proof type
        }
    }
}
