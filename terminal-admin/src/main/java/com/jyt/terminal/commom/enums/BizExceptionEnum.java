package com.jyt.terminal.commom.enums;


/**
 * @author fengshuonan
 * @Description 所有业务异常的枚举
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum implements ServiceExceptionEnum {

	SUCCESS(000,"SUCCESS"),//操作成功
	FAIL(100,"FAIL"),//操作失败
	
    /**
     * 字典
     */
    DICT_EXISTED(400, "DICTIONARY_ALREADY_EXISTS"),//字典已经存在
    ERROR_CREATE_DICT(500, "FAILED_TO_CREATE_DICTIONARY"),//创建字典失败
    /**
    ERROR_WRAPPER_FIELD(500, "包装字典属性失败"),//包装字典属性失败
    ERROR_CODE_EMPTY(500, "字典类型不能为空"),//字典类型不能为空
     */    
    WRITE_ERROR(500, "WRITE_ERROR"),//渲染界面错误
    
    INVLIDE_DATE_STRING(400, "INPUT_DATE_FORMAT_IS_INCORRECT"),//输入日期格式不对
    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),//
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),//
    UPLOAD_ERROR(500, "UPLOAD_ERROR"),//上传文件出错
    DOWNLOAD_ERROR(500, "DOWNLOAD_ERROR"),//下载文件出错
    UPLOAD_SIZE_OVER(500,"UPLOAD_SIZE_OVER"),//上传文件大小超限

    /**
     * 权限和数据问题
     */
    DB_RESOURCE_NULL(400, "DB_RESOURCE_NULL"),//数据库中没有该资源
    NO_PERMITION(405, "USER_DOES_NOT_HAVE_PERMISSION"),//该用户没有此权限
    REQUEST_INVALIDATE(400, "REQUEST_DATA_FORMAT_IS_INCORRECT"),//请求数据格式不正确
    INVALID_KAPTCHA(400, "VERIFICATION_CODE_ERROR"),//验证码不正确
    CANT_DELETE_ADMIN(600, "ADMINISTRATOR_CANNOT_DELETE"),//不能删除超级管理员
    CANT_FREEZE_ADMIN(600, "ADMINISTRATOR_CANNOT_FREEZE"),//不能冻结超级管理员
    CANT_CHANGE_ADMIN(600, "ADMINISTRATOR_ROLE_CANNOT_BE_MODIFIED"),//不能修改超级管理员角色

    /**
     * 账户问题
     */
    USER_ALREADY_REG(401, "USER_HAS_ALREADY_REGISTERED"),//该用户已经注册
    NO_THIS_USER(400, "THIS_USER_DOES_NOT_EXIST"),//没有此用户
    USER_NOT_EXISTED(400, "THIS_USER_DOES_NOT_EXIST"),//没有此用户
    ACCOUNT_FREEZED(401, "ACCOUNT_IS_FROZEN"),//账号被冻结
    OLD_PWD_NOT_RIGHT(402, "OLD_PASSWORD_ERROR"),//原密码不正确
    TWO_PWD_NOT_MATCH(405, "CANNOT_BE_THE_SAME_AS_THE_OLD_PASSWORD"),//新旧密码不能一致
    CUST_IS_EXIT(406, "THIS_CUSTOMER_INFORMATION_HAS_BEEN_CERTIFIED"),//该客户信息已认证
    USER_ACCOUNT_USE(401, "ACCOUNT_IS_ALREADY_IN_USE"),//该登录名已被使用或系统异常
    
    /**
     * 错误的请求
     */
    CHECK_CODE_NOT_MATCH(405, "CODE_ERROR"),//验证码错误
    /*MENU_PCODE_COINCIDENCE(400, "菜单编号和副编号不能一致"),//菜单编号和副编号不能一致
    EXISTED_THE_MENU(400, "菜单编号重复，不能添加"),//菜单编号重复，不能添加
    DICT_MUST_BE_NUMBER(400, "字典的值必须为数字"),//字典的值必须为数字
     */    
    REQUEST_NULL(400, "REQUEST_PARAMETER_IS_INCORRECT"),//请求参数有误
    /*SESSION_TIMEOUT(400, "会话超时"),//会话超时
    APPLY_CHECK_CODE(400, "申请验证码错误"),//申请验证码错误
    NOT_INTENTION_CUSTOMER(402,"非意向客户"),//非意向客户
     */    
	SERVER_ERROR(500, "SERVER_ERROR"),//服务器异常
    REFRESH_ERROR(500, "SYNC_FAILED, PLEASE_TRY_AGAIN"),//同步失败，请重试

    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "TOKEN_EXPIRED"),//token失效
    TOKEN_ERROR(700, "TOKEN_ERROR"),//token验证失败

    /**
     * 签名异常
     */
    SIGN_ERROR(800, "SIGN_ERROR"),//签名验证失败

    /**
     * 其他
     */
    AUTH_REQUEST_ERROR(400, "ACCOUNT_OR_PASSWORD_ERROR"),//账号密码错误
    AUTH_PWD_ERROR(400, "PASSWORD_ERROR"),//密码错误
    INFO_IS_EXIST(400,"INFORMATION_ALREADY_EXISTS"),//信息已存在
	
	/**
	 * 系统配置
	 */
    SYS_CONFIG_ERROR_ENCRYPT_KEY_MISSED(110, "DECRYPTION_KEY_DOES_NOT_EXIST"),//解密秘钥不存在
    SYS_CONFIG_ERROR_ENCRYPT_KEY_PARSE_ERROR(111, "DECRYPTION_KEY_FILE_PARSING_ERROR"),//解密秘钥文件解析错误
    SYS_CONFIG_ERROR_ENCRYPT_KEY_NO_MD5_KEY(112, "INVALID_ENCRYPTED_FILE_SIGNATURE"),//无效加密文件签名
    
    /**
     * 
     */
    NO_PARAM_SETTING(400,"THE_TERMINAL_DOES_NOT_SET_PARAMTERS"),//没有该终端机设置参数
    PARAM_SETTING_IS_SEND(200,"TERMINAL_PARAMETER_SETTINGS_ARE_NOT_UPDATEED"),//终端机参数设置未更新
    UPDATE_IS_SEND_FAIL(500,"UPDATE_SEND_STATUS_FAILED"),//更新发送状态失败
    UPLOAD_NO_PARAM(400,"UPLOAD_DATA_IS_INCOMPLETE"),//上传数据不完整
    /**
     * KYC
     */
    INFO_NOT_EXISTED(400,"INFO_NOT_EXISTED"),//信息不存在
    STATUS_NOT_ILLEGAL(400,"STATUS_NOT_ILLEGAL"),//状态不可用
    
    ENCRYPTION_FAILED(500,"Channel_parameter_encryption_failed"),//渠道参数加密失败
    ORDER_INFO_NULL(500,"Order_information_does_not_exist"),//订单信息不存在
    ;
    
    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
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
