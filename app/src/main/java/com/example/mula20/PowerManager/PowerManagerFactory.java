package com.example.mula20.PowerManager;

import com.example.mula20.Modules.Paras;

public class PowerManagerFactory {

    public static IPowerManager Get() {
        if (Paras.DEVA40_XiPin.equals(Paras.devType)) {
            return new PowerManagerA2040_XiPin(Paras.appContext);
        } else if (Paras.DEVA20_XiPin.equals(Paras.devType)) {
            return new PowerManagerA2040_XiPin(Paras.appContext);
        }
        return new PowerManagerDef();
    }
}
