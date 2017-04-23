package com.med.fast.summary;

import java.sql.Date;
import java.util.List;

/**
 * Created by kevin on 23-Apr-17.
 */

public class LabResultModel {
    public String lab_result_id ;
    public String user_id ;
    public String test_name ;
    public String desc_result ;
    public String img_path ;
    public Date date ;
    public String place ;
    public List<ListImgUploaded> list_img_uploadeds ;
    public class ListImgUploaded
    {
        public String img_id ;
        public String img_path ;
    }
}
