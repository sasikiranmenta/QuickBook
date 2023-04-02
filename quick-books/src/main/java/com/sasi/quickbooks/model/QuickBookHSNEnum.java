package com.sasi.quickbooks.model;

public enum QuickBookHSNEnum {
    GOLD(7113, "GOLD"),
    SILVER(7114, "SILVER");

    private final int value;
    private final String name;

    QuickBookHSNEnum (final int newValue, final String newName) {
        value = newValue;
        name = newName;
    }

    public int getValue() {
        return value;
    }
    public String getName() {return name;}
    }
