package top.wzmyyj.zymk.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.wzmyyj.zymk.app.bean.BookBean;
import top.wzmyyj.zymk.app.bean.ChapterBean;
import top.wzmyyj.zymk.app.bean.ComicBean;
import top.wzmyyj.zymk.app.bean.HistoryBean;
import top.wzmyyj.zymk.app.data.Urls;
import top.wzmyyj.zymk.app.event.HistoryListChangeEvent;
import top.wzmyyj.zymk.app.helper.IntentHelper;
import top.wzmyyj.zymk.base.presenter.BasePresenter;
import top.wzmyyj.zymk.contract.ComicContract;
import top.wzmyyj.zymk.model.db.HistoryModel;
import top.wzmyyj.zymk.model.net.ComicModel;
import top.wzmyyj.zymk.model.net.box.ComicBox;

/**
 * Created by yyj on 2018/08/01. email: 2209011667@qq.com
 */
public class ComicPresenter extends BasePresenter<ComicContract.IView> implements ComicContract.IPresenter {

    private final ComicModel mModel;
    private final HistoryModel historyModel;

    public ComicPresenter(Activity activity, ComicContract.IView iv) {
        super(activity, iv);
        mModel = new ComicModel();
        historyModel = new HistoryModel(activity);
    }

    @Override
    public long getChapterId() {
        return mActivity.getIntent().getLongExtra("chapter_id", 0);
    }

    @Override
    public void loadData() {
        int id = mActivity.getIntent().getIntExtra("comic_id", 0);
        mModel.getComicInfo(id, new BaseObserver<ComicBox>() {
            @Override
            public void onNext(@NonNull ComicBox box) {
                int status = box.getStatus();
                if (status == 0) {
                    List<ChapterBean> chapterList = box.getChapterList();
                    Collections.reverse(chapterList);// 反序
                    List<ComicBean> comicList = getComicData(chapterList);
                    mView.showData(box.getBook(), chapterList, box.getBookList(), comicList);
                } else {
                    mView.showLoadFail(box.getMsg());
                    mView.showToast(box.getMsg());
                }
//                mView.showToast("加载成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.showLoadFail(e.getMessage());
                mView.showToast("Error:" + e.getMessage());
            }
        });
    }

    private List<ComicBean> getComicData(List<ChapterBean> chapterList) {
        List<ComicBean> comicList = new ArrayList<>();
        if (chapterList == null || chapterList.size() == 0) return comicList;
        for (ChapterBean chapter : chapterList) {
            int start = chapter.getStartVar();
            int end = chapter.getEndVar();
//            chapter.setPrice(0);
            for (int i = start; i <= end; i++) {
                ComicBean comic = new ComicBean();
                comic.setChapterId(chapter.getChapterId());
                comic.setChapterName(chapter.getChapterName());
                comic.setPrice(chapter.getPrice());
                comic.setImgHigh(chapter.getImageHigh(i));
                comic.setImgMiddle(chapter.getImageMiddle(i));
                comic.setImgLow(chapter.getFirstImageLow(i));
                comic.setVar(i);
                comic.setVarSize(end - start + 1);
                comicList.add(comic);
            }
        }
        return comicList;
    }

    @Override
    public void goDetails(String href) {
        if (href.contains(Urls.ZYMK_Base)) {
            IntentHelper.toDetailsActivity2(mActivity, href);
        } else {
            IntentHelper.toBrowser(mActivity, href);
        }
    }

    @Override
    public void goSetting() {
        IntentHelper.toSettingActivity(mActivity);
    }

    @Override
    public void saveHistory(BookBean book, ChapterBean chapter) {
        historyModel.insert(book, chapter, new BaseObserver<HistoryBean>() {
            @Override
            public void onNext(@NonNull HistoryBean historyBean) {
                if (historyBean == null) {
                    mView.showToast("保存失败！");
                    return;
                }
                EventBus.getDefault().post(new HistoryListChangeEvent(true));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.showToast("Error:" + e.getMessage());
            }
        });
    }
}
