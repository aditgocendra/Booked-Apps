package com.ark.bookedapps.Utility;

import java.util.HashMap;

public class Constant {

    public static String ROLE_USER = "-";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMessageHeaders = null;
    public static HashMap<String, String> getRemoteMessageHeaders(){
        if (remoteMessageHeaders == null){
            remoteMessageHeaders = new HashMap<>();
            remoteMessageHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAahTVeQ0:APA91bE1_n5PnRLQsOezwJIuxO-UBbXz1CgPnzoN31RSivW5tWmcdl_TGK1KMWAHaA8FK27XofsTF_vmo-tBiPoqHmrLblLNZpsI_U9sv6Ivo79L5ifieNn5ouR7CvYG-EmFz5IbIYKH"
            );
            remoteMessageHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMessageHeaders;
    }

}
