package com.hpx.humanpowerexchange.restapi.dto;

public class UserDto {
    private int id;
    private String name;
    private String mobile;
    private String email;
    private String apikey;
    private boolean detail_completed;
    private boolean verified;
    private int user_page;

    private String address_line_1;
    private String address_line_2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private String created_at;
    private String modified_at;

    public UserDto() {
    }

    public UserDto(int id) {
        this.id = id;
    }

    public UserDto(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public UserDto setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public UserDto setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getApikey() {
        return apikey;
    }

    public UserDto setApikey(String apikey) {
        this.apikey = apikey;
        return this;
    }

    public boolean isDetail_completed() {
        return detail_completed;
    }

    public UserDto setDetail_completed(boolean detail_completed) {
        this.detail_completed = detail_completed;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public UserDto setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    public int getUser_page() {
        return user_page;
    }

    public UserDto setUser_page(int user_page) {
        this.user_page = user_page;
        return this;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public UserDto setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
        return this;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public UserDto setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public UserDto setState(String state) {
        this.state = state;
        return this;
    }

    public String getPincode() {
        return pincode;
    }

    public UserDto setPincode(String pincode) {
        this.pincode = pincode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserDto setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public UserDto setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public String getModified_at() {
        return modified_at;
    }

    public UserDto setModified_at(String modified_at) {
        this.modified_at = modified_at;
        return this;
    }
}
