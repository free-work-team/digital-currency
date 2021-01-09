package com.jyt.terminal.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


// 线程配置类
@Configuration
@EnableAsync 
public class AsyncTaskConfig {

	@Bean(name = "emailThreadPool")
	public ThreadPoolTaskExecutor emailThreadPool() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(10000);
		executor.setKeepAliveSeconds(60);
		executor.setThreadNamePrefix("Pool-email-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;

	}
}