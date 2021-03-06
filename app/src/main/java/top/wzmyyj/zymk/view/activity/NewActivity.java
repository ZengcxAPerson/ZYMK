package top.wzmyyj.zymk.view.activity;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import top.wzmyyj.wzm_sdk.adapter.ViewTitlePagerAdapter;
import top.wzmyyj.wzm_sdk.panel.Panel;
import top.wzmyyj.zymk.R;
import top.wzmyyj.zymk.app.bean.BookBean;
import top.wzmyyj.zymk.base.activity.BaseActivity;
import top.wzmyyj.zymk.contract.NewContract;
import top.wzmyyj.zymk.presenter.NewPresenter;
import top.wzmyyj.zymk.view.panel.NewRecyclerPanel;

/**
 * Created by yyj on 2018/07/13. email: 2209011667@qq.com
 */
@SuppressLint("NonConstantResourceId")
public class NewActivity extends BaseActivity<NewContract.IPresenter> implements NewContract.IView {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void initPresenter() {
        mPresenter = new NewPresenter(activity, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new;
    }

    @Override
    protected void initPanels() {
        super.initPanels();
        addPanels(
                new NewRecyclerPanel(context, mPresenter).setTitle("最新上架"),
                new NewRecyclerPanel(context, mPresenter).setTitle("最新更新")
        );
    }

    @OnClick(R.id.img_back)
    void back() {
        mPresenter.finish();
    }

    @Override
    protected void initView() {
        super.initView();
        List<View> viewList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (Panel p : mPanelManager.getPanelList()) {
            viewList.add(p.getView());
            titles.add(p.getTitle());
        }
        ViewTitlePagerAdapter pagerAdapter = new ViewTitlePagerAdapter(viewList, titles);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.loadData();
    }

    @Override
    public void showData(List<BookBean> bookList1, List<BookBean> bookList2) {
        ((NewRecyclerPanel) getPanel(0)).setNewData(bookList1);
        ((NewRecyclerPanel) getPanel(1)).setNewData(bookList2);
    }
}
