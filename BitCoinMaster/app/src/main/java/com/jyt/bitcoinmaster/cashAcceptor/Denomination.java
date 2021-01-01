package com.jyt.bitcoinmaster.cashAcceptor;

/**
 * 面额列表obj
 *
 * @author huoChangGuo
 * @since 2020/7/1
 */
public class Denomination {
    public static final String PAYOUTSTORE= "PayoutStore";
    public static final String CASHBOX= "Cashbox";

    private Integer index;
    private String realvalue;//面额
    private String country;//币种
    private String route;//PayoutStore 缓存| Cashbox箱底

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getRealvalue() {
        return realvalue;
    }

    public void setRealvalue(String realvalue) {
        this.realvalue = realvalue;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
