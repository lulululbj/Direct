package luyao.sdk.keyboard;

/**
 * @author: luyao
 * @date: 2021/10/24 上午12:26
 */
public interface KeyboardHeightObserver {
    void onVirtualBottomHeight(int i);

    void onKeyboardHeightChanged(int height, int orientation);
}
