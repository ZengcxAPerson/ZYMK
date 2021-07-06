package top.wzmyyj.zymk.app.bean;

/**
 * Created by yyj on 2018/08/06. email: 2209011667@qq.com
 */
public class ComicBean {

    private long chapterId;
    private String chapterName;
    private int var;
    private int varSize;
    private String imgLow;
    private String imgMiddle;
    private String imgHigh;
    private int price;

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getVar() {
        return var;
    }

    public void setVar(int var) {
        this.var = var;
    }

    public int getVarSize() {
        return varSize;
    }

    public void setVarSize(int varSize) {
        this.varSize = varSize;
    }

    public String getImgLow() {
        return imgLow;
    }

    public void setImgLow(String imgLow) {
        this.imgLow = imgLow;
    }

    public String getImgMiddle() {
        return imgMiddle;
    }

    public void setImgMiddle(String imgMiddle) {
        this.imgMiddle = imgMiddle;
    }

    public String getImgHigh() {
        return imgHigh;
    }

    public void setImgHigh(String imgHigh) {
        this.imgHigh = imgHigh;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
