package com.shoppr.shoper.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.MyProfile.MyProfileModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class UpdateProfileActivity extends AppCompatActivity {
    SessonManager sessonManager;
    CircleImageView circleImage;
    ImageView choseImage, imgZoomed;
    EditText editName, editEmail, editMobile;
    Button updateBtn;
    int PICK_IMAGE_MULTIPLE = 1;
    File photoFile;
    Uri photoUri;
    String mCurrentMPath;
    ArrayList<String> imagePathList = new ArrayList<>();
    Bitmap bitmap = null;
    private String photoPath;
    String imageEncoded;
    private static String baseUrl =ApiExecutor.baseUrl;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);
        sessonManager = new SessonManager(this);
        String mobile = sessonManager.getMobileNo();
        circleImage = findViewById(R.id.circleImage);
        choseImage = findViewById(R.id.choseImage);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editMobile = findViewById(R.id.editMobile);
        editMobile.setText(mobile);
        updateBtn = findViewById(R.id.updateBtn);
        imgZoomed = findViewById(R.id.imgZoomed);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().isEmpty()) {
                    editName.setError("Name Field Can't be blank");
                    editName.requestFocus();
                } else if (editEmail.getText().toString().isEmpty()) {
                    editEmail.setError("Email Field Can't be blank");
                    editEmail.requestFocus();
                } else {
                    ProfileUpdateAPI();
                }
            }
        });
        /*Todo:- Image Choose*/
        choseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
        myProfile();

    }

    private void myProfile() {
        if (CommonUtils.isOnline(UpdateProfileActivity.this)) {
            sessonManager.showProgress(UpdateProfileActivity.this);
            Call<MyProfileModel> call = ApiExecutor.getApiService(this).apiMyProfile("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<MyProfileModel>() {
                @Override
                public void onResponse(Call<MyProfileModel> call, Response<MyProfileModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        MyProfileModel myProfileModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (myProfileModel.getData() != null) {
                                //sessonManager.setWalletAmount(String.valueOf(myProfileModel.getData().getBalance()));
                                Picasso.get().load(myProfileModel.getData().getImage()).into(circleImage);
                                Picasso.get().load(myProfileModel.getData().getImage()).into(imgZoomed);
                                editName.setText(String.valueOf(myProfileModel.getData().getName()));
                                editEmail.setText(myProfileModel.getData().getEmail());
                                //sessonManager.setMobileNo(myProfileModel.getData().getMobile());
                            }
                        }else {
                            Toast.makeText(UpdateProfileActivity.this, ""+myProfileModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")){
                                if (response.body().getMessage().equalsIgnoreCase("logout")){
                                    AuthenticationUtils.deauthenticate(UpdateProfileActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(UpdateProfileActivity.this,"");
                                            Toast.makeText(UpdateProfileActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateProfileActivity.this, LoginActivity.class));
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
                public void onFailure(Call<MyProfileModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });


        } else {
            CommonUtils.showToastInCenter(UpdateProfileActivity.this, getString(R.string.please_check_network));
        }
    }

    private void ProfileUpdateAPI() {
        if (CommonUtils.isOnline(UpdateProfileActivity.this)) {
            sessonManager.showProgress(UpdateProfileActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("name", ApiFactory.getRequestBodyFromString(editName.getText().toString()));
            partMap.put("email", ApiFactory.getRequestBodyFromString(editEmail.getText().toString()));

            MultipartBody.Part[] imageArray1 = new MultipartBody.Part[imagePathList.size()];
            //Log.d("arrayLis",""+imageArray1);

            for (int i = 0; i < imageArray1.length; i++) {
                File file = new File(imagePathList.get(i));
                try {
                    File compressedfile = new Compressor(UpdateProfileActivity.this).compressToFile(file);
                    RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                    imageArray1[i] = MultipartBody.Part.createFormData("image", compressedfile.getName(), requestBodyArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());

            Log.d("token", sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiUpdateProfile(headers, imageArray1, partMap)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            sessonManager.hideProgress();
                            JsonObject jsonObject = response.body();
                            String code = jsonObject.get("status").getAsString();
                            if (code.equals("success")) {
                                Toast.makeText(UpdateProfileActivity.this, "Profile update successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateProfileActivity.this, MyAccount.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                String msg = jsonObject.get("message").getAsString();
                                Toast.makeText(UpdateProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            sessonManager.hideProgress();
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(UpdateProfileActivity.this, getString(R.string.please_check_network));
        }

    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

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
                    ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

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
            photoUri = FileProvider.getUriForFile(UpdateProfileActivity.this, getPackageName() + ".provider", photoFile);
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
            bitmap = MediaStore.Images.Media.getBitmap(UpdateProfileActivity.this.getContentResolver(), photoUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            if (Build.VERSION.SDK_INT > 23) {
                bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), photoUri);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);

            }
            circleImage.setImageBitmap(bitmap);
            imgZoomed.setImageBitmap(bitmap);

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
                        photoPath = Helper.pathFromUri(UpdateProfileActivity.this, mImageUri);
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
                        imgZoomed.setImageBitmap(bitmap);
                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPath = Helper.pathFromUri(UpdateProfileActivity.this, mImageUri);
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
                    imgZoomed.setImageBitmap(bitmap);

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
            if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateProfileActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}