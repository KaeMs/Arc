package com.med.fast;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class Constants {
    public static final String dateFormatSlash = "MMM/dd/yyyy";
    public static final String dateFormatSpace = "MMM dd yyyy";
    public static final String dateFormatComma = "MMM dd, yyyy";
    public static final String dateFormatPicture = "MM dd, yyyy - HHmmss";

    public static final String FLAG_LOAD = "load";
    public static final String FLAG_REFRESH = "refresh";

    public static final String REGEX_NAME = "^[a-zA-Z]+$";
    public static final String REGEX_PASSWORD = "[^-\\s]{6,50}"; // No whitespace, min 8 max 50

}
