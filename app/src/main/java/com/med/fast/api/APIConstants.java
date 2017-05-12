package com.med.fast.api;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class APIConstants {
    public static final String API_URL = "http://api.fastmedrecord.com";
    public static final String WEB_URL = "http://www.fastmedrecord.com";

    // REGISTER
    public static final String REGISTER = "/register/registersubmit";
    public static final String REGISTER_EMAIL_VERIFY = "/register/emailverifysubmit";
    public static final String INITIAL_SKIP = "/init/skiptodashboardsubmit";

    // Token
    public static final String TOKEN = "/token";

    // Login
    public static final String LOGIN = "/login/loginsubmit";

    // Summary
    public static final String SUMMARY_SHOW = "/home/dashboardshow";
    // ID Card
    public static final String IDCARD_SHOW = "/cardid/cardidshow";
    public static final String IDCARD_SUBMIT = "/cardid/cardidsubmit";
    // Visit
    public static final String VISIT_SHOW = "/visit/visitlistshow";
    public static final String VISIT_CREATE_SHOW = "/visit/visitcreatesubmit";
    public static final String VISIT_CREATE_SUBMIT = "/visit/visitcreatesubmit";
    public static final String VISIT_EDIT_SHOW = "/visit/visiteditshow";
    public static final String VISIT_EDIT_SUBMIT = "/visit/visiteditsubmit";
    public static final String VISIT_DELETE_SUBMIT = "/visit/visitdeletesubmit";
    public static final int VISIT_INF_SCROLL = 20;
    // Surgery
    public static final String SURGERY_SHOW = "/surgery/surgerylistshow";
    public static final String SURGERY_CREATE_SUBMIT = "/surgery/surgerycreatesubmit";
    public static final String SURGERY_EDIT_SHOW = "/surgery/surgeryeditshow";
    public static final String SURGERY_EDIT_SUBMIT = "/surgery/surgeryeditsubmit";
    public static final String SURGERY_DELETE_SUBMIT = "/surgery/surgerydeletesubmit";
    public static final int SURGERY_INF_SCROLL = 20;
    // Allergy
    public static final String ALLERGY_INIT_SHOW = "/allergyinit/initallergylistshow";
    public static final String ALLERGY_INIT_SUBMIT = "/allergyinit/initallergycreatesubmit";
    public static final String ALLERGY_SHOW = "/allergy/allergylistshow";
    public static final String ALLERGY_CREATE_SUBMIT = "/allergy/allergycreatesubmit";
    public static final String ALLERGY_EDIT_SHOW = "/allergy/allergyeditshow";
    public static final String ALLERGY_EDIT_SUBMIT = "/allergy/allergyeditsubmit";
    public static final String ALLERGY_DELETE_SUBMIT = "/allergy/allergydeletesubmit";
    public static final int ALLERGY_INF_SCROLL = 20;
    // Disease
    public static final String DISEASE_INIT_SHOW = "/diseaseinit/initdiseaselistshow";
    public static final String DISEASE_INIT_SUBMIT = "/diseaseinit/initdiseaselistshow";
    public static final String DISEASE_SHOW = "/disease/diseaselistshow";
    public static final String DISEASE_CREATE_SUBMIT = "/disease/diseasecreatesubmit";
    public static final String DISEASE_EDIT_SHOW = "/disease/diseaseeditshow";
    public static final String DISEASE_EDIT_SUBMIT = "/disease/diseaseeditsubmit";
    public static final String DISEASE_DELETE_SUBMIT = "/disease/diseasedeletesubmit";
    public static final int DISEASE_INF_SCROLL = 20;
    // Medicine
    public static final String MEDICINE_INIT_SHOW = "/medicineinit/initmedicinelitshow";
    public static final String MEDICINE_INIT_SUBMIT = "/medicineinit/initmedicinecreatesubmit";
    public static final String MEDICINE_SHOW = "/medicine/medicinelistshow";
    public static final String MEDICINE_CREATE_SUBMIT = "/medicine/medicinecreatesubmit";
    public static final String MEDICINE_EDIT_SHOW = "/medicine/medicineeditshow";
    public static final String MEDICINE_EDIT_SUBMIT = "/medicine/medicineeditsubmit";
    public static final String MEDICINE_DELETE_SUBMIT = "/medicine/medicinedeletesubmit";
    public static final int MEDICINE_INF_SCROLL = 20;
    // Accident
    public static final String ACCIDENT_SHOW = "/accident/accidentlistshow";
    public static final String ACCIDENT_CREATE_SUBMIT = "/accident/accidentcreatesubmit";
    public static final String ACCIDENT_EDIT_SHOW = "/accident/accidenteditshow";
    public static final String ACCIDENT_EDIT_SUBMIT = "/accident/accidenteditsubmit";
    public static final String ACCIDENT_DELETE_SUBMIT = "/accident/accidentdeletesubmit";
    public static final int ACCIDENT_INF_SCROLL = 20;
    // Lab Result
    public static final String LABRESULT_SHOW = "/labresult/labresultlistshow";
    public static final String LABRESULT_CREATE_SUBMIT = "/labresult/labresultcreatesubmit";
    public static final String LABRESULT_EDIT_SHOW = "/labresult/labresulteditshow";
    public static final String LABRESULT_EDIT_SUBMIT = "/labresult/labresulteditsubmit";
    public static final String LABRESULT_DELETE_SUBMIT = "/labresult/labresultdeletesubmit";
    public static final String LABRESULT_DELETE_IMAGE_SUBMIT = "/labresult/medicinedeletesubmit";
    public static final int LAB_INF_SCROLL = 20;

    // Setting
    public static final String SETTING_SHOW = "/setting/settingshow";
    public static final String SETTING_SUBMIT = "/setting/settingsubmit";
    public static final String SETTING_IDCARD_SHOW = "/cardid/cardidshow";
    public static final String SETTING_IDCARD_SUBMIT = "/cardid/cardidsubmit";

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
    public static final String NO_ID = "no_id";
    public static final String PROGRESS_NORMAL = "0";
    public static final String PROGRESS_ADD = "1";
    public static final String PROGRESS_DELETE = "2";
    public static final String PROGRESS_ADD_FAIL = "3";
}
