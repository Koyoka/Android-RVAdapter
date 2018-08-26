package com.eleven.rvadapter.event;

import android.view.View;

import com.eleven.rvadapter.base.OnRecyclerItemClickListen2;

import java.util.ArrayList;

/**
 * Created by 正 on 2016/4/7.
 */
public class RcyHolderViewEventMng {
    public static void onRegisterEvent(RcyBaseViewHolder vh){

        onRegisterItemClick(vh);
    }

    private static void onRegisterItemClick(final RcyBaseViewHolder vh){

        if(vh instanceof RegisterItemClick){
            View v = ((RegisterItemClick)vh).getClickItemView();
            if(v != null){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vh.setItemClick();
                    }
                });
            }
        }

        if(vh instanceof RegisterItemClickEvent){
            ((RegisterItemClickEvent)vh).onRegisterClickEvent(vh.registerItemClickListens());
        }
    }
    public interface RegisterItemClick{
        View getClickItemView();
    }

    public interface OnExpendBindDataListens<T>{
        void onExpendBindData(int position, ArrayList<T> source);
    }

    public interface RegisterItemClickEvent {
        void onRegisterClickEvent(OnRecyclerItemClickListen2 l);
    }
}
