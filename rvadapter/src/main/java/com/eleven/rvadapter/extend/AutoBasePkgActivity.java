package com.eleven.rvadapter.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.eleven.rvadapter.R;

public abstract class AutoBasePkgActivity extends AppCompatActivity implements AutoPkgFragment.OnShowFragmentEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_activity_auto_pkg);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rv_contentView,MyFragment.newInstance(getResPkgArrayId()))
                .commit();
    }

    @Override
    public void showFragment(Fragment f) {
        this.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
                .addToBackStack(f.getClass().getSimpleName())
                .add(R.id.rv_contentView,f)
                .hide( getSupportFragmentManager().findFragmentById(R.id.rv_contentView))
                .commit();
    }

    protected abstract int getResPkgArrayId();
    public static class MyFragment extends AutoPkgFragment {
        public final static String BUNDLE_PKG_RES_ID = "AutoPkgFragment.MyFragment.resId";

        public static MyFragment newInstance(int resPkgArrayId){
            MyFragment f = new MyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_PKG_RES_ID, resPkgArrayId);
            f.setArguments(bundle);
            return f;
        }
        private int resPkgArrayId = 0;
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            resPkgArrayId = getArguments().getInt(BUNDLE_PKG_RES_ID);
        }

        @Override
        protected int getPkgArrayId() {
            return resPkgArrayId;
        }
    }
}
