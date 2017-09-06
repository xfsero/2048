package com.stupidwind.a2048;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by 蠢风 on 2017/9/6.
 * 卡片实体类
 */

public class Card extends FrameLayout{

    private int mNumber;
    private String mNumberVal;
    private int width;
    private TextView tv;

    public Card(Context context, int width) {
        super(context);
        this.width = width;
        init();
    }

    /**
     * 设置方块的数字
     * @param number
     */
    public void setNumber(int number) {
        if(number > 0) {
            this.mNumberVal = number + "";
        } else {
            this.mNumberVal = "";
        }
        this.mNumber = number;
        tv.setText(mNumberVal);
        setColor(mNumber);
    }

    /**
     * 获取方块的数字
     * @return
     */
    public int getNumber() {
        return mNumber;
    }

    /**
     * 初始化方块
     */
    private void init() {
        tv = new TextView(getContext());
        this.setPadding(5, 5, 5, 5);
        LayoutParams params = new LayoutParams(width - 10, width - 10);
        tv.setLayoutParams(params);
        tv.setText("");
        tv.setTextSize(48);
        tv.setGravity(Gravity.CENTER);
        this.addView(tv);
        setColor(mNumber);
    }

    /**
     * 设置方块的颜色，以及字体颜色
     * @param number
     */
    public void setColor(int number) {

        if(number >= 16) {  // 数字大于等于16时，字体设为白色
            tv.setTextColor(Color.WHITE);
        } else {    // 数字小于16，字体为黑色
            tv.setTextColor(Color.BLACK);
        }

        switch (number) {
            case 0:
                tv.setBackgroundColor(getResources().getColor(R.color.c0));
                break;
            case 2:
                tv.setBackgroundColor(getResources().getColor(R.color.c2));
                break;
            case 4:
                tv.setBackgroundColor(getResources().getColor(R.color.c4));
                break;
            case 8:
                tv.setBackgroundColor(getResources().getColor(R.color.c8));
                break;
            case 16:
                tv.setBackgroundColor(getResources().getColor(R.color.c16));
                break;
            case 32:
                tv.setBackgroundColor(getResources().getColor(R.color.c32));
                break;
            case 64:
                tv.setBackgroundColor(getResources().getColor(R.color.c64));
                break;
            case 128:
                tv.setBackgroundColor(getResources().getColor(R.color.c128));
                break;
            case 256:
                tv.setBackgroundColor(getResources().getColor(R.color.c256));
                break;
            case 512:
                tv.setBackgroundColor(getResources().getColor(R.color.c512));
                break;
            case 1024:
                tv.setBackgroundColor(getResources().getColor(R.color.c1024));
                break;
            case 2048:
                tv.setBackgroundColor(getResources().getColor(R.color.c2048));
                break;
        }
    }

    public boolean equals(Card c) {
        return this.mNumber == c.getNumber();
    }

}
