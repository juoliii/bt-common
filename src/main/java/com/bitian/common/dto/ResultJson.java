package com.bitian.common.dto;

import com.bitian.common.enums.ResultCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultJson<T> implements Serializable{

    private int code;
    private String msg;
    private T data;

    public static <T> ResultJson<T> success() {
        return success(null);
    }

    public static <T> ResultJson<T> success(T o) {
        return new ResultJson(ResultCode.SUCCESS, o);
    }

    public static <T> ResultJson<T> failure(ResultCode code) {
        return failure(code, null);
    }
    
    public static <T> ResultJson<T> failure(String msg) {
        return failure(ResultCode.SERVER_ERROR, null).msg(msg);
    }

    public static <T> ResultJson<T> failure(ResultCode code, T o) {
        return new ResultJson(code, o);
    }

    public ResultJson (ResultCode resultCode) {
        setResultCode(resultCode);
    }

    public ResultJson (ResultCode resultCode,T data) {
        setResultCode(resultCode);
        this.data = data;
    }

    public void setResultCode(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

	public ResultJson msg(String msg) {
		this.msg=msg;
		return this;
	}

}
