package com.archy.dezhou.entity;

public class RequestDto {

    private String fn;

    private String tm;

    private String ver;

    private String token;

    private Object data;

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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
