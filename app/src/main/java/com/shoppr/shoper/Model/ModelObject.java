package com.shoppr.shoper.Model;


import com.shoppr.shoper.R;

public enum ModelObject {

    RED(R.string.red, R.layout.layout_step1),
    BLUE(R.string.blue, R.layout.layout_step2),
    GREEN(R.string.green, R.layout.layout_step3);
 /*   BLACK(R.string.black, R.layout.layout_step4);*/

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}