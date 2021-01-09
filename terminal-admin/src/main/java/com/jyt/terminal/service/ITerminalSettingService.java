package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.QueryTermDTO;
import com.jyt.terminal.model.TerminalSetting;


/**
 * <p>
 * 终端机设置参数 服务类
 * </p>
 *
 * @author sunshubin
 * @since 2019-05-05
 */
public interface ITerminalSettingService extends IService<TerminalSetting> {

	/**
     * 验证用户名密码
     * @param account
     * @param password
     * @return
     */
    boolean validate(String terminalNo,String password);
    
    /**
     * 修改密码
     */
    int changePwd(String terminalNo, String oldPwd,String newPwd);
    
    /**
	 * 获取终端账号列表
	 */
	List<Map<String, Object>> getTermList(Page<Map<String, Object>> page,QueryTermDTO queryTermDTO);
	
	int  getTermListCount(QueryTermDTO queryTermDTO);
	
	/**
     * 修改终端账号状态
     */
    int setStatus(Integer userId,int status);
    
    TerminalSetting getByTermNo(String terminalNo);
    /**
	 * 批次更新终端参数下发状态
	 */
    Integer updateSendStatus(List<Integer> termIds,Integer status);
    
}
