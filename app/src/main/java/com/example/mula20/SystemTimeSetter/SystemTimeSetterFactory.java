package com.example.mula20.SystemTimeSetter;

import com.example.mula20.Modules.Paras;

public class SystemTimeSetterFactory {

    static public ISystemTimeSetter Get() {
        if (Paras.DEVELOPMODE) {
            return new SystemTimeSetterFake();
        }

        return new SystemTimeSetterDef();
    }
}
