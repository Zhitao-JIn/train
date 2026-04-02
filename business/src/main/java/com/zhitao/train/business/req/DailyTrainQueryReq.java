package com.zhitao.train.business.req;

import com.zhitao.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

public class DailyTrainQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String code;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "date=" + date +
                ", code='" + code + '\'' +
                "} " + super.toString();
    }
}
