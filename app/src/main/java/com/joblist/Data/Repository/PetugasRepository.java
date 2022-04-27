package com.joblist.Data.Repository;

import com.joblist.Data.Model.Petugas;

import java.util.List;

public class PetugasRepository {
    String value;
    String message;
    List<Petugas> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Petugas> getResult() {
        return result;
    }
}
