package justread.lzj.com.justread.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.FragmentEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

import butterknife.BindView;
import justread.lzj.com.justread.R;
import justread.lzj.com.justread.factory.fragment.BaseFragment;
import justread.lzj.com.justread.factory.other.glide.GlideRoundTransform;
import justread.lzj.com.justread.factory.other.recyclerView.BaseRecyclerViewAdapter;
import justread.lzj.com.justread.factory.other.recyclerView.BaseViewHolder;
import justread.lzj.com.justread.factory.other.recyclerView.OnItemClickListener;
import justread.lzj.com.justread.thirdparty.xrecyclerview.ProgressStyle;
import justread.lzj.com.justread.thirdparty.xrecyclerview.XRecyclerView;
import justread.lzj.com.justread.workspace.main.activity.WebViewActivity;
import justread.lzj.com.justread.workspace.main.network.Network;
import justread.lzj.com.justread.workspace.main.network.entity.BaseSubscriber;
import justread.lzj.com.justread.workspace.main.network.entity.WeiboTopModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeiboTopFragment extends BaseFragment {
    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private List<WeiboTopModel.DataBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public WeiboTopFragment(){

    }

    public static WeiboTopFragment newInstance(int type){
        WeiboTopFragment fragment = new WeiboTopFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weibo_top;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 3) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        Network.getInstance().getServiceWeiboAPI()
                .getWeiboTopData()
                .compose(this.<WeiboTopModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeiboTopModel, List<WeiboTopModel.DataBean>>() {
                    @Override
                    public List<WeiboTopModel.DataBean> call(WeiboTopModel weiboTopModel) {
                        return weiboTopModel.data;
                    }
                })
                .subscribe(new BaseSubscriber<List<WeiboTopModel.DataBean>>() {
                    @Override
                    public void onNext(List<WeiboTopModel.DataBean> list) {
                        super.onNext(list);
                        setListData(list, isClear);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        if (isClear) {
                            mRecyclerView.refreshComplete();
                        } else {
                            mRecyclerView.loadMoreComplete();
                        }
                    }
                });
    }

    private void setListData(List<WeiboTopModel.DataBean> list, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }
        mListData.addAll(list);
        mRefreshAdapter.notifyDataSetChanged();

        if (isClear) {
            mRecyclerView.refreshComplete();
        } else {
            mRecyclerView.loadMoreComplete();
        }
    }


    private void initViewStatus() {
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }

            @Override
            public void onLoadMore() {
                getData(false);
            }
        });

        mRefreshAdapter = new WeiboTopFragment.RefreshAdapter(mContext, R.layout.item_list_weibotop_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<WeiboTopModel.DataBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, WeiboTopModel.DataBean bean, int position) {
                WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, mListData.get(position).article_url);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, WeiboTopModel.DataBean bean, int position) {
                return false;
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        closeRxBus();
    }

    @Override
    protected void RxBusCall(Bundle bundle) {
        super.RxBusCall(bundle);
        if (thisFragmentIsVisible() && "mTitle".equals(bundle.getString("click"))) {
            if (mRecyclerView != null) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<WeiboTopModel.DataBean> {

        public RefreshAdapter(Context context, int layoutId, List<WeiboTopModel.DataBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, WeiboTopModel.DataBean bean) {
            holder.setText(R.id.mTitle, bean.title);
            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String d = format.format(bean.time*1000);
            holder.setText(R.id.mPublishedAt, d);
            if(bean.image_240.size()>0)
                Glide.with(mContext)
                        .load(bean.image_240.get(0).des_url)//目标URL
                        .placeholder(R.drawable.load_image_ing)
                        .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                        .transform(new GlideRoundTransform(mContext))
                        .into((ImageView) holder.getView(R.id.mImage));
        }
    }

}
