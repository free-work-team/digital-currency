package com.jyt.terminal.util;

/**
 * 返回给前台的成功提示
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:05:22
 */
public class SuccessTip<T> extends Tip {
	public static int SUCCESS_CODE = 200;
	private T t;
	
	public SuccessTip(){
		super.code = 200;
		super.message = "操作成功";
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}
}
