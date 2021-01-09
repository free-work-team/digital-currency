package com.jyt.terminal.exception;

import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

/**
 * 封装guns的异常
 *
 * @author fengshuonan
 * @Date 2017/12/28 下午10:32
 */
public class TerminalException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7936251207741613811L;

	private Integer code;

    private String message;
    
    public TerminalException(Integer code,String message) {
        this.code = code;
        this.message = message;
    }

    public TerminalException(ServiceExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.message = serviceExceptionEnum.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
