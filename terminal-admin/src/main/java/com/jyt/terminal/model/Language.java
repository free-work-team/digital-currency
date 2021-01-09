package com.jyt.terminal.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 角色和菜单关联表
 * </p>
 *
 * @author stylefeng
 * @since 2017-07-11
 */
@TableName("sys_language")
public class Language extends Model<Language> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "lan_english")
    private String lanEnglish;

    @TableField(value = "lan_chinese")
    private String lanChinese;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanEnglish() {
        return lanEnglish;
    }

    public void setLanEnglish(String lanEnglish) {
        this.lanEnglish = lanEnglish;
    }

    public String getLanChinese() {
        return lanChinese;
    }

    public void setLanChinese(String lanChinese) {
        this.lanChinese = lanChinese;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
