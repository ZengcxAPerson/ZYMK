package top.wzmyyj.zymk.view.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import top.wzmyyj.wzm_sdk.tools.A;
import top.wzmyyj.wzm_sdk.utils.DensityUtil;
import top.wzmyyj.wzm_sdk.utils.MockUtil;
import top.wzmyyj.zymk.R;
import top.wzmyyj.zymk.app.bean.ChapterBean;
import top.wzmyyj.zymk.app.bean.ComicBean;
import top.wzmyyj.zymk.app.helper.GlideLoaderHelper;
import top.wzmyyj.zymk.base.panel.BasePanel;
import top.wzmyyj.zymk.contract.ComicContract;

/**
 * Created by yyj on 2018/08/06. email: 2209011667@qq.com
 * 漫画阅读菜单面板。
 */
@SuppressLint("NonConstantResourceId")
public class ComicMenuPanel extends BasePanel<ComicContract.IPresenter> {

    @BindView(R.id.fl_top)
    FrameLayout flTop;
    @BindView(R.id.tv_chapter_name)
    TextView tvChapterName;
    @BindView(R.id.tv_chapter_var)
    TextView tvChapterVar;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.ll_auto)
    LinearLayout llAuto;
    @BindView(R.id.bsb_auto)
    BubbleSeekBar bsbAuto;
    @BindView(R.id.ll_progress)
    LinearLayout llProgress;
    @BindView(R.id.bsb_1)
    BubbleSeekBar mBsb;
    @BindView(R.id.tv_chapter_var2)
    TextView tvChapterVar2;
    @BindView(R.id.rv_catalog)
    RecyclerView rvCatalog;
    @BindView(R.id.img_auto)
    ImageView imgAuto;
    @BindView(R.id.img_definition)
    ImageView imgDefinition;
    @BindView(R.id.ll_definition)
    LinearLayout llDefinition;
    @BindView(R.id.ll_brightness)
    LinearLayout llBrightness;
    @BindView(R.id.v_brightness)
    View vBrightness;
    @BindView(R.id.bsb_brightness)
    BubbleSeekBar bsbBrightness;
    @BindView(R.id.img_catalog_xu)
    ImageView imgCatalogXu;
    @BindView(R.id.tv_catalog_xu)
    TextView tvCatalogXu;

    private ComicRecyclerPanel comicRecyclerPanel;
    private ComicRecyclerPanel.MyRunnable myRunnable;
    private final List<ChapterBean> mCatalogChapterList = new ArrayList<>();
    private CommonAdapter<ChapterBean> mCatalogAdapter;
    private boolean isBsbOnTouch = false;
    private boolean isShow = false;
    private boolean isAuto = false;
    private boolean isShowMenuBrightness = false;
    private boolean isShowMenuDefinition = false;
    private boolean isShowMenuCatalog = false;
    private int xu = 1;

    public ComicMenuPanel(Context context, ComicContract.IPresenter p) {
        super(context, p);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_comic_menu;
    }

    public void setComicRecyclerPanel(ComicRecyclerPanel comicRecyclerPanel) {
        this.comicRecyclerPanel = comicRecyclerPanel;
        this.myRunnable = comicRecyclerPanel.getMyRunnable();
    }

    @OnClick(R.id.img_back)
    public void back() {
        mPresenter.finish();
    }

    // 跳转到设置页面。
    @OnClick(R.id.ll_menu_1)
    public void menu_1() {
        mPresenter.goSetting();
    }

    // 设置自动滑动和停止。
    @OnClick(R.id.ll_menu_2)
    public void menu_2() {
        if (isAuto) {
            stopAuto();
        } else {
            startAuto();
        }
    }

    private void startAuto() {
        if (isAuto) return;
        isAuto = true;
        llAuto.setVisibility(View.VISIBLE);
        llProgress.setVisibility(View.GONE);
        imgAuto.setImageResource(R.mipmap.ic_read_stop);
        myRunnable.a = 0;
        myRunnable.b = Integer.MAX_VALUE;
        myRunnable.c = DensityUtil.dp2px(context, bsbAuto.getProgressFloat() * 2);
        comicRecyclerPanel.postScroll();
    }

    private void stopAuto() {
        if (!isAuto) return;
        isAuto = false;
        llAuto.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);
        imgAuto.setImageResource(R.mipmap.ic_read_start);
        myRunnable.b = 0;
    }

    public boolean isAuto() {
        return isAuto;
    }

    // 设置画质。
    @OnClick(R.id.ll_menu_3)
    public void menu_3() {
        if (isShowMenuDefinition) {
            closeMenuDefinition();
        } else {
            showMenuDefinition();
        }
    }

    public void showMenuDefinition() {
        if (isShowMenuDefinition) return;
        isShowMenuDefinition = true;
        toggleMenuDefinition();
    }

    public void closeMenuDefinition() {
        if (!isShowMenuDefinition) return;
        isShowMenuDefinition = false;
        toggleMenuDefinition();
    }

    private void toggleMenuDefinition() {
        int h = llDefinition.getHeight();
        int fromY = 0, toY = 0;
        float fromA = 1.0f, toA = 1.0f;
        if (isShowMenuDefinition) {
            fromY = -h / 2;
            fromA = 0.0f;
            llDefinition.setVisibility(View.VISIBLE);
        } else {
            toY = -h / 2;
            toA = 0.0f;
            llDefinition.setVisibility(View.GONE);
        }
        A.create()
                .t(0, 0, fromY, toY)
                .a(fromA, toA)
                .duration(300)
                .listener(new A.AListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!isShowMenuDefinition) llDefinition.setVisibility(View.GONE);
                    }
                })
                .into(llDefinition);
    }

    // 流畅画质
    @OnClick(R.id.img_definition_low)
    public void setDefinitionLow() {
        comicRecyclerPanel.setDefinitionLow();
        imgDefinition.setImageResource(R.mipmap.ic_read_definition_low);
        closeMenuDefinition();
    }

    // 标清画质
    @OnClick(R.id.img_definition_middle)
    public void setDefinitionMiddle() {
        comicRecyclerPanel.setDefinitionMiddle();
        imgDefinition.setImageResource(R.mipmap.ic_read_definition_middle);
        closeMenuDefinition();
    }

    // 高清画质
    @OnClick(R.id.img_definition_high)
    public void setDefinitionHigh() {
        comicRecyclerPanel.setDefinitionHigh();
        imgDefinition.setImageResource(R.mipmap.ic_read_definition_high);
        closeMenuDefinition();
    }

    // 设置亮度
    @OnClick(R.id.ll_menu_4)
    public void menu_4() {
        if (isShowMenuBrightness) {
            closeMenuBrightness();
        } else {
            showMenuBrightness();
        }
    }

    public void showMenuBrightness() {
        if (isShowMenuBrightness) return;
        isShowMenuBrightness = true;
        toggleMenuBrightness();

    }

    public void closeMenuBrightness() {
        if (!isShowMenuBrightness) return;
        isShowMenuBrightness = false;
        toggleMenuBrightness();
    }

    private void toggleMenuBrightness() {
        int h = llBrightness.getHeight();
        int fromY = 0, toY = 0;
        float fromA = 1.0f, toA = 1.0f;
        if (isShowMenuBrightness) {
            fromY = -h / 2;
            fromA = 0.0f;
            llBrightness.setVisibility(View.VISIBLE);
        } else {
            toY = -h / 2;
            toA = 0.0f;
            llBrightness.setVisibility(View.GONE);
        }
        A.create()
                .t(0, 0, fromY, toY)
                .a(fromA, toA)
                .duration(300)
                .listener(new A.AListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!isShowMenuBrightness) llBrightness.setVisibility(View.GONE);
                    }
                })
                .into(llBrightness);
    }

    @OnClick(R.id.ll_menu_5)
    public void menu_5() {
        if (!isShowMenuCatalog) {
            closeMenu();
            showMenuCatalog();
        }
    }

    public void showMenuCatalog() {
        if (isShowMenuCatalog) return;
        isShowMenuCatalog = true;
        scrollCatalog();
        toggleMenuCatalog();
    }

    public void closeMenuCatalog() {
        if (!isShowMenuCatalog) return;
        isShowMenuCatalog = false;
        toggleMenuCatalog();
    }

    public void setCatalogChapterList(List<ChapterBean> list) {
        mCatalogChapterList.clear();
        mCatalogChapterList.addAll(list);
        mCatalogAdapter.notifyDataSetChanged();
    }

    public void scrollCatalog() {
        mCatalogAdapter.notifyDataSetChanged();
        int j = mCatalogChapterList.size() - 1;
        for (int i = 0; i < mCatalogChapterList.size(); i++) {
            if (mCatalogChapterList.get(i).getChapterId() == comicRecyclerPanel.getChapterId()) {
                j = i;
                break;
            }
        }
        int p = Math.max(j - 3, 0);
        if (p > mCatalogChapterList.size() - 1) return;// 防止越界。
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) rvCatalog.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(p, 0);
    }

    private void toggleMenuCatalog() {
        int w = ll_catalog.getWidth();
        int fromX = 0, toX = 0;
        if (isShowMenuCatalog) {
            fromX = w;
            ll_catalog.setVisibility(View.VISIBLE);
        } else {
            toX = w;
            ll_catalog.setVisibility(View.GONE);
        }
        A.create()
                .t(fromX, toX, 0, 0)
                .duration(300)
                .listener(new A.AListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!isShowMenuCatalog) ll_catalog.setVisibility(View.GONE);
                    }
                })
                .into(ll_catalog);
    }

    @BindView(R.id.ll_catalog)
    LinearLayout ll_catalog;

    @OnClick({R.id.img_catalog_all, R.id.tv_catalog_all})
    public void catalog_all() {
        comicRecyclerPanel.goDetails();
        mPresenter.finish();
    }

    @OnClick({R.id.img_catalog_xu, R.id.tv_catalog_xu})
    public void catalog_xu() {
        if (xu == 1) {
            // 转为倒序。
            xu = -1;
            imgCatalogXu.setImageResource(R.mipmap.ic_read_catalog_reverse);
            tvCatalogXu.setText("倒序");
        } else {
            // 转为正序。
            xu = 1;
            imgCatalogXu.setImageResource(R.mipmap.ic_read_catalog_order);
            tvCatalogXu.setText("正序");
        }
        Collections.reverse(mCatalogChapterList);
        mCatalogAdapter.notifyDataSetChanged();
    }

    private void closeChildMenu() {
        closeMenuDefinition();
        closeMenuBrightness();
        closeMenuCatalog();
    }

    @Override
    protected void initView() {
        super.initView();
        flTop.setVisibility(View.INVISIBLE);
        llBottom.setVisibility(View.INVISIBLE);
        llDefinition.setVisibility(View.INVISIBLE);
        llBrightness.setVisibility(View.INVISIBLE);
        ll_catalog.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();
        rvCatalog.setLayoutManager(new LinearLayoutManager(context));
        mCatalogAdapter = new CommonAdapter<ChapterBean>(context, R.layout.layout_catalog_item, mCatalogChapterList) {
            @Override
            protected void convert(ViewHolder holder, ChapterBean chapter, int position) {
                ImageView img_pic = holder.getView(R.id.img_catalog_pic);
                TextView tv_name = holder.getView(R.id.tv_catalog_name);
                LinearLayout ll_bg = holder.getView(R.id.ll_catalog_bg);
                if (chapter.getChapterId() == comicRecyclerPanel.getChapterId()) {
                    ll_bg.setBackgroundResource(R.color.colorPrimary);
                } else {
                    ll_bg.setBackgroundResource(R.color.colorClarity);
                }
                if (chapter.getPrice() == 0) {
                    GlideLoaderHelper.img(context, chapter.getFirstImageLow(), img_pic);
                } else {
                    GlideLoaderHelper.img(context, R.mipmap.pic_need_money, img_pic);
                }
                tv_name.setText(chapter.getChapterName());
            }
        };
        rvCatalog.setAdapter(mCatalogAdapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        super.initListener();
        mBsb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isBsbOnTouch = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isBsbOnTouch = false;
                    break;
            }
            return false;
        });
        mBsb.setOnProgressChangedListener(new BubbleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if (isBsbOnTouch) {// 被点击时，判断是否由于自身被触摸而引发的改变。
                    comicRecyclerPanel.progressChanged(progress);
                }
            }
        });
        bsbAuto.setOnProgressChangedListener(new BubbleSeekBarChangedListener() {
            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                myRunnable.c = DensityUtil.dp2px(context, (float) 2 * progressFloat);
            }
        });
        bsbBrightness.setOnProgressChangedListener(new BubbleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                vBrightness.setAlpha(1 - progressFloat);
            }
        });
        mCatalogAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                long id = mCatalogChapterList.get(position).getChapterId();
                comicRecyclerPanel.goChChapterById(id);
                mCatalogAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    public void clickSome() {
        if (isShowMenuDefinition
                || isShowMenuBrightness
                || isShowMenuCatalog) {// 有子菜单打开先关闭子菜单。
            closeChildMenu();
            return;
        }
        if (isAuto) {// 自动播放时。
            changeMenu();
            return;
        }
        myRunnable.a = 0;
        myRunnable.b = MockUtil.getScreenHeight(context) / 2;
        myRunnable.c = MockUtil.getScreenHeight(context) / 30;
        comicRecyclerPanel.postScroll();
    }

    public void setMenu(ComicBean comic) {
        int max = comic.getVarSize();
        int p = comic.getVar();
        tvChapterName.setText(comic.getChapterName());
        String var = p + "/" + max;
        tvChapterVar.setText(var);
        tvChapterVar2.setText(var);
        if (isBsbOnTouch) return;// bsb_1正在被点击时，不设置它。
        mBsb.getConfigBuilder()
                .max(max)
                .min(1)
                .progress(p)
                .build();
    }

    public void showMenu() {
        if (isShow) return;
        isShow = true;
        toggleMenu();
        closeChildMenu();
    }

    public void closeMenu() {
        if (!isShow) return;
        isShow = false;
        toggleMenu();
        closeChildMenu();
    }

    public void changeMenu() {
        if (isShow) {
            closeMenu();
        } else {
            showMenu();
        }
    }

    private void toggleMenu() {
        int th = flTop.getHeight();
        int bh = llBottom.getHeight();
        int fromY1 = 0, toY1 = 0, fromY2 = 0, toY2 = 0;
        if (isShow) {
            fromY1 = -th;
            fromY2 = bh;
            flTop.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.VISIBLE);
        } else {
            toY1 = -th;
            toY2 = bh;
            flTop.setVisibility(View.GONE);
            llBottom.setVisibility(View.GONE);
        }
        A.create()
                .t(0, 0, fromY1, toY1)
                .duration(300)
                .listener(new A.AListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!isShow) flTop.setVisibility(View.GONE);
                    }
                })
                .into(flTop);
        A.create()
                .t(0, 0, fromY2, toY2)
                .duration(300)
                .listener(new A.AListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        if (!isShow) llBottom.setVisibility(View.GONE);
                    }
                })
                .into(llBottom);
    }

    interface BubbleSeekBarChangedListener extends BubbleSeekBar.OnProgressChangedListener {

        default void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }

        default void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }

        default void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }
    }
}
