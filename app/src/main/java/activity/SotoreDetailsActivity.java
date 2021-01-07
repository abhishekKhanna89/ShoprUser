package activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.shoper.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import adapter.SliderAdapter;
import model.Slidermode;

public class SotoreDetailsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager mViewPager;
    private static int currentpage;
    Button btnsubmit;
    ArrayList<Slidermode>arListBanner;
    int[] pic = {R.drawable.shopingfour,
            R.drawable.shopingthree,
            R.drawable.shopingfour,
            R.drawable.shopingfive,
            R.drawable.shopingfour,
            R.drawable.shopingimg};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sotore_details);
        getSupportActionBar().setTitle("Store Listing");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager)findViewById(R.id.viewPage);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        SliderAdapter adapterView = new SliderAdapter(SotoreDetailsActivity.this, pic);
        mViewPager.setAdapter(adapterView);
        tabLayout = findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(mViewPager, true);
        arListBanner=new ArrayList<>();
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SotoreDetailsActivity.this,MyAccount.class));
            }
        });
        init();

    }
    private void init() {

        tabLayout.setupWithViewPager(mViewPager, true);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentpage == pic.length) {
                    currentpage = 0;
                }
                mViewPager.setCurrentItem(currentpage++, true);
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 2000, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}