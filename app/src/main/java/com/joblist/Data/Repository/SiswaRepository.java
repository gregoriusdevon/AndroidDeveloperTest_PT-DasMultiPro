package com.joblist.Data.Repository;

import com.joblist.Data.Model.Siswa;

import java.util.List;

public class SiswaRepository {
    String value;
    String message;
    List<Siswa> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Siswa> getResult() {
        return result;
    }
}
