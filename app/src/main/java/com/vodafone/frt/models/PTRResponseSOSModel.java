package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by vishal on 9/12/17
 */
public class PTRResponseSOSModel implements Comparable<PTRResponseSOSModel> {
    private String sos_id, sos_desc, phone;

    public String getSos_desc() {
        return sos_desc;
    }

    public void setSos_desc(String sos_desc) {
        this.sos_desc = sos_desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSos_id() {
        return sos_id;
    }

    public void setSos_id(String sos_id) {
        this.sos_id = sos_id;
    }

    @Override
    public int compareTo(@NonNull PTRResponseSOSModel frtResponseSOSModel) {
        return getSos_desc().compareTo(frtResponseSOSModel.getSos_desc());
    }
}
