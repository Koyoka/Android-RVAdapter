package com.eleven.rvadapter.event;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eleven.rvadapter.base.OnRecyclerItemClickListen;
import com.eleven.rvadapter.base.OnRecyclerItemClickListen2;
import com.eleven.rvadapter.base.XTouchListener;
import com.eleven.rvadapter.progressindicator.ProgressStyle;
import com.eleven.rvadapter.widget.BaseRefreshHeader;
import com.eleven.rvadapter.widget.XFooterLayout;
import com.eleven.rvadapter.widget.XHeaderLayout;

import java.util.ArrayList;


/**
 * Created by 正 on 2016/4/7.
 */
public abstract class RcyBaseAdapter extends  RecyclerView.Adapter<RcyBaseViewHolder> {

    final String TAG = RcyBaseAdapter.class.getCanonicalName();

    protected OnRecyclerItemClickListen mOnRecyclerItemClickListen;
    protected OnRecyclerItemClickListen mOnRecyclerItemClickListen_Foot;
    protected OnRecyclerItemClickListen mOnRecyclerItemClickListen_Foot_last;
    protected OnRecyclerItemClickListen2 mOnItemClickListener;

    protected Context context;

    public abstract RcyBaseViewHolder onCreateViewholder(ViewGroup parent, int viewType);
    public abstract ArrayList<? extends  RcyHolderViewModel> getBindSource();

    protected RecyclerPluginsMng mRecyclerPluginsMng;
    protected RecyclerView recyclerView;

    public RcyBaseAdapter(Context context,RecyclerView recyclerView){
        mRecyclerPluginsMng = new RecyclerPluginsMng(context,recyclerView,this);
        this.recyclerView = recyclerView;
        this.context = context;
    }

    public RecyclerPluginsMng getRecyclerPluginsMng(){
        return mRecyclerPluginsMng;
    }

    @Override
    public RcyBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RcyBaseViewHolder vh =
            mRecyclerPluginsMng.renderPluginsView(parent,viewType);

        if(vh == null)
            vh = onCreateViewholder(parent, viewType);

        registerEvent(vh,viewType);

//        if(vh != null) {
//            vh.setOnRecyclerItemClickListen(mOnRecyclerItemClickListen);
//            RcyHolderViewEventMng.onRegisterEvent(vh);
//        }
        return vh;
    }

    private void registerEvent(RcyBaseViewHolder vh, int viewType){

        if(vh ==null)
            return;

        if(viewType == RecyclerPluginsMng.RECYCLER_VIEW_FOOT){
            vh.setOnRecyclerItemClickListen(this,mOnRecyclerItemClickListen_Foot);
        }else if(viewType == RecyclerPluginsMng.RECYCLER_VIEW_HEAD){

        }else  if(viewType == RecyclerPluginsMng.RECYCLER_VIEW_FOOT_LAST){
            vh.setOnRecyclerItemClickListen(this,mOnRecyclerItemClickListen_Foot_last);
        }else if(viewType == RecyclerPluginsMng.RECYCLER_VIEW_ITEM){
            vh.setOnRecyclerItemClickListen(this,mOnRecyclerItemClickListen);
            vh.setOnItemClickListener(this,mOnItemClickListener);
        }else if(viewType >= 0){
            //这里有个规则 自定义view_itemType 需要大于0
            vh.setOnRecyclerItemClickListen(this,mOnRecyclerItemClickListen);
            vh.setOnItemClickListener(this,mOnItemClickListener);
        }

        RcyHolderViewEventMng.onRegisterEvent(vh);
    }

    @Override
    public void onBindViewHolder(RcyBaseViewHolder holder, int position) {
        ArrayList source = getBindSource();
        if(source == null)
            return;

        int curPosition = position - mRecyclerPluginsMng.getHeadItemCount();
        if(curPosition < source.size() && curPosition >= 0){

            if(source.get(curPosition) instanceof RcyHolderViewModel){
                holder.bindData((RcyHolderViewModel) source.get(curPosition));
            }

            if(holder instanceof RcyHolderViewEventMng.OnExpendBindDataListens){
                ((RcyHolderViewEventMng.OnExpendBindDataListens)holder).onExpendBindData(position,getBindSource());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if(getBindSource() == null){
//            return super.getItemViewType(position);
//        }
        if(mRecyclerPluginsMng.getItemViewType(position) == RecyclerPluginsMng.RECYCLER_VIEW_ITEM){
            if(getBindSource() != null){
                int curPosition = position - mRecyclerPluginsMng.getHeadItemCount();
                int viewHolderType = getBindSource().get(curPosition).getViewHolderType();
                if(viewHolderType < 0){
                    Log.e(TAG,"getViewHolderType() need more than 0.",new Exception("getViewHolderType() need more than 0"));
                }else{
                    return viewHolderType;
                }
            }
        }
        return mRecyclerPluginsMng.getItemViewType(position);
    }

    @Override
    public int getItemCount() {

        if(getBindSource() == null){
            return 0;
        }
        return mRecyclerPluginsMng.getItemCount();
    }

    public boolean isFootPosition(int position){
        return mRecyclerPluginsMng.isFootPosition(position) || mRecyclerPluginsMng.isPullFooterPosition(position);
    }
    public boolean isHeaderPosition(int position){
        return mRecyclerPluginsMng.isHeaderPosition(position) || mRecyclerPluginsMng.isPullHeaderPosition(position);
    }

    public void setOnRecyclerItemClickListen(OnRecyclerItemClickListen l){
        mOnRecyclerItemClickListen = l;
    }
    public void setOnFootItemClickListen(OnRecyclerItemClickListen l){
        mOnRecyclerItemClickListen_Foot = l;
    }
    public void setOnLastFootItemClickListen(OnRecyclerItemClickListen l){
        mOnRecyclerItemClickListen_Foot_last = l;
    }
    public void setOnRecyclerItemClickListen2(OnRecyclerItemClickListen2 l){
        mOnItemClickListener = l;
    }

    public interface PutRecyclerHeaderListen{
        RcyBaseViewHolder putHeaderView(ViewGroup parent);
    }

    public interface PutRecyclerFootListen{
        RcyBaseViewHolder putFootView(ViewGroup parent);
    }

    public interface PutRecyclerLastFootListen {
        RcyBaseViewHolder putLastFootView(ViewGroup parent);
    }

    private OnHeaderPullLoadingListens mHeaderPullLoadingListens;

    public void setHeaderPullLoadingListens(OnHeaderPullLoadingListens l){
        mHeaderPullLoadingListens = l;
        this.getRecyclerPluginsMng().setPullLoadingHeader();
    }

    public OnHeaderPullLoadingListens getHeaderPullLoadingListens(){
        return mHeaderPullLoadingListens;
    }

    private OnFooterPullLoadingListens mFooterPullLoadingListens;
    public void setFooterPullLoadingListens(OnFooterPullLoadingListens l,int loadMoreCount){
        mFooterPullLoadingListens = l;
        this.getRecyclerPluginsMng().setPullLoadingFooter(loadMoreCount);
    }
    public OnFooterPullLoadingListens getFooterPullLoadingListens(){
        return mFooterPullLoadingListens;
    }

    public interface OnHeaderPullLoadingListens {
        void refreshComplete(XHeaderLayout headView);
    }
    public interface OnFooterPullLoadingListens{
        void loadMoreComplete(XFooterLayout footView);
    }

    public void initHeaderAndFootGridLayoutManager(GridLayoutManager g, final int spanCount){
        g.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(getBindSource() == null)
                    return 0;

                if(isFootPosition(position)){
                    return spanCount;
                }
                if(isHeaderPosition(position)){
                    return spanCount;
                }

                if(position < getBindSource().size() && getBindSource().get(position).isRowSpan()){
                    return spanCount;
                }
                return 1;
            }
        });
    }


    public static class RecyclerPluginsMng{

        public static final int RECYCLER_VIEW_ITEM = 0;
        public static final int RECYCLER_VIEW_HEAD = -1001;
        public static final int RECYCLER_VIEW_HEAD_PULL_LOADING = -1002;
        public static final int RECYCLER_VIEW_FOOT = -1003;
        public static final int RECYCLER_VIEW_FOOT_PULL_LOADING = -1004;
        public static final int RECYCLER_VIEW_FOOT_LAST = -1005;
        //        public static final int RECYCLER_VIEW_ITEM = 1;
//        public static final int RECYCLER_VIEW_HEAD = -1;
//        public static final int RECYCLER_VIEW_HEAD_PULL_LOADING = 0;
//        public static final int RECYCLER_VIEW_FOOT = 2;
//        public static final int RECYCLER_VIEW_FOOT_PULL_LOADING = 3;
        private int mLoadMoreMinCount = 10;

        private ArrayList<Integer> mHeaderTypes = new ArrayList<>();
        private ArrayList<Integer> mFooterTypes = new ArrayList<>();

        private XHeaderLayout mHeaderLayout;
        private XFooterLayout mFooterLayout;
//        private int mHeaderItemCount = 0;
//        private int mFootItemCount = 0;
        private RcyBaseAdapter mAdapter;
        private Context context;
        private RecyclerView recyclerView;

        public RecyclerPluginsMng(Context context,RecyclerView recyclerView,RcyBaseAdapter adapter){
            mAdapter = adapter;
            this.context = context;
            this.recyclerView = recyclerView;
            init(adapter);
        }

        public void setPullLoadingHeader(){
            mHeaderLayout = new XHeaderLayout(context);
            mHeaderLayout.setProgressStyle(ProgressStyle.SysProgress);
            mHeaderTypes.add(0,RECYCLER_VIEW_HEAD_PULL_LOADING);

            XTouchListener l = getXTouchListener();
            l.setHeaderLayout(mHeaderLayout, new XTouchListener.RefreshInterface() {
                @Override
                public void onRefresh() {
                    OnHeaderPullLoadingListens mHeaderPullLoadingListens = mAdapter.getHeaderPullLoadingListens();
                    if(mHeaderPullLoadingListens != null){
                        mHeaderPullLoadingListens.refreshComplete(mHeaderLayout);
                    }else{
                        new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                mHeaderLayout.refreshComplete(BaseRefreshHeader.STATE_DONE);
                            }
                        }.sendEmptyMessageDelayed(0,1000);
                    }
                }
            });
//            new XTouchListener(mHeaderLayout,true,new XTouchListener.RefreshInterface(){
//
//                @Override
//                public void onRefresh() {
//                    OnHeaderPullLoadingListens mHeaderPullLoadingListens = mAdapter.getHeaderPullLoadingListens();
//                    if(mHeaderPullLoadingListens != null){
//                        mHeaderPullLoadingListens.refreshComplete(mHeaderLayout);
//                    }else{
//                        new Handler(){
//                            @Override
//                            public void handleMessage(Message msg) {
//                                mHeaderLayout.refreshComplete(BaseRefreshHeader.STATE_DONE);
//                            }
//                        }.sendEmptyMessageDelayed(0,1000);
//                    }
//                }
//            });
//            recyclerView.setOnTouchListener(l);
        }

        public void setPullLoadingFooter(int loadMoreMinCount){

            mLoadMoreMinCount = loadMoreMinCount;

            mFooterLayout = new XFooterLayout(context);
            mFooterLayout.setProgressStyle(ProgressStyle.SysProgress);
            mFooterTypes.add(RECYCLER_VIEW_FOOT_PULL_LOADING);

            XTouchListener l = getXTouchListener();
            l.setFooterLayout(mFooterLayout, new XTouchListener.RefreshInterface() {
                @Override
                public void onRefresh() {
                    OnFooterPullLoadingListens mFooterPullLoadingListens = mAdapter.getFooterPullLoadingListens();
                    if (mFooterPullLoadingListens != null) {
                        mFooterPullLoadingListens.loadMoreComplete(mFooterLayout);
                    } else {
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                mFooterLayout.refreshComplete(BaseRefreshHeader.STATE_DONE);
                            }
                        }.sendEmptyMessageDelayed(0, 1000);
                    }
                }
            }, new XTouchListener.CanBeTouchPullingInterface() {
                @Override
                public boolean yesYouCan() {
                    return getSourceItemCount() >= mLoadMoreMinCount;
                }
            });

        }

        private XTouchListener mXTouchListener;
        private XTouchListener getXTouchListener(){
            if(mXTouchListener == null){
                mXTouchListener = new XTouchListener();
                recyclerView.setOnTouchListener(mXTouchListener);
            }

            return mXTouchListener;
        }

        public int getRefreshState(){
            if(mHeaderLayout == null){
                return BaseRefreshHeader.STATE_NORMAL;
            }
            return mHeaderLayout.getVisibleHeight();
        }

        public void init(RecyclerView.Adapter adapter){
            if(adapter instanceof PutRecyclerHeaderListen){
                mHeaderTypes.add(RECYCLER_VIEW_HEAD);
            }

            if(adapter instanceof PutRecyclerFootListen){
                mFooterTypes.add(0,RECYCLER_VIEW_FOOT);
            }

            if(adapter instanceof PutRecyclerLastFootListen){
                mFooterTypes.add(0,RECYCLER_VIEW_FOOT_LAST);
            }
        }

        public int getItemCount(){
            return getSourceItemCount() + getFootItemCount() + getHeadItemCount();
        }
        public int getSourceItemCount(){
            return mAdapter.getBindSource()==null?0:mAdapter.getBindSource().size();
        }
        public int getHeadItemCount(){
            return mHeaderTypes.size();
        }
        public int getFootItemCount(){
            return mFooterTypes.size();
        }

        public boolean isHeaderPosition(int position){
            if(position < mHeaderTypes.size()){
                return mHeaderTypes.get(position) == RECYCLER_VIEW_HEAD;
//                ||
//                        mHeaderTypes.get(position) == RECYCLER_VIEW_HEAD_PULL_LOADING;
            }
            return false;
        }

        public boolean isFootPosition(int position){
            int index = position - (getSourceItemCount() + getHeadItemCount());
            if(index >= 0){
                return mFooterTypes.get(index) == RECYCLER_VIEW_FOOT;
//                return true;
            }
            return false;
        }

        public boolean isLastFootPostion(int position){
            int index = position - (getSourceItemCount() + getHeadItemCount());
            if(index >= 0){
                return mFooterTypes.get(index) == RECYCLER_VIEW_FOOT_LAST;
            }
            return false;
        }

        public boolean isItemPostion(int position){
            return !isHeaderPosition(position) && !isFootPosition(position);
        }

        public boolean isPullHeaderPosition(int position){
            if(position < mHeaderTypes.size()){
                return mHeaderTypes.get(position) == RECYCLER_VIEW_HEAD_PULL_LOADING;
            }
            return false;
        }

        public boolean isPullFooterPosition(int position){
            int index = position - (getSourceItemCount() + getHeadItemCount());
            if(index >= 0){
                return mFooterTypes.get(index) == RECYCLER_VIEW_FOOT_PULL_LOADING;
            }
            return false;
        }

        public int getItemViewType(int position){

            if(isPullHeaderPosition(position)){
                return RECYCLER_VIEW_HEAD_PULL_LOADING;
            }

            if(isHeaderPosition(position)){
                return RECYCLER_VIEW_HEAD;
            }

            if(isPullFooterPosition(position)){
                return RECYCLER_VIEW_FOOT_PULL_LOADING;
            }

            if(isFootPosition(position)){
                return RECYCLER_VIEW_FOOT;
            }

            if(isLastFootPostion(position)){
                return RECYCLER_VIEW_FOOT_LAST;
            }

            return RECYCLER_VIEW_ITEM;
        }

        public RcyBaseViewHolder renderPluginsView(ViewGroup parent,int viewType){
            if(viewType == RECYCLER_VIEW_HEAD){
                if(mAdapter instanceof PutRecyclerHeaderListen){
                    return ((PutRecyclerHeaderListen)mAdapter).putHeaderView(parent);
                }
            }else if(viewType == RECYCLER_VIEW_FOOT){
                if(mAdapter instanceof PutRecyclerFootListen){
                    return ((PutRecyclerFootListen)mAdapter).putFootView(parent);
                }
            }else if(viewType == RECYCLER_VIEW_HEAD_PULL_LOADING){
                return new SimpleViewHolder(mHeaderLayout);
            }else if(viewType == RECYCLER_VIEW_FOOT_PULL_LOADING){
                return new SimpleViewHolder(mFooterLayout);
            }else if(viewType == RECYCLER_VIEW_FOOT_LAST){
                if(mAdapter instanceof PutRecyclerLastFootListen){
                    return ((PutRecyclerLastFootListen)mAdapter).putLastFootView(parent);
                }
            }
            return null;
        }
    }

    private static class SimpleViewHolder extends RcyBaseViewHolder{

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(RcyHolderViewModel rcyHolderViewModel) {

        }
    }
}
