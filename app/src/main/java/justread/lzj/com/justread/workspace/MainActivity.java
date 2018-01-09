package justread.lzj.com.justread.workspace;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import justread.lzj.com.justread.factory.console.ActivityCollector;
import justread.lzj.com.justread.factory.utils.RxBusUtils;
import justread.lzj.com.justread.factory.utils.UtilsToast;
import justread.lzj.com.justread.factory.utils.UtilsViewEvent;
import justread.lzj.com.justread.workspace.main.fragment.GankioFragment;
import justread.lzj.com.justread.workspace.main.fragment.OneFragment;
import justread.lzj.com.justread.workspace.main.fragment.ToutiaoFragment;
import justread.lzj.com.justread.workspace.main.fragment.WeiboTopFragment;
import justread.lzj.com.justread.workspace.main.fragment.ZhihuFragment;
import justread.lzj.com.justread.thirdparty.magicindicator.MagicIndicator;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.CommonNavigator;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import justread.lzj.com.justread.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mBack)
    TextView mBack;
    @BindView(R.id.mTitle)
    TextView mTitle;
    @BindView(R.id.mMenu)
    TextView mMenu;
    @BindView(R.id.mActionBarBackground)
    RelativeLayout mActionBarBackground;
    @BindView(R.id.mMagicIndicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    private FragAdapter mAdapter;

    private List<String> mDataList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initViewStatus();
        initData();
        initViewPager();
        initIndicator();
    }
    private void initViewStatus() {
        mTitle.setText("JustRead");
    }

    private void initData() {
        mDataList.add("Gank.io");
//        mDataList.add("U148.net");
        mDataList.add("Zhihu.com");
        mDataList.add("Top.Weibo");
        mDataList.add("Toutiao.com");
        mDataList.add("One 一个");
        //构造适配器
        fragments.add(GankioFragment.newInstance(1));
//        fragments.add(U148Fragment.newInstance(2));
        fragments.add(ZhihuFragment.newInstance(2));
        fragments.add(WeiboTopFragment.newInstance(3));
        fragments.add(ToutiaoFragment.newInstance(4));
        fragments.add(OneFragment.newInstance(5));

        mAdapter = new FragAdapter(getSupportFragmentManager(), fragments);
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    private void initIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true); //当前页始终定位到中间
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getItemView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#727272"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#eeeeee"));

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;

            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#2A2A2A"));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
    }
    @OnClick({R.id.mBack, R.id.mTitle, R.id.mMenu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBack:
                break;
            case R.id.mTitle:
                Bundle bundle = new Bundle();
                bundle.putString("click", "mTitle");
                RxBusUtils.getInstance().send(bundle);
                break;
            case R.id.mMenu:
                break;
        }
    }

    public class FragAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!UtilsViewEvent.isFastDoubleClick2()) {
                UtilsToast.show("Fast double click to exit");
                return true;
            } else {
                ActivityCollector.finishAllActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

