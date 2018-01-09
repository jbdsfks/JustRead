package justread.lzj.com.justread.thirdparty.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;

import justread.lzj.com.justread.thirdparty.magicindicator.ArgbEvaluatorHolder;


/**
 * 两种颜色过渡的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class ColorTransitionPagerTitleView extends SimplePagerTitleView {

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onSelected(int index) {
    }

    @Override
    public void onDeselected(int index) {
    }

    @Override
    public void onLeave(int index, float leavePercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        setTextColor(color);
    }

    @Override
    public void onEnter(int index, float enterPercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        setTextColor(color);
    }
}
