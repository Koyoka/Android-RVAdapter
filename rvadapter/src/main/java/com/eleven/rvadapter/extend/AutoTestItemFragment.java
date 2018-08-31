package com.eleven.rvadapter.extend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleven.rvadapter.ComAdapterFactory;
import com.eleven.rvadapter.CommonRcyAdapter;
import com.eleven.rvadapter.R;
import com.eleven.rvadapter.base.OnRecyclerItemClickListen;

import java.util.ArrayList;

public abstract class AutoTestItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<AutoViewModel> source;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rv_fragment_auto_pkg_view, container, false);
        iView(rootView);
        iData(rootView);
        return rootView;
    }

    protected void iView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    protected void iData(View view) {
        source = new ArrayList<>();

        makeTestFuncs(buildTestFuncs());

        ComAdapterFactory.Operation ops = new ComAdapterFactory.Operation();
        ops.setDisDivider(true);
        CommonRcyAdapter adapter =
                ComAdapterFactory
                        .newInstance()
                        .setOperation(ops)
                        .injectRecyclerView(
                        getContext(),
                        source,recyclerView,
                        AutoViewHolder.class);

        adapter.setOnRecyclerItemClickListen(new OnRecyclerItemClickListen() {
            @Override
            public void onRecyclerItemClick(int position) {
                source.get(position).getOnClickListens().onClick();
            }
        });
    }

    protected abstract TestFuncs buildTestFuncs();

    protected void addd(String title, AutoViewModel.OnClickListens l){
        AutoViewModel vm = new AutoViewModel();
        vm.setValue(title);
        vm.setKey(title);
        vm.setOnClickListens(l);
        source.add(vm);
    }

    private void makeTestFuncs(TestFuncs mTestFuncs) {

        if(mTestFuncs == null)
            return;
        mTestFuncs.testItem();
    }

    public static abstract class TestFuncs{
        public abstract void testItem();
    }

}
