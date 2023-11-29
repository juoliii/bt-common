package com.bitian.common.exception;


import com.bitian.common.dto.ResultJson;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

	private ResultJson resultJson;

    public CustomException(ResultJson resultJson) {
		super(resultJson.getMsg());
		this.resultJson = resultJson;
    }

	public CustomException(String msg) {
		super(msg);
		this.resultJson = ResultJson.failure(msg);
	}
    
}
