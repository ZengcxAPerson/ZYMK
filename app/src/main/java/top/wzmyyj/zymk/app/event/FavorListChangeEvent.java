package top.wzmyyj.zymk.app.event;

/**
 * Created by yyj on 2018/08/17. email: 2209011667@qq.com
 */
public class FavorListChangeEvent {

    private final boolean isChange;

    public FavorListChangeEvent(boolean isChange) {
        this.isChange = isChange;
    }

    public boolean isChange() {
        return isChange;
    }
}
