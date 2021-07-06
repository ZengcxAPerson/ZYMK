package top.wzmyyj.zymk.base.panel;

import android.content.Context;

import com.youth.banner.Banner;

import java.util.List;

import top.wzmyyj.wzm_sdk.panel.BannerPanel;
import top.wzmyyj.zymk.base.contract.IBasePresenter;

/**
 * Created by yyj on 2018/09/19. email: 2209011667@qq.com
 */
public abstract class BaseBannerPanel<P extends IBasePresenter> extends BannerPanel {

    protected P mPresenter;

    public BaseBannerPanel(Context context, P p) {
        super(context);
        this.mPresenter = p;
        checkPresenterIsNull();
        this.mPresenter.log(this.getClass().getSimpleName() + "is created!");
    }

    private void checkPresenterIsNull() {
        if (mPresenter == null) {
            throw new IllegalStateException("please init mPresenter in initPresenter() method ");
        }
    }

    @Override
    protected void setBanner(Banner banner) {
        super.setBanner(banner);
    }

    @Override
    protected void setData(List<Object> images, List<String> titles) {
        super.setData(images, titles);
    }
}
