package com.jyt.terminal.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.LanguageMapper;

import com.jyt.terminal.dto.LanguageQuery;
import com.jyt.terminal.model.Language;
import com.jyt.terminal.service.ILanguageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 多语言
 *
 * @author hcg
 * @Date 2019-6-25
 */
@Service
public class LanguageServiceImpl extends ServiceImpl<LanguageMapper, Language> implements ILanguageService {
    @Resource
    private LanguageMapper languageMapper;

    @Override
    public List<Language> getLanguageList(Page<Language> page, LanguageQuery languageQuery) {
        return languageMapper.getLanguageList(page, languageQuery);
    }

    @Override
    public Integer getCount(LanguageQuery languageQuery) {
        return languageMapper.getCount(languageQuery);

    }

    @Override
    public List<Language> getAllList() {
        return languageMapper.selectALL();
    }

    @Override
    public Integer updateLanguage(Language language) {
        return languageMapper.updateById(language);
    }

    @Override
    public Language selectLanguageById(String lanId) {
        return languageMapper.selectById(lanId);
    }
}
