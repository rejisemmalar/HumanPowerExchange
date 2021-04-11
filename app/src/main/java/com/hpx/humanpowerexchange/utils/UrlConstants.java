package com.hpx.humanpowerexchange.utils;

public class UrlConstants {

    public static final String SERVER_DOMAIN = "https://weeklydoor.com/venmani/hpx";

    public static final String SEND_OTP_URL = SERVER_DOMAIN + "/api/sms/create.php";
    public static final String VERIFY_OTP_URL = SERVER_DOMAIN + "/api/sms/verify.php";

    public static final String USER_CREATE_EDIT_URL = SERVER_DOMAIN + "/api/users";
    public static final String READ_USER_BY_ID = SERVER_DOMAIN + "/api/user/read_one.php";
    public static final String READ_USER_BY_MOBILE = SERVER_DOMAIN + "/api/user/read_by_mobile.php";


}
