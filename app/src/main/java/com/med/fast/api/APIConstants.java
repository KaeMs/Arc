package com.med.fast.api;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class APIConstants {
    public static final String API_URL = "http://api.fastmedrecord.com";

    // Login
    public static final String LOGIN = "/login/loginsubmit";

    // Allergy
    public static final String ALLERGY_INIT_SHOW = "/allergyinit/initallergylistshow";
    public static final String ALLERGY_INIT_SUBMIT = "/allergyinit/initallergycreatesubmit";
    public static final String ALLERGY_CREATE_SUBMIT = "/allergy/allergycreatesubmit";
    public static final String ALLERGY_EDIT_SHOW = "/medicine/allergyeditshow";
    public static final String ALLERGY_EDIT_SUBMIT = "/medicine/allergyeditsubmit";
    public static final String ALLERGY_DELETE_SUBMIT = "/medicine/allergydeletesubmit";
    // Disease
    public static final String DISEASE_INIT_SHOW = "/diseaseinit/initdiseaselistshow";
    public static final String DISEASE_INIT_SUBMIT = "/diseaseinit/initdiseaselistshow";
    public static final String DISEASE_CREATE_SUBMIT = "/allergy/allergycreatesubmit";
    public static final String DISEASE_EDIT_SHOW = "/medicine/diseaseeditshow";
    public static final String DISEASE_EDIT_SUBMIT = "/medicine/diseaseeditsubmit";
    public static final String DISEASE_DELETE_SUBMIT = "/medicine/diseasedeletesubmit";
    // Medicine
    public static final String MEDICINE_INIT_SHOW = "/medicineinit/initmedicinelitshow";
    public static final String MEDICINE_INIT_SUBMIT = "/medicineinit/initmedicinecreatesubmit";
    public static final String MEDICINE_SHOW = "/medicine/medicinelistshow";
    public static final String MEDICINE_CREATE_SUBMIT = "/medicine/medicinecreatesubmit";
    public static final String MEDICINE_EDIT_SHOW = "/medicine/medicineeditshow";
    public static final String MEDICINE_EDIT_SUBMIT = "/medicine/medicineeditsubmit";
    public static final String MEDICINE_DELETE_SUBMIT = "/medicine/medicinedeletesubmit";

    // Misc
    public static final String MISC_SHOW = "/misc/miscshow";
    public static final String MISC_SUBMIT = "/misc/misceditsubmit";

    public static final int connectTimeout = 15;
    public static final int writeTimeout = 15;
    public static final int readTimeout = 30;
    public static final String CONNECTION_ERROR = "Connection error";
    public static final String MODE_EDIT = "read";
    public static final String MODE_DELETE = "delete";
    public static final String DEFAULT = "default";
    public static final String PROGRESS_NORMAL = "0";
    public static final String PROGRESS_ADD = "1";
    public static final String PROGRESS_DELETE = "2";
    public static final String PROGRESS_ADD_FAIL = "3";
}
