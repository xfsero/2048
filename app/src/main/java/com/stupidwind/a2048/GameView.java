package com.stupidwind.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridLayout;

/**
 * Created by 蠢风 on 2017/9/6.
 */

public class GameView extends GridLayout {

    private static final int ROW_NUM = 4;   // 行数
    private static final int COL_NUM = 4;   // 列数

    private Card[][] cards = new Card[ROW_NUM][COL_NUM];
    private int gridWidth;  // 棋盘格子的宽度，根据屏幕大小来自动调整
    private GestureDetector mGestureDetector;
    private int mScore; // 分数

    private ShowScoreListener mShowScoreListener;
    public void setShowScoreListener(ShowScoreListener listener) {
        this.mShowScoreListener = listener;
    }
    public interface ShowScoreListener {
        void showScore();
    }

    public GameView(Context context) {
        super(context);
        init();
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
    }

    /**
     * 初始化棋盘
     */
    private void init() {
        setColumnCount(COL_NUM);
        gridWidth = getResources().getDisplayMetrics().widthPixels / 4;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        newGame();
    }

    /**
     * 开始新的游戏
     */
    private void newGame() {
        this.removeAllViews();
        mScore = 0;
        for(int i = 0; i < ROW_NUM; i++) {
            for(int j = 0; j < COL_NUM; j++) {
                cards[i][j] = new Card(getContext(), gridWidth);
                this.addView(cards[i][j]);
            }
        }
        genRandomNum();
        genRandomNum();
    }

    public int getScore() {
        return mScore;
    }

    /**
     * 在棋盘上生成随机数
     */
    private void genRandomNum() {
        if(!checkOver()) {
            int row;
            int col;
            do {
                int rand = (int)(Math.random() * 16);
                row = rand / 4;
                col = rand % 4;
            } while(cards[row][col].getNumber() > 0);

            int randNum = Math.random() > 0.2 ? 2 : 4;
            cards[row][col].setNumber(randNum);
        } else {
            // 游戏结束
        }
    }

    /**
     * 判定棋盘是否填满
     * @return
     */
    private boolean isFull() {
        for(int i = 0; i < ROW_NUM; i++) {
            for(int j = 0; j < COL_NUM; j++) {
                if(cards[i][j].getNumber() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查游戏是否结束
     */
    private boolean checkOver() {
        boolean isOver = true;
        if(isFull()) {
            for(int row = 1; row < ROW_NUM - 1; row++) {
                for(int col = 1; col < COL_NUM - 1; col++) {
                    if(cards[row][col].equals(cards[row - 1][col]))
                        isOver = false;
                    if(cards[row][col].equals(cards[row + 1][col]))
                        isOver = false;
                    if(cards[row][col].equals(cards[row][col - 1]))
                        isOver = false;
                    if(cards[row][col].equals(cards[row][col + 1]))
                        isOver = false;
                }
            }
        } else {
            isOver = false;
        }
        return isOver;
    }

    /**
     * 滑动操作
     * 以向左滑动为例，寻找下一个不为0的格子next
     * 如果找不到下一个不为0的格子，则什么都不做
     * 如果当前位置格子为0，则和next置换
     * 如果当前格子与next相等，则当前格子数值乘以2，next格子置为0
     * @param dir
     */
    private void moveTo(DIRECTION dir) {
        boolean hasMove = false;
        boolean hasMerge = false;
        switch (dir) {
            case LEFT:
                for(int row = 0; row < ROW_NUM; row++) {
                    for(int col = 0; col < COL_NUM; col++) {
                        int nextI = -1;
                        for(int col1 = col + 1; col1 < COL_NUM; col1++) {
                            if(cards[row][col1].getNumber() > 0) {
                                nextI = col1;
                                break;
                            }
                        }
                        if(nextI != -1) {
                            // 如果下一个格子的索引值不为-1，则说明找到不为0的下一个格子
                            if(cards[row][col].getNumber() == 0) {
                                // 当前格子为0时，则置换
                                cards[row][col].setNumber(cards[row][nextI].getNumber());
                                cards[row][nextI].setNumber(0);
                                col --;
                                hasMove = true;
                            } else if(cards[row][col].equals(cards[row][nextI])) {
                                // 当前格子与下一个格子数字相等时，则合并
                                cards[row][col].setNumber(cards[row][col].getNumber() * 2);
                                cards[row][nextI].setNumber(0);
                                mScore += cards[row][col].getNumber();
                                hasMerge = true;
                            }
                        }
                    }
                }
                if(hasMove || hasMerge) {
                    genRandomNum();
                }

                break;
            case RIGHT:
                for(int row = 0; row < ROW_NUM; row++) {
                    for(int col = COL_NUM - 1; col >= 0; col--) {
                        int nextI = -1;
                        for(int col1 = col - 1; col1 >= 0; col1--) {
                            if(cards[row][col1].getNumber() > 0) {
                                nextI = col1;
                                break;
                            }
                        }

                        if(nextI != -1) {
                            if(cards[row][col].getNumber() == 0) {
                                cards[row][col].setNumber(cards[row][nextI].getNumber());
                                cards[row][nextI].setNumber(0);
                                col++;
                                hasMove = true;
                            } else if(cards[row][col].equals(cards[row][nextI])) {
                                cards[row][col].setNumber(cards[row][col].getNumber() * 2);
                                cards[row][nextI].setNumber(0);
                                mScore += cards[row][col].getNumber();
                                hasMerge = true;
                            }
                        }
                    }
                }
                if(hasMove || hasMerge) {
                    genRandomNum();
                }

                break;

            case UP:
                for(int col = 0; col < COL_NUM; col++) {
                    for(int row = ROW_NUM - 1; row >= 0; row--) {
                        int nextI = -1;
                        for(int row1 = row -1; row1 >= 0; row1--) {
                            if(cards[row1][col].getNumber() > 0) {
                                nextI = row1;
                                break;
                            }
                        }
                        if(nextI != -1) {
                            if(cards[row][col].getNumber() == 0) {
                                cards[row][col].setNumber(cards[nextI][col].getNumber());
                                cards[nextI][col].setNumber(0);
                                row--;
                                hasMove = true;
                            } else if(cards[row][col].equals(cards[nextI][col])) {
                                cards[row][col].setNumber(cards[row][col].getNumber() * 2);
                                cards[nextI][col].setNumber(0);
                                mScore += cards[row][col].getNumber();
                                hasMerge = true;
                            }
                        }
                    }
                }
                if(hasMove || hasMerge) {
                    genRandomNum();
                }

                break;
            case DOWN:
                for(int col = 0; col < COL_NUM; col++) {
                    for(int row = 0; row < ROW_NUM; row++) {
                        int nextI = -1;
                        for(int row1 = row + 1; row1 < ROW_NUM; row1++) {
                            if(cards[row1][col].getNumber() > 0) {
                                nextI = row1;
                                break;
                            }
                        }

                        if(nextI != -1) {
                            if(cards[row][col].getNumber() == 0) {
                                cards[row][col].setNumber(cards[nextI][col].getNumber());
                                cards[nextI][col].setNumber(0);
                                row++;
                                hasMove = true;
                            } else if(cards[row][col].equals(cards[nextI][col])) {
                                cards[row][col].setNumber(cards[row][col].getNumber() * 2);
                                cards[nextI][col].setNumber(0);
                                mScore += cards[row][col].getNumber();
                                hasMerge = true;
                            }
                        }
                    }
                }

                if(hasMove || hasMerge) {
                    genRandomNum();
                }
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if(mShowScoreListener != null) {
            mShowScoreListener.showScore();
        }
        return true;
    }

    // 手势操作枚举
    private enum DIRECTION{
        LEFT, RIGHT, UP, DOWN
    }

    /**
     * 游戏手势识别
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private final float FLING_MIN_DISTANCE = 5F;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(isFull()) {
                return false;
            }

            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();


            if(Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(x) > FLING_MIN_DISTANCE) {
                // 横向滑动
                if(velocityX > 0) {
                    moveTo(DIRECTION.RIGHT);
                } else {
                    moveTo(DIRECTION.LEFT);
                }

            } else if (Math.abs(velocityY) > Math.abs(velocityX) && Math.abs(y) > FLING_MIN_DISTANCE) {
                // 纵向滑动
                if(velocityY > 0) {
                    moveTo(DIRECTION.DOWN);
                } else {
                    moveTo(DIRECTION.UP);
                }
            }

            return true;
        }
    }
}
