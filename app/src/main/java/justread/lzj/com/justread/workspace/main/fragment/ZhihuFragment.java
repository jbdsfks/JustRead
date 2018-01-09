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

import justread.lzj.com.justread.R;
import justread.lzj.com.justread.factory.fragment.BaseFragment;
import justread.lzj.com.justread.factory.other.glide.GlideRoundTransform;
import justread.lzj.com.justread.factory.other.recyclerView.BaseViewHolder;
import justread.lzj.com.justread.factory.utils.UtilsTime;
import justread.lzj.com.justread.factory.utils.UtilsViewEvent;
import justread.lzj.com.justread.thirdparty.logger.Logger;
import justread.lzj.com.justread.workspace.main.activity.WebViewActivity;
import justread.lzj.com.justread.workspace.main.network.Network;
import justread.lzj.com.justread.workspace.main.network.entity.BaseSubscriber;
import justread.lzj.com.justread.workspace.main.network.entity.ZhihuModel;
import justread.lzj.com.justread.thirdparty.xrecyclerview.XRecyclerView;
import justread.lzj.com.justread.thirdparty.xrecyclerview.ProgressStyle;
import justread.lzj.com.justread.factory.other.recyclerView.OnItemClickListener;
import justread.lzj.com.justread.factory.other.recyclerView.BaseRecyclerViewAdapter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by 83827 on 2017/12/15.
 */
public class ZhihuFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private List<ZhihuModel.StoriesBean> mZhihuModels = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public ZhihuFragment() {
        // Required empty public constructor
    }

    public static ZhihuFragment newInstance(int type) {
        ZhihuFragment fragment = new ZhihuFragment();
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
        return R.layout.fragment_zhihu;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 2) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            toLoadFirstData(true);
        } else {
            toLoadMoreData(false);
        }
    }

    private SimpleDateFormat mSDF = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    private Calendar minCal;
    private Calendar lastCal;

    private void initLoadMoreData() {
        //知乎日报诞生日
        minCal = Calendar.getInstance();
        minCal.setTime(UtilsTime.stringTimeToDate("20130520", mSDF));

        lastCal = Calendar.getInstance();
        lastCal.setTime(new Date());
    }

    private void toLoadMoreData(final boolean isClear) {

        if (minCal == null) {
            initLoadMoreData();
        }

        if (minCal.after(lastCal)) {
            mRecyclerView.setNoMore(true);
            return;
        }

        String id = UtilsTime.dateToStringTime(lastCal.getTime(), mSDF);
        lastCal.set(Calendar.DATE,lastCal.get(Calendar.DATE)-1);
        Logger.i(id);
        Network.getInstance().getServiceZhihuAPI()
                .getBeforeData(id)
                .compose(this.<ZhihuModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ZhihuModel, List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public List<ZhihuModel.StoriesBean> call(ZhihuModel zhihuModel) {
                        for (int i = 0; i < zhihuModel.stories.size(); i++) {
                            zhihuModel.stories.get(i).data = zhihuModel.date;
                        }
                        return zhihuModel.stories;
                    }
                })
                .subscribe(new BaseSubscriber<List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public void onNext(List<ZhihuModel.StoriesBean> list) {
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

    private void toLoadFirstData(final boolean isClear) {
        Network.getInstance().getServiceZhihuAPI()
                .getLatestData()
                .compose(this.<ZhihuModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ZhihuModel, List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public List<ZhihuModel.StoriesBean> call(ZhihuModel zhihuModel) {
//                        Log.i(TAG, zhihuModel.stories.get(0).title);
                        for (int i = 0; i < zhihuModel.stories.size(); i++) {
                            zhihuModel.stories.get(i).data = zhihuModel.date;
                        }
                        return zhihuModel.stories;
                    }
                })
                .subscribe(new BaseSubscriber<List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public void onNext(List<ZhihuModel.StoriesBean> list) {
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

    private void setListData(List<ZhihuModel.StoriesBean> list, boolean isClear) {
        if (isClear) {
            initLoadMoreData();
            mZhihuModels.clear();
        }

        mZhihuModels.addAll(list);
        mRefreshAdapter.notifyDataSetChanged();

        if (isClear) {
            mRecyclerView.refreshComplete();
        } else {
            if (list.size() == 0) {
                mRecyclerView.setNoMore(true);
            } else {
                mRecyclerView.loadMoreComplete();
            }
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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_zhihu_fragment, mZhihuModels);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<ZhihuModel.StoriesBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, ZhihuModel.StoriesBean bean, int position) {
                if (!UtilsViewEvent.isFastDoubleClick()) {
                    WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, "http://daily.zhihu.com/story/" + bean.id);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, ZhihuModel.StoriesBean bean, int position) {
                return false;
            }
        });
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<ZhihuModel.StoriesBean> {

        public RefreshAdapter(Context context, int layoutId, List<ZhihuModel.StoriesBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, ZhihuModel.StoriesBean bean) {
            holder.setText(R.id.mTitle, bean.title);
            holder.setText(R.id.mPublishedAt, bean.data);
            Glide.with(mContext)
                    .load(bean.images.get(0))//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .transform(new GlideRoundTransform(mContext))
                    .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}
