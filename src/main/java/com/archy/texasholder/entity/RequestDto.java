package com.archy.texasholder.entity;

import lombok.Data;

@Data
public class RequestDto {

    private String fn;

    private String tm;

    private String ver;

    private String token;

    private Object data;


    @Override
    public String toString() {
        return "RequestDto{" +
                "fn='" + fn + '\'' +
                ", tm='" + tm + '\'' +
                ", ver='" + ver + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                '}';
    }
}
