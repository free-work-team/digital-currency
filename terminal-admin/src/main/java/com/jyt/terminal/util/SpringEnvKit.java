/**
 * 
 */
package com.jyt.terminal.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 加载Spring application.yml 配置
 * 
 * @author lcl
 *
 */
@Component("springEnvKit")
public class SpringEnvKit {
	
	@Autowired
	private Environment env;

	/**
	 * 获取env配置
	 * 
	 * @param key
	 * @return
	 */
	public String getEnv(String key) {
		return env.getProperty(key);
	}
}
