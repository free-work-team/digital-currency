package com.jyt.hardware.cashoutmoudle.enums;


public enum CodeMessageEnum {

    /**成功**/
	SUCCESS("00","Success"),
	
	/***失败**/
	FAIL("01","Fail"),
	
	/** 处理中 */
	PROCESSING("02","Porcessing"),

	/**系统异常**/
	SYSTEM_EXCEPTION("999","System Exception");

    /** code */
    private final String code;

    /** message */
    private final String message;

    /**
     * 私有构造方法
     * 
     * @param code
     * @param message
     */
    private CodeMessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
