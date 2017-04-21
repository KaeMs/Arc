package com.med.fast.dashboard.visit;

import java.sql.Date;
import java.util.List;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitModel {
    public String visit_id;
    public String owner_id;
    public Date created_date;
    public String hospital_name;
    public String doctor_name;
    public String diagnose;
    public String disease;
    public List<VisitImageItem> image_list;
}
