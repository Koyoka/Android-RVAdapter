package com.eleven.rvadapter.extend;

import android.view.View;
import android.widget.TextView;

import com.eleven.rvadapter.R;
import com.eleven.rvadapter.event.RcyBaseViewHolder;
import com.eleven.rvadapter.event.RcyHolderViewEventMng;

public class AutoViewHolder extends RcyBaseViewHolder<AutoViewModel>
        implements RcyHolderViewEventMng.RegisterItemClick {
    public static int getLayoutId(){
        return R.layout.rv_list_item_sample_view;
    }

    private TextView textView;
    public AutoViewHolder(View itemView) {
        super(itemView);
        textView = $(itemView, R.id.txtDescView);
    }

    @Override
    public void bindData(AutoViewModel viewModel) {
        textView.setText(viewModel.getValue());
    }

    @Override
    public View getClickItemView() {
        return itemView;
    }
}