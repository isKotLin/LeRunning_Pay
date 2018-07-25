package com.vigorchip.lerunning;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wr-app1 on 2017/12/26.
 */

public class AdvertiseView extends RelativeLayout {

    protected static final String TAG = "AdvertiseView";

    private ViewPager mViewPager;
    //这个list用来装载view 如果你要做其他展示 改成List<View> 然后你要自定义View播放 否则没效果
    private List<ImageView> mListData;

    private int mLastSelectedPositon = 0;

    //图片
    private ArrayList<Bitmap> ImageList;

    //这里就是那描述信息
//    private ArrayList<String> mTitles;

    private int real_size=0;

    /*
    这个传送数据的接口
     */
    public void setImagesAndTitles(ArrayList<Bitmap> Arrayimages) {//, ArrayList<String> titles
        //这个是广告轮播图的图片链表
        ImageList = Arrayimages;
        //这是广告轮播图的描述信息链表
//        mTitles = titles;
//        mDotsContainer = (LinearLayout) findViewById(R.id.dots_container);
        // ListView -> setAdapter -> create Adapter -> List data
        initDataList();
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        int initPosition = Integer.MAX_VALUE / 2;
        initPosition = initPosition - initPosition % ImageList.size();
//        mTitle.setText(mTitles.get(0));
        mViewPager.setCurrentItem(initPosition);

    }

    public AdvertiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_advertise, this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTitle = (TextView) findViewById(R.id.title);

    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            roll(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private TextView mTitle;

    private LinearLayout mDotsContainer;

    private void initDataList() {
        mListData = new ArrayList<ImageView>();
        real_size=ImageList.size();
        for (int i = 0; i < ImageList.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            mListData.add(imageView);

            View view = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(24, 24);
            if (i != ImageList.size()-1) {
                layoutParams.rightMargin = 16;
            }
            layoutParams.topMargin = 12;
            view.setLayoutParams(layoutParams);

//            if (i == 0) {
//                view.setBackgroundResource(R.drawable.dot_selected);
//            } else {
//                view.setBackgroundResource(R.drawable.dot_normal);
//            }
//            mDotsContainer.addView(view);
        }

        if(ImageList.size()==3){
            ImageView imageView = new ImageView(getContext());
            mListData.add(imageView);
            imageView = new ImageView(getContext());
            mListData.add(imageView);
            imageView = new ImageView(getContext());
            mListData.add(imageView);
            ImageList.add(ImageList.get(0));
            ImageList.add(ImageList.get(1));
            ImageList.add(ImageList.get(2));
        }
        if(ImageList.size()==2){
            ImageView imageView = new ImageView(getContext());
            mListData.add(imageView);
            imageView = new ImageView(getContext());
            mListData.add(imageView);
            ImageList.add(ImageList.get(0));
            ImageList.add(ImageList.get(1));
        }

    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if(ImageList.size()==1){
                return 1;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % ImageList.size();
            ImageView imageView = mListData.get(position);
            //设置广告轮播图的图片
            imageView.setImageBitmap(ImageList.get(position));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            return imageView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            position = position % ImageList.size();
            ImageView imageView = mListData.get(position);
            container.removeView(imageView);
        }

    }

    private void roll(int position){
        position = position % real_size;
//        mTitle.setText(mTitles.get(position));
        View previousDot = mDotsContainer.getChildAt(mLastSelectedPositon);
        previousDot.setBackgroundResource(R.drawable.dot_normal);

        View dot = mDotsContainer.getChildAt(position);
        dot.setBackgroundResource(R.drawable.dot_selected);
        mLastSelectedPositon = position;
    }

    /*
     *这个是用于自动滚动图片接口
     */
    public void autoscroll(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

}