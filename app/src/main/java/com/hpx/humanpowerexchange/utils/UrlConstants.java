package com.hpx.humanpowerexchange.utils;

public class UrlConstants {

    public static final String SERVER_DOMAIN = "https://weeklydoor.com/venmani/hpx";

    public static final String SEND_OTP_URL = SERVER_DOMAIN + "/api/sms/create.php";
    public static final String VERIFY_OTP_URL = SERVER_DOMAIN + "/api/sms/verify.php";

    public static final String USER_CREATE_EDIT_URL = SERVER_DOMAIN + "/api/users";
    public static final String READ_USER_BY_ID = SERVER_DOMAIN + "/api/user/read_one.php";
    public static final String READ_USER_BY_MOBILE = SERVER_DOMAIN + "/api/user/read_by_mobile.php";
    public static final String EDIT_USER = SERVER_DOMAIN + "/api/user/edit.php";


    public static final String SERVICES_FOR_USER = SERVER_DOMAIN + "/api/service/read_for_user.php";
    public static final String SAVE_USER_SERVICES = SERVER_DOMAIN + "/api/user-service/update.php";
    public static final String UPDATE_USER_PAGE = SERVER_DOMAIN + "/api/user/update_user_page.php";

    public static final String READ_SERVICE_REQUEST = SERVER_DOMAIN + "/api/service-request/read.php";





}
