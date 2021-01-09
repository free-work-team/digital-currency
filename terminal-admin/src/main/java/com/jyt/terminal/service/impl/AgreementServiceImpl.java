package com.jyt.terminal.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.AgreementMapper;
import com.jyt.terminal.model.Agreement;
import com.jyt.terminal.service.IAgreementService;

@Service
public class AgreementServiceImpl extends ServiceImpl<AgreementMapper, Agreement> implements  IAgreementService{
	
}
