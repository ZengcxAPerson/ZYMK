package top.wzmyyj.zymk.model.db;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import top.wzmyyj.wzm_sdk.utils.TimeUtil;
import top.wzmyyj.zymk.app.bean.BookBean;
import top.wzmyyj.zymk.app.bean.FavorBean;
import top.wzmyyj.zymk.app.data.Urls;
import top.wzmyyj.zymk.greendao.gen.FavorDbDao;
import top.wzmyyj.zymk.model.db.dao.FavorDb;
import top.wzmyyj.zymk.model.db.utils.DaoManager;
import top.wzmyyj.zymk.model.net.box.ComicBox;
import top.wzmyyj.zymk.model.net.service.ComicService;
import top.wzmyyj.zymk.model.net.utils.ReOk;

/**
 * Created by yyj on 2018/08/01. email: 2209011667@qq.com
 */
public class FavorModel {

    private final FavorDbDao mDao;

    public FavorModel(Context context) {
        mDao = DaoManager.getInstance(context).getDaoSession().getFavorDbDao();
    }

    private List<FavorBean> db2beanList(List<FavorDb> DbList) {
        List<FavorBean> list = new ArrayList<>();
        if (DbList != null && DbList.size() > 0) {
            for (FavorDb db : DbList) {
                list.add(db2bean(db));
            }
        }
        return list;
    }

    private FavorBean db2bean(FavorDb db) {
        FavorBean bean = new FavorBean();
        BookBean book = new BookBean();
        book.setId(db.getId().intValue());
        book.setTitle(db.getTitle());
        book.setChapter(db.getChapter_name());
        book.setChapterId(db.getChapter_id());
        book.setUpdateTime(db.getUpdate_time());
        bean.setBook(book);
        bean.setUnRead(db.getIsUnRead());
        return bean;
    }

    public void loadAll(Observer<List<FavorBean>> observer) {
        Observable.create((ObservableOnSubscribe<List<FavorBean>>) observableEmitter -> {
            try {
                List<FavorDb> list = mDao.loadAll();
                List<FavorBean> data = db2beanList(list);
                observableEmitter.onNext(data);
            } catch (Exception e) {
                observableEmitter.onError(e);
                e.printStackTrace();
            } finally {
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    public void insert(final BookBean book, Observer<FavorBean> observer) {
        Observable.create((ObservableOnSubscribe<FavorBean>) observableEmitter -> {
            try {
                // 查询原来是否已有。
                FavorDb dd = mDao.load((long) book.getId());
                // 有的话，直接返回。
                if (dd != null) {
                    observableEmitter.onNext(new FavorBean());//内容为空的对象。
                } else {
                    // 插入。
                    FavorDb db = new FavorDb((long) book.getId(), book.getTitle(), book.getUpdateTime(),
                            book.getChapter(), book.getChapterId(), false);
                    mDao.insert(db);
                    // 转化。
                    FavorBean bean = db2bean(db);
                    observableEmitter.onNext(bean);
                }
            } catch (Exception e) {
                observableEmitter.onError(e);
                e.printStackTrace();
            } finally {
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void delete(final Long[] ids, Observer<Boolean> observer) {
        Observable.create((ObservableOnSubscribe<Boolean>) observableEmitter -> {
            try {
                mDao.deleteByKeyInTx(ids);
                observableEmitter.onNext(true);
            } catch (Exception e) {
                observableEmitter.onError(e);
                e.printStackTrace();
            } finally {
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    // 判断是否收藏，同时设为已读。
    public void isFavor(final Long id, Observer<Boolean> observer) {
        Observable.create((ObservableOnSubscribe<Boolean>) observableEmitter -> {
            try {
                // 查询原来是否已有。
                FavorDb dd = mDao.load(id);
                // 有的话，返回true，否则false。
                if (dd != null) {
                    // 设为已读。
                    dd.setIsUnRead(false);
                    mDao.update(dd);
                    observableEmitter.onNext(true);
                } else {
                    observableEmitter.onNext(false);
                }
            } catch (Exception e) {
                observableEmitter.onError(e);
                e.printStackTrace();
            } finally {
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    // 全部设为已读。
    public void setALLRead(Observer<Boolean> observer) {
        Observable.create((ObservableOnSubscribe<Boolean>) observableEmitter -> {
            try {
                List<FavorDb> list = mDao.loadAll();
                for (FavorDb dd : list) {
                    dd.setIsUnRead(false);
                    mDao.update(dd);
                }
                observableEmitter.onNext(true);
            } catch (Exception e) {
                observableEmitter.onError(e);
                e.printStackTrace();
            } finally {
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    //更新数据。
    @SuppressLint("CheckResult")
    public void updateAll(Observer<FavorBean> observer) {
        Observable.just(0)
                .flatMap((Function<Integer, Observable<FavorBean>>) integer -> {
                    List<FavorDb> list = mDao.loadAll();
                    List<FavorBean> data = db2beanList(list);
                    return Observable.fromIterable(data);
                })
                .map(favorBean -> {
                    int comic_id = favorBean.getBook().getId();
                    Gson gson = new GsonBuilder().registerTypeAdapter(ComicBox.class, new ComicBox.Deserializer2()).create();
                    Retrofit retrofit = ReOk.bind(Urls.ZYMK_BaseApi, gson);
                    ComicService service = retrofit.create(ComicService.class);
                    Observable<ComicBox> observable = service.getComic(comic_id);
                    return observable.blockingFirst();
                })
                .map(comicBox -> {
                    BookBean book = comicBox.getBook();
                    FavorDb db = new FavorDb((long) book.getId(), book.getTitle(), TimeUtil.getTime(),
                            book.getChapter(), book.getChapterId(), true);// 新添加的数据设为未读。

                    FavorDb dd = mDao.load((long) book.getId());
                    if (dd.getChapter_id() != db.getChapter_id()) {// 最新章节ID不一样则更新数据。
                        mDao.update(db);
                        return db2bean(db);
                    } else {
                        return db2bean(dd);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}

