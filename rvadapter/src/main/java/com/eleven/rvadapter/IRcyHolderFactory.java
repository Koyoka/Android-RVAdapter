package com.eleven.rvadapter;

import android.view.ViewGroup;

import com.eleven.rvadapter.event.RcyBaseViewHolder;


/**
 * Created by 正 on 2017/2/16.
 */

public abstract class IRcyHolderFactory {
    public abstract RcyBaseViewHolder create(ViewGroup parent, int viewType);
}
