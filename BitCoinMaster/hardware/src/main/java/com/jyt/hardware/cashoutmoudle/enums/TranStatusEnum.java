package com.jyt.hardware.cashoutmoudle.enums;


import org.apache.commons.lang.StringUtils;

/**
 * 钱包交易状态的枚举
 *
 * @author sunshubin
 * @date 2019-11-21
 */

public enum TranStatusEnum {

        INIT(0, "init"),
        PENDING(1, "pending"),
        CONFIRM(2, "confirmed"),
        FAIL(3, "fail"),
        ERROR(4, "error");

        private int value;
        private String desc;

       TranStatusEnum(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static String valueOf(Integer code) {
            if (code == null) {
                return "";
            }
            for (TranStatusEnum s : TranStatusEnum.values()) {
                if (s.getValue() == code) {
                    return s.getDesc();
                }
            }
            return "";
        }

        /**
         * 通过value获取
         * @param yourValue
         * @return
         */
        public static TranStatusEnum getEnum(String yourValue) {
            if (StringUtils.isNotEmpty(yourValue)) {
                int intValue = Integer.valueOf(yourValue);
                for (TranStatusEnum s : TranStatusEnum.values()) {
                    if (s.getValue() == intValue) {
                        return s;
                    }
                }
            }
            return TranStatusEnum.INIT;
        }

    }

