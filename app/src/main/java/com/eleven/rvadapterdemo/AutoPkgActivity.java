package com.eleven.rvadapterdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.eleven.rvadapter.extend.AutoBasePkgActivity;
import com.eleven.rvadapter.extend.AutoPkgFragment;

public class AutoPkgActivity extends AutoBasePkgActivity  {

    @Override
    protected int getResPkgArrayId() {
        return R.array.fragmentMenu;
    }
}
