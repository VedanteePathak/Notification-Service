package org.adrij.web.Constants;

public class APIEndPoints {
    public static final String SMS_REQUEST_END_POINT= "/v1/sms";
    public static final String SMS_REQUEST_POST = "/send";
    public static final String SMS_REQUEST_GET = "/{requestId}";
    public static final String BLACKLIST_END_POINT = "/v1/blacklist";
    public static final String BLACKLIST_BATCHES_END_POINT = "/batches";
    public static final String SEARCH_BY_REQUEST_ID_ENDPOINT = "/searchbyid/{requestId}";
    public static final String SEARCH_BY_TEXT_ENDPOINT = "/searchbytext/{text}";
    public static final String SEARCH_BY_PHONENUMBER_IN_DATERANGE_ENDPOINT = "/searchbyphonenumberindaterange";
}
