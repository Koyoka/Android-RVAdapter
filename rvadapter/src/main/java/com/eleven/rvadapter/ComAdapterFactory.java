package com.eleven.rvadapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eleven.rvadapter.event.RcyBaseAdapter;
import com.eleven.rvadapter.event.RcyBaseViewHolder;
import com.eleven.rvadapter.event.RcyHolderViewModel;

import java.util.ArrayList;

/**
 * Created by 正 on 2016/9/2.
 */
public class ComAdapterFactory {

//    CommonRcyAdapter mCustAdapter;
    IRcyHolderFactory mIRcyHolderFactory;
    Operation mOperation;
    public static ComAdapterFactory newInstance(){
        return new ComAdapterFactory();
    }

    public ComAdapterFactory setRcyHolderFactory(IRcyHolderFactory factory){
        mIRcyHolderFactory = factory;
        return this;
    }

    public ComAdapterFactory(){}

    //region #delete
//    @Deprecated
//    public ComAdapterFactory(CommonRcyAdapter adapter){
//        mCustAdapter = adapter;
//    }
//    @Deprecated
//    public ComAdapterFactory(Class<? extends CommonRcyAdapter> clz){
//
//        Constructor<?> constructor = null;
//        try {
//            constructor = clz.getConstructor();
//        } catch (Exception e) {
//            throw new IllegalStateException(
//                    "Could not find public constructor that has a single (Context) argument for plugins class ");
//        }
//
//        try {
//            mCustAdapter = (CommonRcyAdapter) constructor.newInstance();
//        } catch (Exception e) {
//            throw new IllegalStateException("Could not construct instance of helper class " + clz, e);
//        }
//    }
    @Deprecated
    public CommonRcyAdapter InjectRecyclerView(Context c,
                                               Class<? extends RcyBaseViewHolder> clz,
                                               ArrayList<? extends RcyHolderViewModel> source,
                                               RecyclerView recyclerView, Operation ops){
        CommonRcyAdapter adapter = new  CommonRcyAdapter(c,recyclerView);
        adapter.init(source,clz);
        return initSetting(c,adapter,recyclerView,ops);
    }

    @Deprecated
    public CommonRcyAdapter InjectRecyclerView(Context c,
                                               Class<? extends RcyBaseViewHolder> clz,
                                               ArrayList<? extends RcyHolderViewModel> source,
                                               RecyclerView recyclerView){
        Operation ops = new Operation();
        ops.setDisDivider(false);
        return InjectRecyclerView(c,clz,source,recyclerView,ops);
    }
    //endregion

    public ComAdapterFactory setOperation(Operation operation){
        mOperation = operation;
        return this;
    }

    public <T extends CommonRcyAdapter> CommonRcyAdapter injectRcyAdapter(T adapter,
                                              ArrayList<? extends RcyHolderViewModel> source,
                                              IRcyHolderFactory factory){
        adapter.init(source,factory);
        Operation ops = mOperation == null?new Operation():mOperation;
        return initSetting(adapter.getContext(),adapter,adapter.getRecyclerView(),ops);
    }
    public <T extends CommonRcyAdapter> CommonRcyAdapter injectRcyAdapter(T adapter,
                                              ArrayList<? extends RcyHolderViewModel> source,
                                              Class<? extends RcyBaseViewHolder> clz){
        adapter.init(source,clz);
        Operation ops = mOperation == null?new Operation():mOperation;
        return initSetting(adapter.getContext(),adapter,adapter.getRecyclerView(),ops);
    }


    public CommonRcyAdapter injectRecyclerView(Context c,
                                                      ArrayList<? extends RcyHolderViewModel> source,
                                                      RecyclerView recyclerView,IRcyHolderFactory factory){
        CommonRcyAdapter adapter =  new CommonRcyAdapter(c,recyclerView);
        adapter.init(source,factory);
        Operation ops = mOperation == null?new Operation():mOperation;
        return initSetting(c,adapter,recyclerView,ops);
    }

    public CommonRcyAdapter injectRecyclerView(Context c,
                                               ArrayList<? extends RcyHolderViewModel> source,
                                               RecyclerView recyclerView,
                                               Class<? extends RcyBaseViewHolder> clz){
        CommonRcyAdapter adapter = new CommonRcyAdapter(c,recyclerView);
        adapter.init(source,clz);
        Operation ops = mOperation == null?new Operation():mOperation;
        return initSetting(c,adapter,recyclerView,ops);
    }


    private CommonRcyAdapter initSetting(Context c, CommonRcyAdapter adapter,
                                         RecyclerView recyclerView, Operation ops){
        LinearLayoutManager mLinearLayoutManager = null;

        if(ops.isGrid()){
            mLinearLayoutManager = new GridLayoutManager(c,ops.getGridColCount());
            adapter.initHeaderAndFootGridLayoutManager((GridLayoutManager)mLinearLayoutManager,ops.getGridColCount());
        }else{
            mLinearLayoutManager =  new LinearLayoutManager(c);
        }

        if(ops.getItemDecoration() != null){
            recyclerView.addItemDecoration(ops.getItemDecoration());
        }else if(ops.isDisDivider()){
            recyclerView.addItemDecoration(new RecycleViewDivider(c,adapter, LinearLayoutManager.VERTICAL));
        }

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);

        bindFooterEvent(recyclerView,mLinearLayoutManager,adapter,ops.getOnFooterPositionListens());
        return adapter;
    }

    private void bindFooterEvent(RecyclerView recyclerView, final LinearLayoutManager mng, final CommonRcyAdapter adapter, final OnFooterPositionListens l){
        if(l == null)
            return;
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && mng.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                    l.onShowFooter();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    static class RecycleViewDivider extends RecyclerView.ItemDecoration {
        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;
        private RcyBaseAdapter adapter;

        public RecycleViewDivider(Context context, RcyBaseAdapter adapter, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            this.adapter = adapter;
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if(adapter.getRecyclerPluginsMng().getRefreshState() != 0){
                return ;
            }
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }

        }


        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }

    }

    public static class Operation{
        private boolean isGrid = false;
        private boolean disDivider = false;
        private int gridColCount = 2;
        private RecyclerView.ItemDecoration itemDecoration;

        public RecyclerView.ItemDecoration getItemDecoration() {
            return itemDecoration;
        }

        public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
            this.itemDecoration = itemDecoration;
        }

        public int getGridColCount() {
            return gridColCount;
        }

        public Operation setGridColCount(int gridColCount) {
            this.gridColCount = gridColCount;
            return this;
        }

        public boolean isGrid() {
            return isGrid;
        }

        public Operation setGrid(boolean grid) {
            isGrid = grid;
            return this;
        }
        public Operation setGrid(boolean grid,int colCount) {
            isGrid = grid;
            gridColCount = colCount;
            return this;
        }

        public boolean isDisDivider() {
            return disDivider;
        }

        public Operation setDisDivider(boolean disDivider) {
            this.disDivider = disDivider;
            return this;
        }

        private OnFooterPositionListens mOnFooterPositionListens;

        public OnFooterPositionListens getOnFooterPositionListens() {
            return mOnFooterPositionListens;
        }

        public Operation setOnFooterPositionListens(OnFooterPositionListens l) {
            this.mOnFooterPositionListens = l;
            return this;
        }
    }

    public interface OnFooterPositionListens{
        void onShowFooter();
    }

}
