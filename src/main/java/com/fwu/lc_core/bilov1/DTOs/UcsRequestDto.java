package com.fwu.lc_core.bilov1.DTOs;

public class UcsRequestDto {

    private String student;

    public UcsRequestDto() {
    }

    public UcsRequestDto(String student) {
        this.student = student;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

}
