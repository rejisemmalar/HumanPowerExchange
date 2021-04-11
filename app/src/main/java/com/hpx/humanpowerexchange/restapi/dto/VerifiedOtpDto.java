package com.hpx.humanpowerexchange.restapi.dto;

public class VerifiedOtpDto {
    private boolean otpVerified;
    private int userPage;

    public VerifiedOtpDto() {
    }

    public VerifiedOtpDto(boolean verified, int userStatus) {
        this.otpVerified = verified;
        this.userPage = userStatus;
    }

    public boolean isOtpVerified() {
        return otpVerified;
    }

    public VerifiedOtpDto setOtpVerified(boolean otpVerified) {
        this.otpVerified = otpVerified;
        return this;
    }

    public int getUserPage() {
        return userPage;
    }

    public VerifiedOtpDto setUserPage(int userPage) {
        this.userPage = userPage;
        return this;
    }
}
