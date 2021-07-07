package top.wzmyyj.zymk.view.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import top.wzmyyj.wzm_sdk.adapter.ivd.IVD;
import top.wzmyyj.wzm_sdk.adapter.ivd.SingleIVD;
import top.wzmyyj.wzm_sdk.tools.T;
import top.wzmyyj.zymk.R;
import top.wzmyyj.zymk.app.bean.BookBean;
import top.wzmyyj.zymk.app.bean.ChapterBean;
import top.wzmyyj.zymk.app.bean.ComicBean;
import top.wzmyyj.zymk.app.helper.GlideLoaderHelper;
import top.wzmyyj.zymk.base.panel.BaseRecyclerPanel;
import top.wzmyyj.zymk.contract.ComicContract;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * Created by yyj on 2018/08/06. email: 2209011667@qq.com
 * 漫画阅读主页面。
 */
@SuppressLint("NonConstantResourceId")
public class ComicRecyclerPanel extends BaseRecyclerPanel<ComicBean, ComicContract.IPresenter> {

    private final static int Definition_Low = 1;
    private final static int Definition_Middle = 2;
    private final static int Definition_High = 3;
    private final static int Add_Previous = 1;
    private final static int Add_After = 2;
    private ComicMenuPanel mMenuPanel;
    private ComicLoadPosePanel mLoadPosePanel;
    private int definition = Definition_Middle;
    private long mChapterId = 0;
    private boolean isScrollByCatalog = false;
    private BookBean mBook;
    private final List<ChapterBean> mChapterList = new ArrayList<>();
    private final List<BookBean> mBookList = new ArrayList<>();
    private final List<ComicBean> mComicList = new ArrayList<>();
    private long chapterIdPrevious;
    private long chapterIdAfter;
    private final MyRunnable myRunnable = new MyRunnable();
    private final Handler scrollHandler = new Handler();

    public ComicRecyclerPanel(Context context, ComicContract.IPresenter comicPresenter) {
        super(context, comicPresenter);
    }

    @Override
    protected void initPanels() {
        super.initPanels();
        addPanels(
                mMenuPanel = new ComicMenuPanel(context, mPresenter),
                mLoadPosePanel = new ComicLoadPosePanel(context, mPresenter)
        );
        mMenuPanel.setComicRecyclerPanel(this);
    }

    public void setDefinitionLow() {
        this.definition = Definition_Low;
        notifyItemShowRangeChanged();
        T.s("已切换到流畅画质");
    }

    public void setDefinitionMiddle() {
        this.definition = Definition_Middle;
        notifyItemShowRangeChanged();
        T.s("已切换到标清画质");
    }

    public void setDefinitionHigh() {
        this.definition = Definition_High;
        notifyItemShowRangeChanged();
        T.s("已切换到高清画质");
    }

    public long getChapterId() {
        return mChapterId;
    }

    public void postAutoScroll() {
        scrollHandler.postDelayed(myRunnable, 5);
    }

    public MyRunnable getMyRunnable() {
        return myRunnable;
    }

    // mBsb主动控制mRecyclerView滑动。
    public void progressChanged(int p) {
        // 滑很快的话，holder可能等于null。返回NO_POSITION=-1。
        int a = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(0));
        if (a == -1) return;// 防止越界。
        ComicBean comic = mData.get(a);
        int var = comic.getVar();
        int g = p - var;
        if (g == 0) return;// 防止无意义操作。
        int c = a + g;
        scrollToPosition(c);
    }

    public void goDetails() {
        mPresenter.goDetails(mBook.getHref());
    }

    public void loadFail() {
        mLoadPosePanel.loadFail();
    }

    @Override
    protected void initView() {
        super.initView();
        mFrameLayout.addView(getPanelView(0));
        mFrameLayout.addView(getPanelView(1));
        // 消除mRecyclerView刷新的动画。
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void initData() {
        super.initData();
        mChapterId = mPresenter.getChapterId();
    }

    @Override
    protected void setIVD(List<IVD<ComicBean>> ivd) {
        ivd.add(new SingleIVD<ComicBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.layout_comic_image;
            }

            @Override
            public void convert(ViewHolder holder, ComicBean comicBean, int position) {
                ImageView img_comic = holder.getView(R.id.img_comic);
                if (comicBean.getChapterId() == -1) {
                    GlideLoaderHelper.imgFix(context, R.mipmap.pic_comic_end, img_comic);
                    return;
                }
                if (comicBean.getPrice() == 0) {
                    switch (definition) {
                        case Definition_Low:
                            GlideLoaderHelper.imgFix(context, comicBean.getImgLow(), img_comic);
                            break;
                        case Definition_Middle:
                            GlideLoaderHelper.imgFix(context, comicBean.getImgMiddle(), img_comic);
                            break;
                        case Definition_High:
                            GlideLoaderHelper.imgFix(context, comicBean.getImgHigh(), img_comic);
                            break;
                    }
                } else {
                    GlideLoaderHelper.imgFix(context, R.mipmap.pic_need_money, img_comic);
                }
            }
        });
    }

    @Override
    public void viewRecycled(@NonNull ViewHolder holder) {
        GlideLoaderHelper.clear(context, holder.getView(R.id.img_comic));
        super.viewRecycled(holder);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // 当前屏幕显示最上面一行的position。
            private int loadPositionNow = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mMenuPanel.isAuto() && recyclerView.getScrollState() != SCROLL_STATE_SETTLING) {
                    if (dy > 10) {
                        mMenuPanel.closeMenu();
                    } else if (dy < -10) {
                        mMenuPanel.showMenu();
                    }
                }
                int p = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(0));
                if (p == -1 || p == loadPositionNow) return;
                loadPositionNow = p;
                mMenuPanel.setMenu(mData.get(p));
                long chapterId = mData.get(p).getChapterId();
                if (mChapterId != chapterId && !isScrollByCatalog) {
                    mChapterId = chapterId;
                    mMenuPanel.scrollCatalog();
                }
                if (loadPositionNow < 3) {
                    mHandler.sendEmptyMessage(Add_Previous);
                } else if (loadPositionNow > mData.size() - 5) {
                    mHandler.sendEmptyMessage(Add_After);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int w = msg.what;
            if (w == Add_Previous) {
                addPrevious();
            } else if (w == Add_After) {
                addAfter();
            } else {
                mHandler.removeMessages(Add_Previous);
                mHandler.removeMessages(Add_After);
            }
        }
    };

    @Override
    public void update() {
        mPresenter.loadData();
    }

    public void setComicData(BookBean book, List<ChapterBean> chapterList, List<BookBean> bookList, List<ComicBean> comicList) {
        mLoadPosePanel.loadSuccess();
        if (book != null) {
            mBook = book;
        }
        if (chapterList != null && chapterList.size() > 0) {
            mChapterList.clear();
            mChapterList.addAll(chapterList);
        }
        if (bookList != null && bookList.size() > 0) {
            // 暂时没用。
            mBookList.clear();
            mBookList.addAll(bookList);
            mPresenter.log("mBookList Size:" + mBookList.size());
        }
        if (comicList != null && comicList.size() > 0) {
            mComicList.clear();
            mComicList.addAll(comicList);
        }
        if (mChapterId == 0) {
            mChapterId = mChapterList.get(0).getChapterId();
        }
        addEnd();
        addOnce();
        mHandler.sendEmptyMessageDelayed(Add_Previous, 500);
        mMenuPanel.setCatalogChapterList(chapterList);
    }

    // 增加最后结束语。
    private void addEnd() {
        ComicBean comic = new ComicBean();//增加一张结束语。
        comic.setChapterId(-1);
        comic.setVar(1);
        comic.setVarSize(1);
        mComicList.add(comic);
        ChapterBean last = new ChapterBean();
        last.setChapterId(-1);
        mChapterList.add(last);//增加一章结束语。
    }

    // 第一次添加数据。
    private void addOnce() {
        long chapter_id = mChapterId;
        chapterIdPrevious = 0;
        chapterIdAfter = 0;
        List<ComicBean> comicList = new ArrayList<>();
        for (ComicBean comic : mComicList) {
            if (comic.getChapterId() == chapter_id) {
                comicList.add(comic);
            }
        }
        mData.clear();
        mData.addAll(comicList);
        notifyDataSetChanged();
        mMenuPanel.setMenu(mData.get(0));
        // 找上一章和下一章的ID
        for (int i = 0; i < mChapterList.size(); i++) {
            if (mChapterList.get(i).getChapterId() == chapter_id) {
                if (i > 0) {
                    chapterIdPrevious = mChapterList.get(i - 1).getChapterId();
                }
                if (i < mChapterList.size() - 1) {
                    chapterIdAfter = mChapterList.get(i + 1).getChapterId();
                }
                break;
            }
        }
    }

    // 向前加载一章。
    private void addPrevious() {
        if (chapterIdPrevious == 0) return;
        long previous = chapterIdPrevious;
        chapterIdPrevious = 0;
        List<ComicBean> comicList = new ArrayList<>();
        for (ComicBean comic : mComicList) {
            if (comic.getChapterId() == previous) {
                comicList.add(comic);
            }
        }
        mData.addAll(0, comicList);
        mHeaderAndFooterWrapper.notifyItemRangeInserted(0, comicList.size());
        // 找上一章ID
        for (int i = 0; i < mChapterList.size(); i++) {
            if (mChapterList.get(i).getChapterId() == previous) {
                if (i > 0) {
                    chapterIdPrevious = mChapterList.get(i - 1).getChapterId();
                }
                break;
            }
        }
    }

    // 向后加载一章。
    private void addAfter() {
        if (chapterIdAfter == 0) return;
        long after = chapterIdAfter;
        chapterIdAfter = 0;
        List<ComicBean> comicList = new ArrayList<>();
        for (ComicBean comic : mComicList) {
            if (comic.getChapterId() == after) {
                comicList.add(comic);
            }
        }
        mData.addAll(comicList);
        notifyDataSetChanged();
        // 找下一章ID
        for (int i = 0; i < mChapterList.size(); i++) {
            if (mChapterList.get(i).getChapterId() == after) {
                if (i < mChapterList.size() - 1) {
                    chapterIdAfter = mChapterList.get(i + 1).getChapterId();
                }
                break;
            }
        }
    }

    // 前往指定的章节。
    public void goSeeChapterById(long id) {
        mChapterId = id;
        int p = -1;
        for (int i = 0; i < mData.size(); i++) {// mData中查找指定章节。
            if (mData.get(i).getChapterId() == id) {
                p = i;
                break;
            }
        }
        if (p == -1) {// mData中没有所需章节。
            addOnce();
            return;
        }
        // 滑到指定章节。
        isScrollByCatalog = true;
        scrollToPosition(p);
    }

    // mRecyclerView滑到指定position的位置。
    private void scrollToPosition(int p) {
        if (p < 0 || p > mData.size() - 1) return;// 防止越界。
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(p, 0);
        isScrollByCatalog = false;
    }

    private void notifyItemShowRangeChanged() {
        // 只刷新当前显示的item，防止图片跳闪。
        int a = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(0));
        int b = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (a == -1 || b == -1) return;
        mHeaderAndFooterWrapper.notifyItemRangeChanged(a, b);
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        super.onItemClick(view, holder, position);
        mMenuPanel.clickSome();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 保存阅读记录。
        for (ChapterBean chapter : mChapterList) {
            if (chapter.getChapterId() == mChapterId) {
                mPresenter.saveHistory(mBook, chapter);
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.sendEmptyMessage(0);
        scrollHandler.removeCallbacks(myRunnable);
    }

    public class MyRunnable implements Runnable {
        int a, b, c;

        @Override
        public void run() {
            if (a >= b || !mRecyclerView.canScrollVertically(1)) {
                a = b = c = 0;
                scrollHandler.removeCallbacks(myRunnable);
                return;
            } else {
                a += c;
            }
            int scroll = mRecyclerView.getScrollY();
            mRecyclerView.scrollBy(0, scroll + c);
            postAutoScroll();
        }
    }
}
