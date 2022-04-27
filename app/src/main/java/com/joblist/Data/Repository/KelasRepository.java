package com.joblist.Data.Repository;

import com.joblist.Data.Model.Kelas;

import java.util.List;

public class KelasRepository {
    String value;
    String message;
    List<Kelas> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Kelas> getResult() {
        return result;
    }
}
