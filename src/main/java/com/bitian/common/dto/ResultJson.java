package com.bitian.common.dto;

import com.bitian.common.enums.ResultCode;
import com.bitian.common.exception.CustomException;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    public static <T> ResultJson<T> success(T o,String msg) {
        return new ResultJson(ResultCode.SUCCESS, o).msg(msg);
    }

    public static <T> ResultJson<T> failure(ResultCode code) {
        return failure(code, null);
    }

    public static <T> ResultJson<T> failure(ResultCode code, T o) {
        return new ResultJson(code, o);
    }
    
    public static <T> ResultJson<T> failure(String msg) {
        return failure(ResultCode.SERVER_ERROR, null).msg(msg);
    }

    public ResultJson(ResultCode resultCode) {
        setResultCode(resultCode);
    }

    public ResultJson (ResultCode resultCode,T data) {
        setResultCode(resultCode);
        this.data = data;
    }

    public ResultJson () {
        setResultCode(ResultCode.SUCCESS);
    }

    public ResultJson setResultCode(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        return this;
    }

	public ResultJson msg(String msg) {
		this.msg=msg;
		return this;
	}

    public ResultJson<T> data(T t){
        this.data=t;
        return this;
    }

    public static ResultJson<Map<String,Object>> mapData(){
        return ResultJson.success(new HashMap<>());
    }

    public ResultJson<Map<String,Object>> put(String key,Object value){
        if(this.data instanceof Map){
            ((Map<String, Object>) this.data).put(key,value);
            return (ResultJson<Map<String, Object>>) this;
        }else{
            throw new CustomException("data is not instanceof Map");
        }
    }

}
