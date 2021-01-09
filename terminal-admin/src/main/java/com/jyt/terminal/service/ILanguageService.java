package com.jyt.terminal.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.LanguageQuery;
import com.jyt.terminal.model.Language;

import java.util.List;

/**
 * 多语言
 *
 * @author hcg
 * @Date 2019-6-25
 */
public interface ILanguageService extends IService<Language> {

    List<Language> getLanguageList(Page<Language> page,LanguageQuery languageQuery);

    Integer getCount(LanguageQuery languageQuery);

    List<Language> getAllList();

    Integer updateLanguage(Language language);

    Language selectLanguageById(String lanId);

}
