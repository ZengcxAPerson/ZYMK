package top.wzmyyj.zymk.view.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import top.wzmyyj.zymk.R;
import top.wzmyyj.zymk.contract.ComicContract;
import top.wzmyyj.zymk.base.panel.BasePanel;

/**
 * Created by yyj on 2018/08/06. email: 2209011667@qq.com
 */
@SuppressLint("NonConstantResourceId")
public class ComicLoadPosePanel extends BasePanel<ComicContract.IPresenter> {

    @BindView(R.id.img_load_pose)
    ImageView imgLoadPose;
    @BindView(R.id.tv_load_pose)
    TextView tvLoadPose;

    private int count = 0;
    private int status = -1;

    public ComicLoadPosePanel(Context context, ComicContract.IPresenter p) {
        super(context, p);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_comic_loadpose;
    }

    @OnClick(R.id.tv_load_pose)
    public void reLoad() {
        if (status == -1) {
            mHandler.sendEmptyMessage(1);
            mPresenter.loadData();
            tvLoadPose.setText("正在加载中。。。");
            tvLoadPose.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    protected void initData() {
        super.initData();
        showLoad();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    imgLoadPose.setImageResource(R.mipmap.pic_load_pose1);
                    mHandler.sendEmptyMessageDelayed(2, 100);
                    break;
                case 2:
                    imgLoadPose.setImageResource(R.mipmap.pic_loadpose2);
                    mHandler.sendEmptyMessageDelayed(1, 100);
                    break;
            }
            count++;
            if (status == 0 && count > 18) {
                mHandler.removeMessages(0);
                view.setVisibility(View.GONE);
            }
        }
    };

    public void showLoad() {
        view.setVisibility(View.VISIBLE);
        count = 0;
        status = -1;
        mHandler.sendEmptyMessage(1);
    }

    public void loadSuccess() {
        status = 0;
    }

    public void loadFail() {
        mHandler.removeMessages(0);
        tvLoadPose.setText("加载失败，点击重试！");
        tvLoadPose.setTextColor(context.getResources().getColor(R.color.colorRed));
        imgLoadPose.setImageResource(R.mipmap.pic_load_error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }
}
