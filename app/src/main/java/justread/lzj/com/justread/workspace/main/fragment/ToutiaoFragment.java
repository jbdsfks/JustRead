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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import justread.lzj.com.justread.R;
import justread.lzj.com.justread.factory.fragment.BaseFragment;
import justread.lzj.com.justread.factory.other.glide.GlideRoundTransform;
import justread.lzj.com.justread.factory.other.recyclerView.BaseRecyclerViewAdapter;
import justread.lzj.com.justread.factory.other.recyclerView.BaseViewHolder;
import justread.lzj.com.justread.factory.other.recyclerView.OnItemClickListener;
import justread.lzj.com.justread.factory.utils.UtilsViewEvent;
import justread.lzj.com.justread.thirdparty.logger.Logger;
import justread.lzj.com.justread.thirdparty.xrecyclerview.ProgressStyle;
import justread.lzj.com.justread.thirdparty.xrecyclerview.XRecyclerView;
import justread.lzj.com.justread.workspace.main.activity.WebViewActivity;
import justread.lzj.com.justread.workspace.main.network.Network;
import justread.lzj.com.justread.workspace.main.network.entity.BaseSubscriber;
import justread.lzj.com.justread.workspace.main.network.entity.GankioHistoryModel;
import justread.lzj.com.justread.workspace.main.network.entity.ToutiaoModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by 83827 on 2017/12/24.
 */

public class ToutiaoFragment extends BaseFragment {
    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;
    private long max_behot_time;

    private ToutiaoModel toutiaoModels;
    private List<ToutiaoModel.DataBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public ToutiaoFragment() {
        // Required empty public constructor
    }

    public static ToutiaoFragment newInstance(int type) {
        ToutiaoFragment fragment = new ToutiaoFragment();
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
        return R.layout.fragment_toutiao;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 4) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            Date date = new Date();
            max_behot_time = date.getTime() / 1000;
//            Logger.i(String.valueOf(max_behot_time));
        } else {
            max_behot_time = toutiaoModels.next.max_behot_time;
        }
        String category = "news_hot";
//        Logger.i("1");
        Network.getInstance().getServiceToutiaoAPI()
                .getHot(category, max_behot_time)
                .compose(this.<ToutiaoModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ToutiaoModel, List<ToutiaoModel.DataBean>>() {
                    @Override
                    public List<ToutiaoModel.DataBean> call(ToutiaoModel toutiaoModel) {
                        toutiaoModels = toutiaoModel;
                        return toutiaoModel.data;
                    }
                })
                .subscribe(new BaseSubscriber<List<ToutiaoModel.DataBean>>() {
                    @Override
                    public void onNext(List<ToutiaoModel.DataBean> list) {

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

    private void setListData(List<ToutiaoModel.DataBean> list, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }
        Iterator<ToutiaoModel.DataBean> dataBeanIterator = list.iterator();
        while (dataBeanIterator.hasNext()) {
            ToutiaoModel.DataBean e = dataBeanIterator.next();
            if (e.tag != null && e.tag.equals("ad")) {
                dataBeanIterator.remove();
            }
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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_toutiao_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<ToutiaoModel.DataBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, ToutiaoModel.DataBean bean, int position) {
                if (!UtilsViewEvent.isFastDoubleClick()) {
                    WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, "http://www.toutiao.com/group/" + bean.group_id);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, ToutiaoModel.DataBean bean, int position) {
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

    private class RefreshAdapter extends BaseRecyclerViewAdapter<ToutiaoModel.DataBean> {

        public RefreshAdapter(Context context, int layoutId, List<ToutiaoModel.DataBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, ToutiaoModel.DataBean bean) {
            holder.setText(R.id.mTitle, bean.title);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String d = format.format(bean.behot_time * 1000);
            holder.setText(R.id.mPublishedAt, d);
            holder.setText(R.id.mTag,bean.chinese_tag);
            String image_url = bean.image_url;
            if (image_url != null)
                Glide.with(mContext)
                        .load(image_url.replaceFirst("//", "http://"))//目标URL
                        .placeholder(R.drawable.load_image_ing)
                        .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                        .transform(new GlideRoundTransform(mContext))
                        .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}
