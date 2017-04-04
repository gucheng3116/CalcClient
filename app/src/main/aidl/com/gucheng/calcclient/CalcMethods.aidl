// CalcMethods.aidl
package com.gucheng.calcclient;

// Declare any non-default types here with import statements

interface CalcMethods {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

            long addFunc(long addOne, long addTwo);
            long addGetResult();
}
