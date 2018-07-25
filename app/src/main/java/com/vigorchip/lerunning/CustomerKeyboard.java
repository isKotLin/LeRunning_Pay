package com.vigorchip.lerunning;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wr-app1 on 2017/12/14.
 * Description:自定义键盘
 */

public class CustomerKeyboard extends LinearLayout implements View.OnClickListener {
    private ImageView img_clean;
    public CustomerKeyboard(Context context) {
        this(context,null);
    }

    public CustomerKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomerKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context,R.layout.customer_keyboard,this);
        setItemClickListener(this);
        img_clean = (ImageView) findViewById(R.id.img_clean);
    }

    /**
    *设置子View的Click
    * */
    private void setItemClickListener(View view) {
        if (view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup)view;
            int childCount = viewGroup.getChildCount();
            for (int i=0;i<childCount;i++){
                View childView = viewGroup.getChildAt(i);
                setItemClickListener(childView);
            }
        }else{
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v instanceof TextView){
            //点击的是数字
            String number = ((TextView)v).getText().toString().trim();
            if (mListener!=null){
                mListener.click(number);
            }
        }


        if (v == img_clean){
            //点击的是删除
            mListener.delete();
        }
    }


    /**
     * 设置点击回调监听
     * */
    private CustomerKeyboardClickListener mListener;
    public void setOnCustomerKeyboardClickListener(CustomerKeyboardClickListener listener){
        this.mListener = listener;
    }

    /**
    * 点击键盘的回调
    **/
    public interface CustomerKeyboardClickListener{
        public void click(String number);
        public void delete();
    }

}
