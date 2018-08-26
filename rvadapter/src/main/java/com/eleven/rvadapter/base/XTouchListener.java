package com.eleven.rvadapter.base;

import android.view.MotionEvent;
import android.view.View;

import com.eleven.rvadapter.widget.BaseRefreshHeader;
import com.eleven.rvadapter.widget.XFooterLayout;
import com.eleven.rvadapter.widget.XHeaderLayout;


public class XTouchListener implements View.OnTouchListener {
    String TAG = XTouchListener.class.getSimpleName();

    private XHeaderLayout mRefreshHeaderLayout = null;
    private XFooterLayout mRefreshFooterLayout = null;
    private boolean isRefreshHeader = false;
    private float rawY = -1;

    private RefreshInterface headerRefreshInterface = null;
    private RefreshInterface footerRefreshInterface = null;
    private static final int DAMP = 4;
    private AppBarStateChangeListener.State state = AppBarStateChangeListener.State.EXPANDED;




    public void setState(AppBarStateChangeListener.State state) {
        this.state = state;
    }


    public XTouchListener(XHeaderLayout mRefreshHeaderLayout, boolean isRefreshHeader, RefreshInterface refreshInterface) {
        this.mRefreshHeaderLayout = mRefreshHeaderLayout;
        this.isRefreshHeader = isRefreshHeader;
        this.headerRefreshInterface = refreshInterface;
    }
    public XTouchListener(XHeaderLayout mRefreshHeaderLayout,XFooterLayout mFooterLayout, boolean isRefreshHeader, RefreshInterface refreshInterface) {
        this.mRefreshHeaderLayout = mRefreshHeaderLayout;
        this.mRefreshFooterLayout = mFooterLayout;
        this.isRefreshHeader = isRefreshHeader;
        this.headerRefreshInterface = refreshInterface;
    }

    public XTouchListener(){
        isRefreshHeader = true;
    }

    public void setHeaderLayout(XHeaderLayout layout,RefreshInterface refreshInterface){
        this.mRefreshHeaderLayout = layout;
        this.headerRefreshInterface = refreshInterface;
    }
    public interface CanBeTouchPullingInterface{
        boolean yesYouCan();
    }
    private CanBeTouchPullingInterface mCanBeTouchPullingInterface;
    public void setFooterLayout(XFooterLayout layout,RefreshInterface refreshInterface,CanBeTouchPullingInterface canBeTouchPullingInterface){
        this.mRefreshFooterLayout = layout;
        this.footerRefreshInterface = refreshInterface;
        this.mCanBeTouchPullingInterface = canBeTouchPullingInterface;
    }

    public boolean canBeTouchPulling(){
        if(mCanBeTouchPullingInterface == null)
            return false;
        return mCanBeTouchPullingInterface.yesYouCan();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (null == mRefreshHeaderLayout && null == mRefreshFooterLayout) {
            return false;
        }
        if (rawY == -1) {
            rawY = motionEvent.getRawY();
        }
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rawY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                 float deltaY = motionEvent.getRawY() - rawY;
                boolean isHeader = deltaY > 0;
                boolean isFooter = !isHeader;

                rawY = motionEvent.getRawY();

                if (isHeader &&
                        isTop() && state == AppBarStateChangeListener.State.EXPANDED) {

                    mRefreshHeaderLayout.onMove(deltaY / DAMP);
                    if (mRefreshHeaderLayout.getVisibleHeight() > 0 && mRefreshHeaderLayout.getState() < BaseRefreshHeader.STATE_REFRESHING) {
                        return true;
                    }
                }else if(canBeTouchPulling() && isFooter &&
                        isBottom() && state == AppBarStateChangeListener.State.EXPANDED){

                    deltaY = Math.abs(deltaY);
                    mRefreshFooterLayout.onMove(deltaY / DAMP);
                    if (mRefreshFooterLayout.getVisibleHeight() > 0 && mRefreshFooterLayout.getState() < BaseRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }else{
                        return true;
                    }
                }
                break;
            default:
                rawY = -1;
                if (isTop() && mRefreshHeaderLayout.getVisibleHeight() > 0
                        && isRefreshHeader && state == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeaderLayout.releaseAction()) {
                        if (headerRefreshInterface != null) {
                            headerRefreshInterface.onRefresh();
                        }
                    }
                }else if (isBottom() && mRefreshFooterLayout.getVisibleHeight() > 0
                        && state == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshFooterLayout.releaseAction()) {
                        if (footerRefreshInterface != null) {
                            footerRefreshInterface.onRefresh();
                        }
                    }
                }
                break;
        }
        return false;
    }

    private boolean isTop() {
        return mRefreshHeaderLayout != null && mRefreshHeaderLayout.getParent() != null;
    }

    private boolean isBottom(){
        return mRefreshFooterLayout != null && mRefreshFooterLayout.getParent() != null;
    }

    public interface RefreshInterface {
        void onRefresh();
    }
}
