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

import java.util.ArrayList;
import java.util.List;

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
import justread.lzj.com.justread.workspace.main.network.entity.GankioHistoryModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by 83827 on 2017/12/18.
 */

public class GankioFragment extends BaseFragment {
    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private int PAGE_COUNT = 10;
    private int PAGE;

    private List<GankioHistoryModel.ResultsBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public GankioFragment() {
        // Required empty public constructor
    }

    public static GankioFragment newInstance(int type) {
        GankioFragment fragment = new GankioFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("type", type);
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
        return R.layout.fragment_gankio;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 1) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            PAGE = 1;
        } else {
            PAGE = (int) Math.ceil((double) mListData.size() * 1.0 / PAGE_COUNT) + 1;
        }

        Network.getInstance().getServiceGankioAPI()
                .getHistoryContent(PAGE_COUNT, PAGE)
                .compose(this.<GankioHistoryModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<GankioHistoryModel, List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public List<GankioHistoryModel.ResultsBean> call(GankioHistoryModel gankioHistoryModel) {
                        return gankioHistoryModel.results;
                    }
                })
                .subscribe(new BaseSubscriber<List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public void onNext(List<GankioHistoryModel.ResultsBean> list) {
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

    private void setListData(List<GankioHistoryModel.ResultsBean> list, boolean isClear) {
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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_gankio_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<GankioHistoryModel.ResultsBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, GankioHistoryModel.ResultsBean bean, int position) {
                WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, mListData.get(position).url);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, GankioHistoryModel.ResultsBean bean, int position) {
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

    private class RefreshAdapter extends BaseRecyclerViewAdapter<GankioHistoryModel.ResultsBean> {

        public RefreshAdapter(Context context, int layoutId, List<GankioHistoryModel.ResultsBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, GankioHistoryModel.ResultsBean bean) {
            holder.setText(R.id.mTitle, bean.desc);
            holder.setText(R.id.mPublishedAt, bean.publishedAt);
            if(bean.images != null)
                Glide.with(mContext)
                        .load(bean.images.get(0))//目标URL
                        .placeholder(R.drawable.load_image_ing)
                        .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                        .transform(new GlideRoundTransform(mContext))
                        .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}
