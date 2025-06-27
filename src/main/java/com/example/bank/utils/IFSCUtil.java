package com.example.bank.utils;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IFSCUtil {

    private static final Map<String, String> BANK_IFSC_PREFIXES = Map.of(
            "State Bank of India", "SBIN",
            "HDFC Bank", "HDFC",
            "ICICI Bank", "ICIC",
            "Axis Bank", "AXIS",
            "Punjab National Bank", "PUNB",
            "Bank of Baroda", "BARB",
            "Kotak Mahindra Bank", "KKBK",
            "Yes Bank", "YESB",
            "IndusInd Bank", "INDB",
            "Canara Bank", "CNRB"
    );

    public static String getBankIfscPrefix(String bankName) {
        return BANK_IFSC_PREFIXES.getOrDefault(bankName, "GENB");
    }
}
