package justread.lzj.com.justread.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.FragmentEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import justread.lzj.com.justread.R;
import justread.lzj.com.justread.factory.fragment.BaseFragment;
import justread.lzj.com.justread.factory.other.glide.GlideRoundTransform;
import justread.lzj.com.justread.factory.other.recyclerView.BaseRecyclerViewAdapter;
import justread.lzj.com.justread.factory.other.recyclerView.BaseViewHolder;
import justread.lzj.com.justread.factory.other.recyclerView.OnItemClickListener;
import justread.lzj.com.justread.thirdparty.logger.Logger;
import justread.lzj.com.justread.thirdparty.xrecyclerview.ProgressStyle;
import justread.lzj.com.justread.thirdparty.xrecyclerview.XRecyclerView;
import justread.lzj.com.justread.workspace.main.activity.WebViewActivity;
import justread.lzj.com.justread.workspace.main.network.Network;
import justread.lzj.com.justread.workspace.main.network.entity.BaseSubscriber;
import justread.lzj.com.justread.workspace.main.network.entity.GankioHistoryModel;
import justread.lzj.com.justread.workspace.main.network.entity.OneModel;
import justread.lzj.com.justread.workspace.main.network.service.RequestServiceOne;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OneFragment extends BaseFragment {
    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;
    private int type;
    private String before_date;
    private Date date;
    private String months[] = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};
    private List<OneModel.DataBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;
    public static OneFragment newInstance(int type) {
        OneFragment fragment = new OneFragment();
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
        return R.layout.fragment_one;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 5) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {

        if (isClear){
            date = new Date();

        }else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            calendar.setTime(date);
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
            try {
                date = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        before_date = simpleDateFormat.format(date);
        Network.getInstance().getServiceOneApi()
                .getOne(before_date)
                .compose(this.<OneModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<OneModel, OneModel.DataBean>() {
                    @Override
                    public OneModel.DataBean call(OneModel oneModel) {
                        return oneModel.data;
                    }
                })
                .subscribe(new BaseSubscriber<OneModel.DataBean>() {
                    @Override
                    public void onNext(OneModel.DataBean list) {
                        super.onNext(list);
                        List<OneModel.DataBean> dataBeanList = new ArrayList<>();
                        dataBeanList.add(list);
                        setListData(dataBeanList, isClear);
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

    private void setListData(List<OneModel.DataBean> list, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }

        mListData.addAll(list);
//        Logger.e(String.valueOf(mListData.size()));
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

        mRefreshAdapter = new OneFragment.RefreshAdapter(mContext, R.layout.item_list_one_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<OneModel.DataBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, OneModel.DataBean bean, int position) {
                WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, mListData.get(position).share_url);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, OneModel.DataBean bean, int position) {
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

    private class RefreshAdapter extends BaseRecyclerViewAdapter<OneModel.DataBean> {

        public RefreshAdapter(Context context, int layoutId, List<OneModel.DataBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, OneModel.DataBean bean) {
            holder.setText(R.id.mTitle_1,bean.share_info.title);
            holder.setText(R.id.mTitle_2, bean.title+"|"+bean.pic_info);
            holder.setText(R.id.mDesc,bean.forward);
            holder.setText(R.id.mWords,bean.words_info);
            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Calendar calendar = format.getCalendar();
            try {
                calendar.setTime(format.parse(bean.post_date));
                holder.setText(R.id.mDay,String.valueOf(calendar.get(Calendar.DATE)));
                holder.setText(R.id.mYearAndMonth,months[calendar.get(Calendar.MONTH)]+". "+calendar.get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Glide.with(mContext)
                    .load(bean.img_url)//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .transform(new GlideRoundTransform(mContext))
                    .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}
