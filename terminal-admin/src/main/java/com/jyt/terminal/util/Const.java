package com.jyt.terminal.util;

/**
 * 系统常量
 *
 * @author fengshuonan
 * @date 2017年2月12日 下午9:42:53
 */
public interface Const {

    /**
     * 系统默认的管理员密码
     */
    String DEFAULT_PWD = "111111";

    /**
     * 管理员角色的名字
     */
    String ADMIN_NAME = "1";

    /**
     * 管理员id
     */
    Integer ADMIN_ID = 10;

    /**
     * 超级管理员角色id
     */
    Integer ADMIN_ROLE_ID = 10;

    /**
     * 接口文档的菜单名
     */
    String API_MENU_NAME = "接口文档";
    
    /**
     * OCR测试账号数据
     * */
    String TEST_USER_NAME="hw96742858";
	String TEST_PASS_WORD="tfq123456";
	String TEST_DOMAIN_NAME="hw96742858";
	//https://ocr.cn-north-4.myhuaweicloud.com/v1.0/ocr/passport
	String TEST_REGION_NAME_PASSPORT="cn-north-4";
	//https://ocr.cn-south-1.myhuaweicloud.com/v1.0/ocr/web-image
	String TEST_DOMAIN_NAME_WEB_IMAGE="cn-north-1";
	String TEST_HTTP_WEBIMAGE_URL = "/v1.0/ocr/web-image";
	
	/**
	 * OCR生产账号数据
	 */
	String PROD_USER_NAME="hw96742858";
	String PROD_PASS_WORD="tfq123456";
	String PROD_DOMAIN_NAME="hw96742858";
	//https://ocr.cn-north-4.myhuaweicloud.com/v1.0/ocr/passport
	String PROD_REGION_NAME_PASSPORT="cn-north-4";
	//https://ocr.cn-south-1.myhuaweicloud.com/v1.0/ocr/web-image
	String PROD_DOMAIN_NAME_WEBIMAGE="cn-north-1";
	String PROD_HTTP_WEBIMAGE_URL = "/v1.0/ocr/web-image";
	
}
