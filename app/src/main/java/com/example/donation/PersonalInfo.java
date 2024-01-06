package com.example.donation;

public class PersonalInfo {
    private String name;
    private String mobile;
    private String address;
    private String amount;
    private String type;

    public PersonalInfo(String name, String mobile, String address, String amount, String type) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.amount = amount;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
