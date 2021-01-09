package com.jyt.terminal.controller.admin;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.dto.LanguageQuery;
import com.jyt.terminal.model.Language;
import com.jyt.terminal.service.ILanguageService;
import com.jyt.terminal.util.Tip;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 多语言
 *
 * @author hcg
 * @Date 2019-6-25
 */
@Controller
@RequestMapping("/language")
public class LanguageController extends BaseController {

    @Resource
    private ILanguageService languageService;

    private static String PREFIX = "/system/language";

    /**
     * 跳转
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @Permission
    public String index() {
        return PREFIX + "/language.html";
    }

    /**
     * 列表
     *
     * @param languageQuery
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Object list(LanguageQuery languageQuery) {
        Page<Language> page = new PageFactory<Language>().defaultPage();
        Integer count = languageService.getCount(languageQuery);
        page.setTotal(count);
        List<Language> result = languageService.getLanguageList(page, languageQuery);
        page.setRecords(result);
        return super.packForBT(page);
    }

    /**
     * 跳转添加
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @Permission
    public String openAddView() {
        return PREFIX + "/language_add.html";
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Tip add(Language language) {
        languageService.insert(language);
        return SUCCESS_TIP;
    }
    /**
     * to编辑
     *
     * @return
     */
    @RequestMapping(value = "/update/{lanId}",method = RequestMethod.GET)
    @Permission
    public String edit(@PathVariable("lanId") String lanId, Model model) {
        model.addAttribute("language", languageService.selectLanguageById(lanId));
        return PREFIX + "/language_edit.html";
    }

    /**
     * 更新
     *
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Tip edit(Language language) {
        languageService.updateById(language);
        return SUCCESS_TIP;
    }



}
