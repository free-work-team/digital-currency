package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.QueryTermDTO;
import com.jyt.terminal.model.TerminalSetting;


/**
 * <p>
 * 终端机设置参数 Mapper 接口
 * </p>
 *
 * @author sunshubin
 * @since 2019-05-05
 */
public interface TerminalSettingMapper extends BaseMapper<TerminalSetting> {

	/**
     * 通过账号获取用户
     */
	TerminalSetting getByTermNo(@Param("terminalNo") String terminalNo);
    
    /**
     * 修改密码
     */
    int changePwd(@Param("terminalNo") String terminalNo, @Param("pwd") String pwd);
    
    
    List<Map<String, Object>> getTermList(@Param("page")Page<Map<String, Object>> page,@Param("term")QueryTermDTO queryTermDTO);
    
    int getTermListCount(@Param("term")QueryTermDTO queryTermDTO);

	int setStatus(@Param("termId")Integer termId, @Param("status")int status);
	
	boolean isSend(@Param("id")Integer id, @Param("isSend")int isSend);
	/**
	 * 批次更新终端参数下发状态
	 */
	Integer updateSendStatus(@Param("list")List<Integer> termIds,@Param("status")Integer status);
}