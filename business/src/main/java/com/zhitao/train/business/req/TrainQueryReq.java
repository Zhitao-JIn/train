package com.zhitao.train.business.req;

import com.zhitao.train.common.req.PageReq;

public class TrainQueryReq extends PageReq {

    @Override
    public String toString() {
        return "TrainQueryReq{" +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return getPage() == ((TrainQueryReq)obj).getPage()&&getSize() == ((TrainQueryReq)obj).getSize();
    }
}
