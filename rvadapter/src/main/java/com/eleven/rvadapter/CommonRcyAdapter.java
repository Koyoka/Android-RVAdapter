package com.eleven.rvadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleven.rvadapter.base.BindResId;
import com.eleven.rvadapter.event.RcyBaseAdapter;
import com.eleven.rvadapter.event.RcyBaseViewHolder;
import com.eleven.rvadapter.event.RcyHolderViewModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by 正 on 2016/6/20.
 */
public class CommonRcyAdapter extends RcyBaseAdapter {

    String TAG = CommonRcyAdapter.class.getSimpleName();

    private ArrayList<? extends RcyHolderViewModel> source;
    private Class<? extends RcyBaseViewHolder> clz;

//    public CommonRcyAdapter(Context context, RecyclerView recyclerView, ArrayList<? extends RcyHolderViewModel> source, Class<? extends RcyBaseViewHolder> clz){
//        super(context,recyclerView);
//        this.source = source;
//        this.clz = clz;
//    }
//    public CommonRcyAdapter(Context context, RecyclerView recyclerView, ArrayList<? extends RcyHolderViewModel> source,IRcyHolderFactory factory){
//        super(context,recyclerView);
//        this.source = source;
//        mIRcyHolderFactory = factory;
//
//    }
    public CommonRcyAdapter(Context context, RecyclerView recyclerView){
        super(context,recyclerView);
    }
    public Context getContext(){
        return this.context;
    }
    public RecyclerView getRecyclerView(){
        return this.recyclerView;
    }

    public void init(ArrayList<? extends RcyHolderViewModel> source, Class<? extends RcyBaseViewHolder> clz){
        this.source = source;
        this.clz = clz;
    }
    public void init(ArrayList<? extends RcyHolderViewModel> source, IRcyHolderFactory factory){
        this.source = source;
        mIRcyHolderFactory = factory;
    }
    public void setLayoutId(int layoutId){
        mLayoutId = layoutId;
    }
    private IRcyHolderFactory mIRcyHolderFactory;


    @Override
    public RcyBaseViewHolder onCreateViewholder(ViewGroup parent, int viewType) {
        if(mIRcyHolderFactory != null)
            return mIRcyHolderFactory.create(parent,viewType);

        int id = getLayoutId();
        View v = LayoutInflater.from(parent.getContext()).inflate(
                id,
                parent,false);
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_test_eleven_menu_view,parent,false);
        return newHolderView(v);
    }

    private Integer mLayoutId;
    private int getLayoutId(){
        if(mLayoutId != null)
            return mLayoutId;
        mLayoutId = 0;

        //region 添加获取方式 注释标签
        boolean flag = clz.isAnnotationPresent(BindResId.class);
        if (flag) {
            BindResId first = clz.getAnnotation(BindResId.class);
            mLayoutId = first.value();
            return mLayoutId;
        }
        //endregion

        //region 老版本读取静态方法
        try {
            Method m = clz.getMethod("getLayoutId");
            mLayoutId =  (int)m.invoke(clz);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //endregion
        return mLayoutId;
    }

    private RcyBaseViewHolder newHolderView(View v){

        if(clz == null) return null;

        try {
            Constructor c=clz.getDeclaredConstructor(new Class[]{View.class});
            return (RcyBaseViewHolder) c.newInstance(v);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList getBindSource() {
        return source;
    }

}
