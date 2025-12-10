package com.archy.dezhou.entity;

public class ResponseDto {
    private boolean success = true;
    private String errorMessage;
    private Object data;
    private String timestamp;

    public ResponseDto() {
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}