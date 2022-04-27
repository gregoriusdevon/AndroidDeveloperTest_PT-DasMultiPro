package com.joblist.Data.Repository;

import com.joblist.Data.Model.SPP;

import java.util.List;

public class SPPRepository {
    String value;
    String message;
    List<SPP> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<SPP> getResult() {
        return result;
    }
}
