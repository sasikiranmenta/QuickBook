package com.sasi.quickbooks.model;

public enum QuickBookHSNEnum {
    GOLD(7113),
    SILVER(7114);

    private final int value;

    QuickBookHSNEnum (final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

    }
