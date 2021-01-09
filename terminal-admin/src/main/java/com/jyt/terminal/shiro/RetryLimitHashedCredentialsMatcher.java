package com.jyt.terminal.shiro;  
  
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import com.jyt.terminal.shiro.cache.CacheKit;

public class RetryLimitHashedCredentialsMatcher extends  
        HashedCredentialsMatcher {  
  
  
	private String passwordRetryCache ="passwordRetryCache";
	
    @Override  
    public boolean doCredentialsMatch(AuthenticationToken token,  
            AuthenticationInfo info) {  
    	String username = (String) token.getPrincipal();  
        // retry count + 1  
        AtomicInteger retryCount =  CacheKit.get(passwordRetryCache,username);  
        if (retryCount == null) {  
            retryCount = new AtomicInteger(0);  
            CacheKit.put(passwordRetryCache,username, retryCount);  
        }  
        if (retryCount.incrementAndGet() > 3) {  
        	CacheKit.remove(passwordRetryCache,username);  
            throw new ExcessiveAttemptsException();
        }  
  
        boolean matches = super.doCredentialsMatch(token, info);  
        if (matches) {  
            // clear retry count 
        	CacheKit.remove(passwordRetryCache,username);  
        }  
        return matches;  
    }  
}  