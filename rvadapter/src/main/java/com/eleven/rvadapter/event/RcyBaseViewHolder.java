package com.eleven.rvadapter.event;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eleven.rvadapter.base.OnRecyclerItemClickListen;
import com.eleven.rvadapter.base.OnRecyclerItemClickListen2;


/**
 * Created by 正 on 2016/4/7.
 */
public abstract class RcyBaseViewHolder<DTO extends RcyHolderViewModel> extends RecyclerView.ViewHolder {

    final String TAG = RcyBaseViewHolder.class.getName();
    protected RcyBaseAdapter adapter;
    protected int headItemCount = 0;

    public RcyBaseViewHolder(View itemView) {
        super(itemView);
    }


    public abstract void bindData(DTO dto);

    protected OnRecyclerItemClickListen mOnRecyclerItemClickListen;

    public void setOnRecyclerItemClickListen(RcyBaseAdapter adapter,OnRecyclerItemClickListen l){
        this.adapter = adapter;
        if(adapter.getRecyclerPluginsMng() != null){
            headItemCount = adapter.getRecyclerPluginsMng().getHeadItemCount();
        }
        mOnRecyclerItemClickListen = l;
    }

    public int getCurrentPosition(){
        return getAdapterPosition() - headItemCount;
    }

    public void setItemClick(){
        if(mOnRecyclerItemClickListen != null
                && getAdapterPosition() != RecyclerView.NO_POSITION){
            mOnRecyclerItemClickListen.onRecyclerItemClick(getCurrentPosition());

        }
    }

    protected OnRecyclerItemClickListen2 mOnItemClickListens;
    public void setOnItemClickListener(RcyBaseAdapter adapter,OnRecyclerItemClickListen2 l){
        mOnItemClickListens = l;
    }
    public OnRecyclerItemClickListen2 registerItemClickListens(){
        return mOnItemClickListens;
    }
    protected <T extends View> T $(View v,int id) {
        return (T) v.findViewById(id);
    }

}
