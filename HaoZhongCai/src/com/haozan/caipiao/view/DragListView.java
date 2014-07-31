package com.haozan.caipiao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.PlusMinusLottery.DragListAdapter;

public class DragListView
    extends ListView {

    private ImageView dragImageView;// 被拖拽的项，其实就是一个ImageView
    private int dragSrcPosition;// 手指拖动项原始在列表中的位置
    private int dragPosition;// 手指拖动的时候，当前拖动项在列表中的位置

    private int dragPoint;// 在当前数据项中的位置
    private int dragOffset;// 当前视图和屏幕的距离(这里只使用了y方向上)

    private WindowManager windowManager;// windows窗口控制类
    private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数

    private int scaledTouchSlop;// 判断滑动的一个距离
    private int upScrollBounce;// 拖动的时候，开始向上滚动的边界
    private int downScrollBounce;// 拖动的时候，开始向下滚动的边界

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    // 拦截touch事件，其实就是加一层控制
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            startX = x;
            startY = y;
            System.out.println("x: " + x + " y: " + y + " RawX: " + ev.getRawX() + " RawY: " + ev.getRawY());
            dragSrcPosition = dragPosition = pointToPosition(x, y);// 判断当前xy值 是否在item上 如果在 返回改item的position 否则
            // 返回 INVALID_POSITION（-1）
            System.out.println("super.onInterceptTouchEvent(ev): " + super.onInterceptTouchEvent(ev));
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            System.out.println("dragPosition: " + dragSrcPosition + " getFirstVisiblePosition(): " +
                getFirstVisiblePosition());

            // getChildAt(int position)参数 为当前屏幕里面显示的item 从0开始 并不是adapter里面的位置 如果当前屏幕显示的微list从 5--8 的数据 则
            // getChildAt(0) 为在list中position为5的那个item
            // getFirstVisiblePosition()返回第一个显示在界面的view在adapter的位置position，可能是0，也可能是4

            ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
            System.out.println("itemView.getTop(): " + itemView.getTop());
            dragPoint = y - itemView.getTop();
            // lipeng add
            // dragPoint = itemView.getTop();
            dragOffset = (int) (ev.getRawY() - y);

            View dragger = itemView.findViewById(R.id.drag_list_item_image);
            if (dragger != null && x > dragger.getLeft() - 0) {
                //
                upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
                downScrollBounce = Math.max(y + scaledTouchSlop, getHeight() * 2 / 3);

                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                startDrag(bm, y);
            }
            View DeleButton = itemView.findViewById(R.id.dele_list_view_item);
            if (DeleButton != null && x < DeleButton.getRight() + 10) {
                DragListAdapter adapter = (DragListAdapter) getAdapter();
                int index = adapter.getList().indexOf("未显示的彩种");
                String dragItem = adapter.getItem(dragSrcPosition);

                if (dragSrcPosition <= index - 1 && existItemNum > 2) {
                    adapter.remove(dragItem);
                    adapter.insert(dragItem, index);
                }
                else if (dragSrcPosition > index) {
                    adapter.remove(dragItem);
                    adapter.insert(dragItem, index);
                }

// if (existItemNum == 2) {
// Toast.makeText(getContext(), "需至少保留两个彩种", Toast.LENGTH_SHORT).show();
// }

                countExistListItem(adapter.getList().toString());
// Toast.makeText(getContext(), String.valueOf(existItemNum), Toast.LENGTH_SHORT).show();/
                clickListener.onDragListViewItemClick(adapter.getList().toString(), existItemNum);
            }

            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
// if (existItemNum != 2)
        if (dragImageView != null && dragPosition != INVALID_POSITION) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    int upY = (int) ev.getY();
                    stopDrag();
                    onDrop(upY);
                    endX = (int) ev.getX();
                    endY = (int) ev.getY();
                    if (existItemNum < 2) {
                        moveListViewItemAutomatic(ev);
                        Toast.makeText(getContext(), "需至少保留两个彩种", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getY();
                    // onDrag(moveY);
                    // lipeng add
                    onDrag(moveY, (int) ev.getRawY());
                    break;
                default:
                    break;
            }
            return true;
        }

        // 也决定了选中的效果
        return super.onTouchEvent(ev);
    }

    private void moveListViewItemAutomatic(MotionEvent ev) {
        int x = endX;
        int y = endY;
        dragSrcPosition = dragPosition = pointToPosition(x, y);
        ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
        System.out.println("itemView.getTop(): " + itemView.getTop());
        dragPoint = y - itemView.getTop();
        dragOffset = (int) (ev.getRawY() - y);

        View dragger = itemView.findViewById(R.id.drag_list_item_image);
        if (dragger != null && x > dragger.getLeft() - 20) {
            upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
            downScrollBounce = Math.max(y + scaledTouchSlop, getHeight() * 2 / 3);
            itemView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
            startDrag(bm, y);
        }
        int moveY = startY;
        int upY = startY;
        onDrag(moveY, (int) ev.getRawY());
        stopDrag();
        onDrop(upY);
    }

    /**
     * 准备拖动，初始化拖动项的图像
     * 
     * @param bm
     * @param y
     */
    public void startDrag(Bitmap bm, int y) {
        stopDrag();

        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;

        windowParams.y = y - dragPoint + dragOffset;

        // windowParams.y = dragPoint + 50;// lipeng add
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService("window");
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /**
     * 停止拖动，去除拖动项的头像
     */
    public void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    /**
     * 拖动执行，在Move方法中执行
     * 
     * @param y
     */
    public void onDrag(int y) {
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            windowParams.y = y - dragPoint + dragOffset;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        // 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            dragPosition = tempPosition;
        }

        // 滚动
        int scrollHeight = 0;
        if (y < upScrollBounce) {
            scrollHeight = 8;// 定义向上滚动8个像素，如果可以向上滚动的话
        }
        else if (y > downScrollBounce) {
            scrollHeight = -8;// 定义向下滚动8个像素，，如果可以向上滚动的话
        }

        if (scrollHeight != 0) {
            // 真正滚动的方法setSelectionFromTop()
            setSelectionFromTop(dragPosition, getChildAt(dragPosition - getFirstVisiblePosition()).getTop() +
                scrollHeight);
        }
    }

    // lipeng add {
    /**
     * 拖动执行，在Move方法中执行
     * 
     * @param y
     */
    public void onDrag(int y, int rawY) {
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            // windowParams.y = y - dragPoint + dragOffset;
            windowParams.y = rawY - dragPoint;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        // 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            dragPosition = tempPosition;
        }

        // 滚动
        int scrollHeight = 0;
        if (y < upScrollBounce) {
            scrollHeight = 8;// 定义向上滚动8个像素，如果可以向上滚动的话
        }
        else if (y > downScrollBounce) {
            scrollHeight = -8;// 定义向下滚动8个像素，，如果可以向上滚动的话
        }

        if (scrollHeight != 0) {
            // 真正滚动的方法setSelectionFromTop()
            setSelectionFromTop(dragPosition, getChildAt(dragPosition - getFirstVisiblePosition()).getTop() +
                scrollHeight);
        }
    }

    // lipeng add }

    /**
     * 拖动放下的时候
     * 
     * @param y
     */
    public void onDrop(int y) {

        // 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            dragPosition = tempPosition;
        }

        // 超出边界处理
        if (y < getChildAt(1).getTop()) {
            // 超出上边界
            dragPosition = 1;
        }
        else if (y > getChildAt(getChildCount() - 1).getBottom()) {
            // 超出下边界
            dragPosition = getAdapter().getCount() - 1;
        }

        // 数据交换
        if (dragPosition > 0 && dragPosition < getAdapter().getCount()) {
            DragListAdapter adapter = (DragListAdapter) getAdapter();
            String dragItem = adapter.getItem(dragSrcPosition);
            adapter.remove(dragItem);
            adapter.insert(dragItem, dragPosition);
// adapter.notifyDataSetChanged();
// adapter.getView(1, null, null).findViewById(r.id);
// Toast.makeText(getContext(), adapter.getList().toString(), Toast.LENGTH_SHORT).show();
            countExistListItem(adapter.getList().toString());
            clickListener.onDragListViewItemClick(adapter.getList().toString(), existItemNum);
        }
    }

    private int existItemNum = 0;

    public void setExstTemNum(int existItemNum) {
        this.existItemNum = existItemNum;
    }

    private void countExistListItem(String list) {
        String[] listArray = list.split("\\未显示的彩种");
        String[] listItemArray01 = listArray[0].split("\\,");
        existItemNum = listItemArray01.length - 2;
    }

    private OnDragListViewListener clickListener;

    public void setOnDragListViewListener(OnDragListViewListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnDragListViewListener {
        public void onDragListViewItemClick(String list, int count);
    }
}