package com.eleven.rvadapter.extend;

import com.eleven.rvadapter.event.RcyHolderViewModel;

public class AutoViewModel extends RcyHolderViewModel {
    private String value;
    private String key;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private OnClickListens mOnClickListens;

    public OnClickListens getOnClickListens() {
        return mOnClickListens;
    }

    public void setOnClickListens(OnClickListens mOnClickListens) {
        this.mOnClickListens = mOnClickListens;
    }

    public interface OnClickListens{
        void onClick();
    }
}
