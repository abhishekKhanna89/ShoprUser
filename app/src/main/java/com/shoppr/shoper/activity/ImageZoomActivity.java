package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.Model.StoreListDetails.StoreListDetailsModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageZoomActivity extends AppCompatActivity {
    ImageView selectedImage;
    SessonManager sessonManager;
    List<Image> imageList;
    int storeId;
    Gallery gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager =new SessonManager(this);
        storeId=getIntent().getIntExtra("shopId",0);
        Log.d("StoreId",""+storeId);
         gallery = (Gallery) findViewById(R.id.gallery);
        selectedImage=(ImageView)findViewById(R.id.imageView);

        gallery.setSpacing(1);
        viewDetails();
    }
    private void viewDetails() {
        if (CommonUtils.isOnline(ImageZoomActivity.this)) {
            Call<StoreListDetailsModel> call= ApiExecutor.getApiService(this)
                    .apiStoreListDetails("Bearer "+sessonManager.getToken(),storeId);

            Log.d("imagelist", String.valueOf(storeId));

            call.enqueue(new Callback<StoreListDetailsModel>() {
                @Override
                public void onResponse(Call<StoreListDetailsModel> call, Response<StoreListDetailsModel> response) {
                    if (response.body()!=null) {
                        StoreListDetailsModel storeListDetailsModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            imageList=storeListDetailsModel.getData().getStoresDetails().getImages();
                            for (int i=0;i<imageList.size();i++){
                                Picasso.get().load(imageList.get(i).getImage()).into(selectedImage);
                                PhotoViewAttacher pAttacher;
                                pAttacher = new PhotoViewAttacher(selectedImage);
                                pAttacher.update();
                            }
                            //SliderAdapter sliderAdapter=new SliderAdapter(SotoreDetailsActivity.this,imageList);
                            final GalleryImageAdapter galleryImageAdapter= new GalleryImageAdapter(ImageZoomActivity.this,imageList);
                            gallery.setAdapter(galleryImageAdapter);
                            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    Picasso.get().load(imageList.get(position).getImage()).into(selectedImage);
                                    // show the selected Image
                                    // selectedImage.setImageResource(galleryImageAdapter.mImageIds[position]);
                                }
                            });


                        }else {
                            Toast.makeText(ImageZoomActivity.this, ""+storeListDetailsModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(ImageZoomActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ImageZoomActivity.this,"");
                                            Toast.makeText(ImageZoomActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ImageZoomActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        }else {

                                        }
                                    });
                                }
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<StoreListDetailsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(ImageZoomActivity.this, getString(R.string.please_check_network));
        }

    }

    public class GalleryImageAdapter extends BaseAdapter {
        private Context mContext;
        List<Image> imageList;


        public GalleryImageAdapter(Context context, List<Image> imageList)
        {
            this.mContext = context;
            this.imageList=imageList;
        }

        public int getCount() {


            Log.d("sizeimagelist", String.valueOf(imageList.size()));
            return imageList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        // Override this method according to your need
        public View getView(int index, View view, ViewGroup viewGroup)
        {
            // TODO Auto-generated method stub
            ImageView i = new ImageView(mContext);
            Picasso.get().load(imageList.get(index).getImage()).into(i);
           // i.setImageResource(mImageIds[index]);
            i.setLayoutParams(new Gallery.LayoutParams(200, 200));
            i.setScaleType(ImageView.ScaleType.FIT_XY);

            return i;
        }


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