package com.jyt.terminal.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.LanguageQuery;
import com.jyt.terminal.model.Language;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 多语言 Mapper 接口
 * </p>
 *
 * @author hcg
 * @since 2019-6-25
 */
public interface LanguageMapper extends BaseMapper<Language> {
    List<Language> getLanguageList(@Param("page") Page<Language> page, @Param("languageQuery") LanguageQuery languageQuery);

    Integer getCount(@Param("languageQuery") LanguageQuery languageQuery);

    List<Language> selectALL();

}
