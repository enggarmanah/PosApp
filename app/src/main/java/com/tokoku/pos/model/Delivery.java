package com.tokoku.pos.model;

import java.util.Date;

/**
 * Created by Radix on 23/3/2018.
 */

public class Delivery {

    private String no;
    private Date date;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
