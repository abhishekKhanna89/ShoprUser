package com.shoppr.shoper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppr.shoper.R;

import java.util.List;


public class BannerAdapter extends PagerAdapter {
    Context context;
    int images[];
    LayoutInflater layoutInflater;
    private List<Integer> data;

    public BannerAdapter(Context context, List<Integer> data) {
        this.context = context;
        this.data = data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30.0f);
        circularProgressDrawable.start();
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .skipMemoryCache(true)
                .dontAnimate()
                .dontTransform();
        View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imgBanner);
        Glide.with(context).load(data.get(position)).apply(requestOptions).into(imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}