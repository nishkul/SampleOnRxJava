package com.test.android.dto;

import android.content.Intent;

/**
 * Created by Manish on 20/2/17.
 */

public class Pojo {
    private Intent intent;
    private int resultCode;

    public Pojo(Intent intent, int resultCode) {
        this.intent = intent;
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "Pojo{}";
    }
}
