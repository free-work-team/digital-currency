package com.jyt.bitcoinmaster.cashAcceptor;

import android.content.Context;
import com.jyt.hardware.billacceptor.listener.DevEventListener;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.enums.CashAcceptorEnum;

/**
 * 纸币接收模块的工厂
 * @author hcg
 */
public class CashAcceptorFactory {

    /**
     * 获取钞箱对象
     *
     * @param context
     * @param devEventListener
     * @return
     */
    public static ICashAcceptor getCashAcceptor(Context context, DevEventListener devEventListener) {
        String acceptorValue = DBHelper.getHelper().getCashAcceptor();
        ICashAcceptor iCashAcceptor = null;
        CashAcceptorEnum cashAcceptor = CashAcceptorEnum.getEnum(acceptorValue);
        switch (cashAcceptor) {
            case ITL:
                iCashAcceptor = ITLService.getInstance(context,devEventListener);
                break;
            case CPI:
                iCashAcceptor = CPIService.getInstance(context,devEventListener);
                break;
            default:// 默认ITL
                iCashAcceptor = ITLService.getInstance(context,devEventListener);
                break;
        }
        return iCashAcceptor;
    }

}
