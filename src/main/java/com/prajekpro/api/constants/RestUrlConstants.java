package com.prajekpro.api.constants;

public class RestUrlConstants {

    /**
     * pp = Prajekpro
     */

    //Common Base Url
    public static final String PP_BASE_URL = "/api/pp";

    //Controller url
    public static final String PP_PUBLIC = PP_BASE_URL + "/public";
    public static final String PP_COMMON = PP_BASE_URL + "/common";
    public static final String PP_APPOINTMENT = PP_BASE_URL + "/appointments";
    public static final String PP_PAYMENT = PP_BASE_URL + "/payments";
    public static final String PP_LOCATION_DATA = PP_BASE_URL + "/location";
    public static final String PP_USERS = PP_BASE_URL + "/users";
    public static final String PP_MASTER = PP_BASE_URL + "/master";
    public static final String PP_ADMIN_MASTER = PP_BASE_URL + "/admin/master";
    public static final String PP_WALLET = PP_BASE_URL + "/wallet";
    public static final String PP_CHAT = PP_BASE_URL + "/chat";
    public static final String PP_SUBSCRIPTION = PP_BASE_URL + "/subscription";
    public static final String PP_PRO = PP_BASE_URL + "/pro";
    public static final String PP_ADMIN_CUSTOMER = PP_BASE_URL + "/customer";
    public static final String PP_PRO_SERVICES = PP_BASE_URL + "/proServices";
    public static final String PP_SEARCH = PP_BASE_URL + "/search";
    public static final String PP_REPORTS = PP_BASE_URL + "/reports";
    public static final String PP_SOCIAL_HANDLES = "/social-handles";

    //General URL
    public static final String PING = "/ping";
    public static final String PP_UPLOAD_IMAGE_DOCUMENTS = "/uploadImageDocuments/{id}";

    //User Authorization & Authentication URL's
    public static final String LOGIN = "/login";
    public static final String PP_FORGOT_PASSWORD = "/forgot-password";
    public static final String PP_RESET_PASSWORD = "/reset-password";
    public static final String PP_SEND_OTP = "/send-otp";
    public static final String PP_VERIFY_USER = "/verify-user";
    public static final String PP_VERIFY_OTP = "/verify-otp";
    public static final String PP_VERIFY_USER_OTP = "/verify-user-otp";
    public static final String PP_CHANGE_PASSWORD = "change-password";
    public static final String PP_USER_INFO = "/user-info";

    //Customer Registration
    public static final String PP_REGISTER = "/register";
    public static final String PP_SOCIAL_HANDLES_GOOGLE_HANDLE = PP_SOCIAL_HANDLES + "/google-handle";
    public static final String PP_SOCIAL_HANDLES_FACEBOOK_HANDLE = PP_SOCIAL_HANDLES + "/facebook-handle";


    //Basic Details
    public static final String PP_REGISTER_PRO = "/register/pro";
    public static final String PP_PRO_DETAILS = "/register/pro/{pro-id}";

    //PRO Documents
    public static final String PP_REGISTER_PRO_DOC_UPLOAD = "/register/pro/{pro-id}/upload_documents";
    public static final String PP_REGISTER_PRO_DOC = "/register/pro/{pro-id}/documents";

    //PRO Profile Image
    public static final String PP_REGISTER_PRO_PROFILE = "/register/pro/{pro-id}/profile-image";
    public static final String PP_DOWNLOAD_PRO_PROFILE_IMAGE = "/register/pro/{pro-id}/profile-image";

    //PRO Service Location
    public static final String PP_PRO_LOCATION = "/register/pro/{pro-id}/location";

    //PRO Subscription
    public static final String PP_ADD_SUBSCRIPTION = "{pro-id}/subscription/{subscription-id}";
    public static final String PP_SUBSCRIPTION_CURRENT = "/current";
    public static final String PP_SUBSCRIPTION_RENEW = "/renew";

    //PRO Services
    public static final String PP_REGISTER_PRO_SERVICE = "/register/pro/{pro-id}/services";
    public static final String PP_REGISTER_DELETE_PRO_SERVICES = "/register/pro/{pro-id}/services/{serviceId}";
    public static final String PP_PRO_SERVICE_UPDATE = "/{pro-id}/services/{serviceId}";
    public static final String PP_PRO_SERVICE_COMMENT = PP_PRO_SERVICE_UPDATE + "/comment";
    public static final String PP_PRO_SERVICE_UNAVAILABLE_DATES = PP_PRO_SERVICE_UPDATE + "/unavailable-dates";
    public static final String PP_PRO_SERVICE_UNAVAILABLE_DATES_DELETE = PP_PRO_SERVICE_UPDATE + "/unavailable-dates/{unavailableDateId}";

    //Customer Home Screen
    public static final String PP_CUSTOMER_HOME_DETAILS = "/customer/home";

    //Masters
    public static final String PP_SERVICES = "/services";

    //Customer PRO List
    public static final String PP_SERVICE_PRO_LIST = PP_SERVICES + "/{service-id}/pro";

    //Customer PRO Details
    public static final String PP_SERVICE_PRO_DETAILS = PP_SERVICE_PRO_LIST + "/{pro-id}";

    //Customer PRO Time Slots
    public static final String PP_APPOINTMENT_TIME_SLOTS = "/time-slots";

    //Master URL's
    public static final String PP_MASTER_SERVICES = "/services/{source}";
    public static final String PP_MASTER_SERVICES_BY_ID = PP_MASTER_SERVICES + "/{id}";
    public static final String PP_MASTER_SERVICES_CATEGORY = PP_MASTER_SERVICES_BY_ID + "/categories";
    public static final String PP_MASTER_SERVICES_CATEGORY_BY_ID = PP_MASTER_SERVICES_CATEGORY + "/{categoryId}";
    public static final String PP_MASTER_SERVICES_SUBCATEGORY = PP_MASTER_SERVICES_CATEGORY_BY_ID + "/subcategories";
    public static final String PP_MASTER_SERVICES_SUBCATEGORY_BY_ID = PP_MASTER_SERVICES_SUBCATEGORY + "/{subCategoryId}";
    public static final String PP_MASTER_TIMESLOTS = "/timeSlots";
    public static final String PP_MASTER_TIMESLOT_BY_ID = PP_MASTER_TIMESLOTS + "/{timeSlotId}";
    public static final String PP_MASTER_ADVERTISE = "/advertise";
    public static final String PP_MASTER_ADVERTISE_BY_ID = PP_MASTER_ADVERTISE + "/{advertiseId}";
    public static final String PP_MASTER_CATEGORY_SUBCATEGORY_BY_SERVICE_ID = "/categorySubcategory/{source}/{id}";

    public static final String PP_CUSTOMER = "/customer";

    /*Appointment Constants */
    public static final String PP_LOOK_UP = "/lookup";

    public static final String PP_BOOKED_APPOINTMENT_DETAILS = "/{id}";
    public static final String PP_UPDATE_APPOINTMENT_STATE = "{appointmentId}/appointmentRequestedServices/{appointmentRequestedServiceId}/state/{state}";
    public static final String PP_RE_SCHEDULE_APPOINTMENT = "/{appointmentId}";
    public static final String PP_PRO_APPOINTMENT_LIST = "/proAppointmentList/{serviceId}";
    public static final String PP_UPLOAD_CUSTOMER_SIGN = "/{appointmentId}/uploadSign";
    public static final String PP_DOWNLOAD_CUSTOMER_SIGN = "/{appointmentId}/downloadSign";
    public static final String PP_APPOINTMENT_INVOICE = "/{appointmentId}/generateInvoice";
    public static final String PP_SEND_INVOICE = "/{appointmentId}/sendInvoice";
    public static final String PP_PRO_APPOINTMENT_DETAILS_LIST = "/proAppointmentDetailsList/{serviceId}";
    public static final String PP_APPOINTMENT_ADD_PARTICULAR = "/addParticular";
    public static final String PP_APPOINTMENT_ADDRESS = "/address";
    public static final String PP_APPOINTMENT_DELETE_ADDRESS = PP_APPOINTMENT_ADDRESS + "/{addressId}";
    public static final String PP_PROJECTS_OVERVIEW = "/overview";
    public static final String PP_PRO_REVIEWS = "/reviews";


    public static final String PP_PUBLIC_ADD_ENQUIRY_DETAILS = "/addEnquiry";


    //wallet url
    public static final String PP_WALLET_TOPUPHISTORY = "/topUpHistory";


    //Chat module url
    public static final String PP_CHAT_MESSAGE = "/{chatThreadId}";
    public static final String PP_CHAT_THREAD = "/thread/{userId}";
    public static final String PP_CHAT_MESSAGE_DOCUMENT = PP_CHAT_MESSAGE + "/document";


    public static final String PP_LOGOUT = "/logout/{deviceId}";
    public static final String PP_NOTIFICATIONS = "/notification";
    public static final String PP_CLEARALL_NOTIFICATION = "/clearNotifications";
    public static final String PP_CLEAR_NOTIFICATION = "/clearNotificationById";

    public static final String PP_SUBSCRIPTION_MASTER = "/subscriptions";


    public static final String PP_USERS_REGISTRATION = "/registerUser";

    public static final String PP_DASHBOARD = "/dashboard";
    public static final String PP_APPOINTMENT_SEARCH = "/search";

    //pro management url
    public static final String PP_PRO_LIST = "/proList";
    public static final String PP_ADMIN_PRO_DETAILS = "/{proId}/proDetails";
    public static final String PP_ADMIN_PRO_APPOINTMENTS = "/{proId}/proAppointments";
    public static final String PP_ADMIN_PRO_APPOINTMENTS_HISTORY = "/{proId}/proAppointmentsHistory";
    public static final String PP_ADMIN_PRO_REVIEWS = "/{proId}/reviews";
    public static final String PP_ADMIN_PRO_DEACTIVATE = "/{proId}";
    public static final String PP_STORE_REVIEWS = "/reviews";

    //customer management url
    public static final String PP_ADMIN_CUSTOMER_DEACTIVATE = "/{userId}";
    public static final String PP_ADMIN_CUSTOMER_APPOINTMENTS_HISTORY = PP_ADMIN_CUSTOMER_DEACTIVATE;
    public static final String PP_ADMIN_CUSTOMER_REVIEWS = "/{userId}/reviews";
    public static final String PP_ADMIN_CUSTOMER_APPOINTMENTS = "/{userId}/customerAppointments";
    public static final String PP_ADMIN_CUSTOMER_DETAILS = "/{userId}/customerDetails";

    //appointment management url
    public static final String PP_ADMIN_APPOINTMENTS = "/adminList";

    // pro service Schedule
    public static final String PP_PRO_SERVICE_SCHEDULE = "/{serviceId}";

    public static final String PP_PRO_SERVICE_SUBCATEGORY_PRICING = "/{serviceId}/subCategoryPrice";

    public static final String PP_PRO_AVAILABILITY = "/availability/{status}";
    public static final String PP_ADVERTISEMENT_TYPE = "/advertisementType";
    public static final String PP_DOWNLOAD_ADVERTISEMENT_IMAGE = "/advertisementImage/{imageId}";

    public static final String PP_CURRENCY = "/currency";
    public static final String PP_PRO_CANCELLATION_TIME = "/proCancellationTime";
    public static final String PP_PAYMENT_GCASH = "/gcash";

    public static final String PP_DOWNLOAD_IMAGE = "/downloadImage/{id}";
    public static final String PP_PRAJEKPRO_WALLET_HISTORY = "/prajekPro";
    public static final String PP_PAYMENT_REDIRECTRESULT = "/redirectResult";
    public static final String PP_PAYMENT_CHECKOUTURL = "/checkOutUrl/{transactionId}";
    public static final String PP_PRO_SUBSCRIPTION = "/subscriptionDetails/{proId}";
    public static final String PP_APPOINTMENT_PAYMENT = "/payment/{appointmentId}";
    public static final String PP_CANCEL_APPOINTMENT = "/cancel/{appointmentId}";
    public static final String PP_PAYMENT_RESULTS = "/results";

    //Coupon API Endpoints
    public static final String PP_COUPONS = "/{proId}/coupons";
    public static final String PP_COUPON_DETAILS = PP_COUPONS + "/{couponCode}";

    //Static Content Endpoints
    public static final String PP_STATIC_CONTENT = "/static/content";

    //Timestamp
    public static final String NOW = "/now";
}