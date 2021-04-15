package com.hpx.humanpowerexchange.restapi.dto;

public class ServiceRequestDto {
    private int id;
    private int serviceId;
    private int consumerId;
    private int providerId;
    private String description;
    private String audio;
    private String images;
    private String log;
    private int status;
    private String createdAt;
    private String acceptedAt;
    private String completedAt;
    private String modifiedAt;

    public ServiceRequestDto() {
    }

    public ServiceRequestDto(int id, int serviceId, int consumerId, int providerId, String description, String audio, String images, String log, int status, String createdAt, String acceptedAt, String completedAt, String modifiedAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.consumerId = consumerId;
        this.providerId = providerId;
        this.description = description;
        this.audio = audio;
        this.images = images;
        this.log = log;
        this.status = status;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.completedAt = completedAt;
        this.modifiedAt = modifiedAt;
    }

    public int getId() {
        return id;
    }

    public ServiceRequestDto setId(int id) {
        this.id = id;
        return this;
    }

    public int getServiceId() {
        return serviceId;
    }

    public ServiceRequestDto setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public ServiceRequestDto setConsumerId(int consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    public int getProviderId() {
        return providerId;
    }

    public ServiceRequestDto setProviderId(int providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ServiceRequestDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAudio() {
        return audio;
    }

    public ServiceRequestDto setAudio(String audio) {
        this.audio = audio;
        return this;
    }

    public String getImages() {
        return images;
    }

    public ServiceRequestDto setImages(String images) {
        this.images = images;
        return this;
    }

    public String getLog() {
        return log;
    }

    public ServiceRequestDto setLog(String log) {
        this.log = log;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ServiceRequestDto setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public ServiceRequestDto setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public ServiceRequestDto setAcceptedAt(String acceptedAt) {
        this.acceptedAt = acceptedAt;
        return this;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public ServiceRequestDto setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public ServiceRequestDto setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }
}
