package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 订单状态的枚举
 *
 * @author hcg
 * @date 2019-11-20
 */

public enum OrderStatusEnum {

    PRO_CREATE("0", "PRO_CREATE"),
    PRO_PENDING("1", "PRO_PENDING"),
    PRO_CONFIRM("2", "PRO_CONFIRM"),
    PRO_FAIL("3", "PRO_FAIL"),
    PRO_CANCEL("4", "PRO_CANCEL");

        private String value;
        private String desc;

       OrderStatusEnum(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

}

