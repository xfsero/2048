package com.stupidwind.a2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridLayout;

/**
 * Created by 蠢风 on 2017/9/6.
 */

public class GameView extends GridLayout {

    public static final String TAG = GridLayout.class.getSimpleName().toString();

    private static final int ROW_NUM = 4;   // 行数
    private static final int COL_NUM = 4;   // 列数

    private Card[][] cards = new Card[ROW_NUM][COL_NUM];
    private int gridWidth;  // 棋盘格子的宽度，根据屏幕大小来自动调整
    private GestureDetector mGestureDetector;
    private int mScore; // 分数
    private int mHighScore; // 历史最高分数
    private boolean isGameOver; // 判定游戏是否结束

    private ShowScoreListener mShowScoreListener;
    public void setShowScoreListener(ShowScoreListener listener) {
        this.mShowScoreListener = listener;
    }
    public interface ShowScoreListener {
        void showScore();
        void updateHighScore();
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
    public void newGame() {
        Log.d(TAG, "开始游戏");
        this.removeAllViews();
        isGameOver = false;
        // 更新历史最高得分
        SharedPreferences sp = getContext().getSharedPreferences("stupidwind", Context.MODE_PRIVATE);
        mHighScore = sp.getInt("high_score", 0);
        mScore = 0;
        if(mShowScoreListener != null) {
            mShowScoreListener.showScore();
            mShowScoreListener.updateHighScore();
        }
        mScore = 0;
        // 初始化所有格子 4x4
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

    public int getHighScore() {
        return mHighScore;
    }

    /**
     * 在棋盘上生成随机数
     */
    private void genRandomNum() {
        if(!isFull()) {
            int row;
            int col;
            do {
                int rand = (int)(Math.random() * 16);
                row = rand / 4;
                col = rand % 4;
            } while(cards[row][col].getNumber() > 0);

            int randNum = Math.random() > 0.2 ? 2 : 4;
            cards[row][col].setNumber(randNum);
            Log.d(TAG, "生成一个新方块");
        }
    }

    /**
     * 游戏结束时执行的函数
     */
    private void onGameOver() {

        // 游戏结束，判断分数是否超过历史最高分数，并保存
        Log.e(TAG, "GAME OVER");
        isGameOver = true;
        if(mScore > mHighScore) {
            mHighScore = mScore;
            SharedPreferences sp = getContext().getSharedPreferences("stupidwind", Context.MODE_PRIVATE);
            sp.edit().putInt("high_score", mHighScore).commit();
            if(mShowScoreListener != null) {
                mShowScoreListener.updateHighScore();
            }
        }

        Log.d(TAG, "onGameOver: ");
        isGameOver = true;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("GAME OVER")
                .setCancelable(false)
                .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();
                    }
                })
                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
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

        if(!isFull()) {
            return false;
        }

        for(int row = 0; row < ROW_NUM; row++) {
            for(int col = 0; col < COL_NUM; col++) {

                if(row < ROW_NUM - 1 && cards[row][col].equals(cards[row + 1][col])) {
                    isOver = false;
                    break;
                } else if(row > 0 && cards[row][col].equals(cards[row - 1][col])) {
                    isOver = false;
                    break;
                } else if(col < COL_NUM - 1 && cards[row][col].equals(cards[row][col + 1])) {
                    isOver = false;
                    break;
                } else if(col > 0 && cards[row][col].equals(cards[row][col - 1])) {
                    isOver = false;
                    break;
                }

            }
        }
        Log.d(TAG, "checkOver: " + isOver);
        return isOver;
    }

    /**
     * 滑动操作
     * 以向左滑动为例，寻找下一个不为0的格子next
     * 如果找不到下一个不为0的格子，则什么都不做， 否则：
     * 如果当前位置格子为0，则和next置换
     * 如果当前格子与next相等，则当前格子数值乘以2，next格子置为0
     * @param dir
     */
    private void moveTo(DIRECTION dir) {

        boolean hasMove = false;
        boolean hasMerge = false;

        switch (dir) {
            case LEFT:
                Log.d(TAG, "向左滑动");
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
                                cards[row][col].swap(cards[row][nextI]);
                                hasMove = true;
                                col --;
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
                Log.d(TAG, "向右滑动");
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
                                cards[row][col].swap(cards[row][nextI]);
                                hasMove = true;
                                col++;
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
                Log.d(TAG, "向上滑动");

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
                                cards[row][col].swap(cards[nextI][col]);
                                hasMove = true;
                                row++;
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
                Log.d(TAG, "向下滑动");

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
                                cards[row][col].swap(cards[nextI][col]);
                                hasMove = true;
                                row--;
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

        }

        if(checkOver()) {
            onGameOver();
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

            if(isGameOver) {
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

            Log.d(TAG, "手势操作完毕");
            return true;
        }
    }
}
