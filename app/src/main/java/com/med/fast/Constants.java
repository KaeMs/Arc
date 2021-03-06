package com.med.fast;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class Constants {
    public static final String MESSAGE = "MESSAGE";
    public static final String FORCE_LOGOUT = "FORCE_LOGOUT";

    public static final String dateFormatSlash = "MMM/dd/yyyy";
    public static final String dateFormatSpace = "MMM dd yyyy";
    public static final String dateFormatComma = "MMM dd, yyyy";
    public static final String dateFormatPicture = "MMMddyyyyHHmmss";

    public static final String FLAG_LOAD = "load";
    public static final String FLAG_REFRESH = "refresh";

    public static final String REGEX_NAME = "^[a-zA-Z]+$";
    public static final String REGEX_PASSWORD = "[^-\\s]{6,50}"; // No whitespace, min 8 max 50

    public static final int scrollTopTime = 75;
    public static final String MANAGER_STATE = "manager_state";

    public static final String WEB_ADDRESS_BLANK = "about:blank";
}
