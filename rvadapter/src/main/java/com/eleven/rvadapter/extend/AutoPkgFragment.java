package com.eleven.rvadapter.extend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eleven.rvadapter.ComAdapterFactory;
import com.eleven.rvadapter.CommonRcyAdapter;
import com.eleven.rvadapter.R;
import com.eleven.rvadapter.base.OnRecyclerItemClickListen;
import com.eleven.rvadapter.event.RcyBaseAdapter;
import com.eleven.rvadapter.event.RcyBaseViewHolder;
import com.eleven.rvadapter.event.RcyHolderViewEventMng;
import com.eleven.rvadapter.event.RcyHolderViewModel;
import com.eleven.rvadapter.widget.BaseRefreshHeader;
import com.eleven.rvadapter.widget.XHeaderLayout;

import java.util.ArrayList;

public abstract class AutoPkgFragment extends Fragment {

    protected <T extends View> T $(View rootView, int id){
        return (T) rootView.findViewById(id);
    }
    private RecyclerView recyclerView;
    private ArrayList<AutoViewModel> source;
    private OnShowFragmentEventListener mainActivity;


    public AutoPkgFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rv_fragment_auto_pkg_view, container, false);
        iView(rootView);
        iData(rootView);
        return rootView;
    }

    protected void iView(View view) {
        recyclerView = $(view, R.id.recyclerView);
        if(getActivity() instanceof OnShowFragmentEventListener){
            mainActivity = (OnShowFragmentEventListener) getActivity();
        }
    }

    protected void iData(View view) {
        //region build source
        source = new ArrayList<>();
        String[] menus = getResources().getStringArray(getPkgArrayId());

        int i = 1;
        for (String s : menus) {

            if(s == null){
                continue;
            }

            String[] ary = s.split(",");
            AutoViewModel vm = new AutoViewModel();
            if(ary.length == 2){
                vm.setKey(ary[0].trim());
                vm.setValue(i+++". "+ary[1].trim());
            }else{
                vm.setValue(s);
            }
            source.add(vm);
        }
        //endregion

        //region init adapter
        ComAdapterFactory.Operation ops = new ComAdapterFactory.Operation();
        ops.setDisDivider(true);
        CommonRcyAdapter adapter =
                ComAdapterFactory.newInstance()
                        .setOperation(ops)
                        .injectRecyclerView(getContext(),
                                source,
                                recyclerView,
                                AutoViewHolder.class);
        //endregion

        //region recycler view pull down listener
        adapter.setHeaderPullLoadingListens(new RcyBaseAdapter.OnHeaderPullLoadingListens() {

            @Override
            public void refreshComplete(final XHeaderLayout headView) {
                new MyHandler(headView).
                        sendEmptyMessageDelayed(0,500);
            }
        });
        //endregion

        //region recycler view clicn event
        adapter.setOnRecyclerItemClickListen(new OnRecyclerItemClickListen() {

            @Override
            public void onRecyclerItemClick(int position) {

                String src = source.get(position).getKey();
                if(TextUtils.isEmpty(src)){
                    return;
                }

                String[] ss = src.split(":");
                if(ss.length == 1){
                    Fragment f = ClzHelperLoad(src);
                    mainActivity_ShowFragment(f);
                }else if(ss.length == 2){

                    String pkg = ss[1];

                    if(src.contains("activity:")){

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(getContext(), pkg));
                        startActivity(intent);

                    }else if(src.contains("fragment:")){

                        Fragment f = ClzHelperLoad(pkg);
                        mainActivity_ShowFragment(f);
                    }else{
                        ToastUtilShow(getContext(),src+"???");
                    }

                }else{
                    ToastUtilShow(getContext(),src+"///"+ss.length);
                }
            }
        });
        //endregion

    }

    private static class MyHandler extends Handler{
        private XHeaderLayout headView;
        private MyHandler(XHeaderLayout headView){
            this.headView = headView;
        }
        @Override
        public void handleMessage(Message msg) {
            headView.refreshComplete(BaseRefreshHeader.STATE_ERROR);
        }
    }


    private void mainActivity_ShowFragment(Fragment f){
        if(mainActivity == null)
            return;
        mainActivity.showFragment(f);
    }
    protected abstract int getPkgArrayId();
    public interface OnShowFragmentEventListener{
        void showFragment(Fragment f);
    }


    //region utils
    protected <T> T ClzHelperLoad(String pkg){
        Class<?> clz = null;
        try {
            clz = Class.forName(pkg);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not find pkg class "+pkg);
        }

        try {
            T n = (T) clz.newInstance();
            return n;
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not construct instance of helper class " + pkg, e);
        }
    }

    protected void  ToastUtilShow(Context context, String message){
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
    }
    //endregion

//    //region recyclerView
//    protected static class ViewModel extends RcyHolderViewModel{
//        private String value;
//        private String key;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getKey() {
//            return key;
//        }
//
//        public void setKey(String key) {
//            this.key = key;
//        }
//    }
//
//    protected static class ViewHolder extends RcyBaseViewHolder<ViewModel>
//            implements RcyHolderViewEventMng.RegisterItemClick {
//        public static int getLayoutId(){
//            return R.layout.rv_list_item_sample_view;
//        }
//
//        private TextView textView;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            textView = $(itemView, R.id.txtDescView);
//        }
//
//        @Override
//        public void bindData(ViewModel viewModel) {
//            textView.setText(viewModel.getValue());
//        }
//
//        @Override
//        public View getClickItemView() {
//            return itemView;
//        }
//    }
//    //endregion
}
