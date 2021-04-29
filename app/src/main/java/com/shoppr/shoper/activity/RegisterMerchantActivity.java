package com.shoppr.shoper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.shoppr.shoper.Model.GetRegisterMerchant.GetRegisterMerchantModel;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.ApiService;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.Helper;
import com.shoppr.shoper.util.SessonManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterMerchantActivity extends AppCompatActivity {
    CircleImageView circleImage;
    ImageView choseImage,getImageShow;
    EditText editStoreName,editStoreType,editEmail,
    editMobile,editOpeningTime,editAboutStore,editAddress;
    SessonManager sessonManager;
    /*Todo:- Image Choose*/
    int PICK_IMAGE_MULTIPLE = 1;
    File photoFile;
    Uri photoUri;
    String mCurrentMPath;
    ArrayList<String> imagePathList = new ArrayList<>();
    Bitmap bitmap = null;
    private String photoPath;
    String imageEncoded;
    private static String baseUrl="http://shoppr.avaskmcompany.xyz/api/";
    String location_address;
    LatLng latLng;
    Double latitude,longitude;
    Button updateBtn;
    TextView msgPrintText;
    private int mRequestCode = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_merchant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        /*todo:-Find id*/
        circleImage=findViewById(R.id.circleImage);
        choseImage=findViewById(R.id.choseImage);
        getImageShow=findViewById(R.id.getImageShow);
        /*Todo:- Image Choose*/
        choseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
        editStoreName=findViewById(R.id.editStoreName);
        editStoreType=findViewById(R.id.editStoreType);
        editEmail=findViewById(R.id.editEmail);
        editMobile=findViewById(R.id.editMobile);
        editOpeningTime=findViewById(R.id.editOpeningTime);
        editAboutStore=findViewById(R.id.editAboutStore);
        editAddress=findViewById(R.id.editAddress);
        updateBtn=findViewById(R.id.updateBtn);

        msgPrintText=findViewById(R.id.msgPrintText);

        //location_address=getIntent().getStringExtra("location_address");


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStoreName.getText().toString().isEmpty()){
                    editStoreName.setError("Store Name Field Can't be blank");
                    editStoreName.requestFocus();
                }else if (editStoreType.getText().toString().isEmpty()){
                    editStoreType.setError("Store Type Field Can't be blank");
                    editStoreType.requestFocus();
                }else if (editEmail.getText().toString().isEmpty()){
                    editEmail.setError("Email Field Can't be blank");
                    editEmail.requestFocus();
                }else if(editMobile.getText().toString().length()!=10){
                    editMobile.setError("Mobile No. should be 10 digit");
                    editMobile.requestFocus();
                }else if(editOpeningTime.getText().toString().isEmpty()){
                    editOpeningTime.setError("Opening Time Field Can't be blank");
                    editOpeningTime.requestFocus();
                }else if(editAboutStore.getText().toString().isEmpty()){
                    editAboutStore.setError("About Store Field Can't be blank");
                    editAboutStore.requestFocus();
                }else if(editAddress.getText().toString().isEmpty()){
                    editAddress.setError("Address Field Can't be blank");
                    editAddress.requestFocus();
                }else if (imagePathList.size()==0){
                    Toast.makeText(RegisterMerchantActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
                else {
                    UpdateAPI();
                }
            }
        });
        viewMerchantRegister();

    }

    private void viewMerchantRegister() {
        if (CommonUtils.isOnline(RegisterMerchantActivity.this)){
            sessonManager.showProgress(RegisterMerchantActivity.this);
            Call<GetRegisterMerchantModel>call=ApiExecutor.getApiService(this)
                    .apiGetMerchantRegister("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<GetRegisterMerchantModel>() {
                @Override
                public void onResponse(Call<GetRegisterMerchantModel> call, Response<GetRegisterMerchantModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null) {
                        GetRegisterMerchantModel getRegisterMerchantModel=response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (getRegisterMerchantModel.getData().getApplication()!=null){
                                updateBtn.setVisibility(View.GONE);
                                msgPrintText.setVisibility(View.VISIBLE);
                                choseImage.setVisibility(View.GONE);
                                circleImage.setVisibility(View.GONE);
                                getImageShow.setVisibility(View.VISIBLE);
                                Picasso.get().load(getRegisterMerchantModel.getData().getApplication().getImage()).into(getImageShow);
                                editStoreName.setText(getRegisterMerchantModel.getData().getApplication().getStoreName());
                                editStoreType.setText(getRegisterMerchantModel.getData().getApplication().getStoreType());
                                editEmail.setText(getRegisterMerchantModel.getData().getApplication().getEmail());
                                editMobile.setText(getRegisterMerchantModel.getData().getApplication().getMobile());
                                editOpeningTime.setText(getRegisterMerchantModel.getData().getApplication().getOpeningTime());
                                editAboutStore.setText(getRegisterMerchantModel.getData().getApplication().getAboutStore());
                                editAddress.setText(getRegisterMerchantModel.getData().getApplication().getAddress());
                                msgPrintText.setText(getRegisterMerchantModel.getData().getMessage());
                            }
                        }else {
                            Toast.makeText(RegisterMerchantActivity.this, ""+getRegisterMerchantModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetRegisterMerchantModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });

        }else {
            CommonUtils.showToastInCenter(RegisterMerchantActivity.this, getString(R.string.please_check_network));
        }
    }


    private void getAddress(double latitude, double longitude) {
        //Log.d("ressssssLocation",""+latitude+longitude);
        Geocoder geocoder = new Geocoder(RegisterMerchantActivity.this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);
        location_address = address.getAddressLine(0);
        editAddress.setText(location_address);
    }

    private void UpdateAPI() {
        if (CommonUtils.isOnline(RegisterMerchantActivity.this)) {
            sessonManager.showProgress(RegisterMerchantActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("store_name", ApiFactory.getRequestBodyFromString(editStoreName.getText().toString()));
            partMap.put("store_type",ApiFactory.getRequestBodyFromString(editStoreType.getText().toString()));
            partMap.put("email", ApiFactory.getRequestBodyFromString(editEmail.getText().toString()));
            partMap.put("mobile", ApiFactory.getRequestBodyFromString(editMobile.getText().toString()));
            partMap.put("opening_time", ApiFactory.getRequestBodyFromString(editOpeningTime.getText().toString()));
            partMap.put("about_store", ApiFactory.getRequestBodyFromString(editAboutStore.getText().toString()));
            partMap.put("lat", ApiFactory.getRequestBodyFromString(String.valueOf(latitude)));
            partMap.put("lang", ApiFactory.getRequestBodyFromString(String.valueOf(longitude)));
            partMap.put("address", ApiFactory.getRequestBodyFromString(editAddress.getText().toString()));

            MultipartBody.Part[] imageArray1 = new MultipartBody.Part[imagePathList.size()];
            //Log.d("arrayLis",""+imageArray1);

            for (int i = 0; i < imageArray1.length; i++) {
                File file = new File(imagePathList.get(i));
                try {
                    File compressedfile = new Compressor(RegisterMerchantActivity.this).compressToFile(file);
                    RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                    imageArray1[i] = MultipartBody.Part.createFormData("image", compressedfile.getName(), requestBodyArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer "+sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiRegisterMerchant(headers,imageArray1,partMap)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            sessonManager.hideProgress();
                            JsonObject jsonObject = response.body();
                            String code = jsonObject.get("status").getAsString();
                            //Log.d("responseJson",""+jsonObject);
                            if (code.equals("success")) {
                                String msg = jsonObject.get("message").getAsString();
                                Toast.makeText(RegisterMerchantActivity.this, msg, Toast.LENGTH_SHORT).show();
                                viewMerchantRegister();
                            } else {
                                String msg = jsonObject.get("message").getAsString();
                                Toast.makeText(RegisterMerchantActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
        }else {
            CommonUtils.showToastInCenter(RegisterMerchantActivity.this, getString(R.string.please_check_network));
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
    /*Todo:- Image Choose*/
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterMerchantActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 786);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterMerchantActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == 1)) {

            try {
                rotateImage();
            } catch (Exception e) {
                e.printStackTrace();

            }


        } else if ((requestCode == 786)) {
            selectFromGallery(data);
        }
        if(requestCode == mRequestCode && resultCode == RESULT_OK){
            location_address= data.getStringExtra("location_address");
            if (location_address!=null){
                Geocoder coder = new Geocoder(RegisterMerchantActivity.this);
                List<Address> address;

                try {
                    //Get latLng from String
                    address = coder.getFromLocationName(location_address, 5);

                    //check for null
                    if (address != null) {

                        //Lets take first possibility from the all possibilities.
                        try {
                            Address location = address.get(0);
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            latitude = latLng.latitude;
                            longitude = latLng.longitude;
                            getAddress(latLng.latitude,latLng.longitude);
                        } catch (IndexOutOfBoundsException er) {
                            Toast.makeText(RegisterMerchantActivity.this, "Location isn't available", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //editAddress.setText(location_address);
            //Log.d("location_addressResss",editTextString);
        }

    }

    private void takeCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createFile();
                //Log.d("checkexcesdp", String.valueOf(photoFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                //Log.d("checkexcep", ex.getMessage());
            }
            photoFile = createFile();
            photoUri = FileProvider.getUriForFile(RegisterMerchantActivity.this, getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, 1);
        }

    }

    private File createFile() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMPath = image.getAbsolutePath();
        return image;
    }

    public void rotateImage() throws IOException {

        try {
            String photoPath = photoFile.getAbsolutePath();
            imagePathList.add(photoPath);
            // Log.d("ress",""+imagePathList);
            bitmap = MediaStore.Images.Media.getBitmap(RegisterMerchantActivity.this.getContentResolver(), photoUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            if (Build.VERSION.SDK_INT > 23) {
                bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), photoUri);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            }

            circleImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectFromGallery(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPath = Helper.pathFromUri(RegisterMerchantActivity.this, mImageUri);
                        imagePathList.add(photoPath);


                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor1.getString(columnIndex1);

                        if (Build.VERSION.SDK_INT > 23) {
                            // Log.d("inelswe", "inelse");
                            bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                        } else {
                            // Log.d("inelse", "inelse");
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                        }


                        //   deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();

                        circleImage.setImageBitmap(bitmap);


                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPath = Helper.pathFromUri(RegisterMerchantActivity.this, mImageUri);
                    imagePathList.add(photoPath);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor1.getString(columnIndex1);


                    if (Build.VERSION.SDK_INT > 23) {
                        //Log.d("inelswe", "inelse");
                        bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);

                    } else {
                        //Log.d("inelse", "inelse");
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

                    }

                    //  deedBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);

                    cursor1.close();
                    circleImage.setImageBitmap(bitmap);


                }


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }


    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = null;
        if (Build.VERSION.SDK_INT > 23) {
            ei = new ExifInterface(input);
        }


        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }


    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void askForPermissioncamera(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterMerchantActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(RegisterMerchantActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(RegisterMerchantActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }


    }

    public void Map(View view) {
       /* startActivity(new Intent(RegisterMerchantActivity.this,EditLocationActivity.class)
        .putExtra("merchant","merchant"));*/
        startActivityForResult(new Intent(RegisterMerchantActivity.this,EditLocationActivity.class)
                .putExtra("merchant","merchant"),mRequestCode);
    }

}