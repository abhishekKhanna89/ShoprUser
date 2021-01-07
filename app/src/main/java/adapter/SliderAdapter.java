package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.shoper.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Slidermode;

public class SliderAdapter extends PagerAdapter {


    Context mContext;

    int[] sliderImage;



    public SliderAdapter(Context mContext, int[] sliderImage) {
        this.mContext = mContext;
        this.sliderImage = sliderImage;
    }

    @Override
    public int getCount() {
        return sliderImage.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_slide_pager,null);
        ImageView imageView = view.findViewById(R.id.custom_image);
        imageView.setImageResource(sliderImage[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

}
