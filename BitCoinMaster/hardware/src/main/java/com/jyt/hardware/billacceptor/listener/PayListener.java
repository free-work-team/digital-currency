package com.jyt.hardware.billacceptor.listener;

import device.itl.sspcoms.SSPPayoutEvent;

public interface PayListener {
    void payResult(SSPPayoutEvent sspPayoutEvent);
}
