package top.wzmyyj.zymk.app.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import jp.wasabeef.glide.transformations.BlurTransformation;
import top.wzmyyj.wzm_sdk.utils.MockUtil;
import top.wzmyyj.zymk.R;

/**
 * Created by yyj on 2018/07/09. email: 2209011667@qq.com
 * Glide 图片加载器的封装类。
 */
public class GlideLoaderHelper {

    public static void clear(ImageView img) {
        Glide.with(img).clear(img);
    }

    public static void img(ImageView img, String url) {
        Glide.with(img)
                .load(url)
                .apply(new RequestOptions().centerCrop().error(R.mipmap.ic_error))
                .into(img);
    }

    public static void img(ImageView img, String url, int error) {
        Glide.with(img)
                .load(url)
                .apply(new RequestOptions().centerCrop().error(error))
                .into(img);
    }

    public static void imgBlur(ImageView img, String url, int r) {
        if (url == null) return;
        Glide.with(img)
                .load(url)
                .apply(new RequestOptions().error(R.mipmap.ic_error))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(r, 4)))
                .transition(new DrawableTransitionOptions().crossFade(400))
                .into(img);

    }

    public static void img(ImageView img, int res_id) {
        Glide.with(img)
                .load(res_id)
                .apply(new RequestOptions().centerCrop().error(R.mipmap.ic_error))
                .into(img);
    }

    public static void imgFix(final ImageView img, final String url) {
        final int screenWidth = MockUtil.getScreenWidth(img.getContext());
        GlideApp.with(img)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .load(url)
                .placeholder(R.mipmap.pic_no)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        float scale = 1.0f;
                        ViewGroup.LayoutParams params = img.getLayoutParams();
                        params.width = screenWidth;
                        params.height = Math.round(scale * screenWidth);
                        img.requestLayout();
                        img.setImageResource(R.mipmap.pic_fail);
                        img.setOnClickListener(v -> GlideLoaderHelper.imgFix(img, url));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        img.setClickable(false);
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        float scale = ((float) height) / width;
                        ViewGroup.LayoutParams params = img.getLayoutParams();
                        params.width = screenWidth;
                        params.height = Math.round(scale * screenWidth);
                        img.setLayoutParams(params);
                        return false;
                    }
                })
                .into(img);
    }

    public static void imgFix(final ImageView img, final int res_id) {
        final int screenWidth = MockUtil.getScreenWidth(img.getContext());
        GlideApp.with(img)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .load(res_id)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        float scale = ((float) height) / width;
                        ViewGroup.LayoutParams params = img.getLayoutParams();
                        params.width = screenWidth;
                        params.height = Math.round(scale * screenWidth);
                        img.setLayoutParams(params);
                        return false;
                    }
                })
                .into(img);
    }
}
