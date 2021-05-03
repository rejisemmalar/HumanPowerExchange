package com.hpx.humanpowerexchange.restapi.dto;

public class ServiceProviderDto {
    private int id;
    private String name;
    private String imageUrl;
    private boolean selected;

    public ServiceProviderDto() {
    }

    public ServiceProviderDto(int id, String name, String imageUrl, boolean selected) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public ServiceProviderDto setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ServiceProviderDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ServiceProviderDto setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public ServiceProviderDto setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
