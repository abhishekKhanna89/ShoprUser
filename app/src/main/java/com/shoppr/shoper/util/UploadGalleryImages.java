package com.shoppr.shoper.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;





public class UploadGalleryImages extends AsyncTask<ArrayList<ImageArr>, String, ArrayList<String>> {
    CallBackImageUpload callBackImageUpload;
    Context context;
    ArrayList<String> imageUrlList = new ArrayList<>();
    public UploadGalleryImages(Context context, CallBackImageUpload callBackImageUpload) {
        this.callBackImageUpload = callBackImageUpload;
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected ArrayList<String> doInBackground(ArrayList<ImageArr>... lists) {
        final List<ImageArr> imageList = lists[0];
        String ACCESS_KEY = SocketInfo.AWS_SECRET_ACCESS_KEY;
        String  SECRET_KEY = SocketInfo.KEY_AWS_SECRET_KEY;
        String  MY_BUCKET = SocketInfo.AWS_BUCKET;

        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        s3.setEndpoint("https://s3.ap-south-1.amazonaws.com/");
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
            Log.e("Bucket ", "Name " + bucket.getName() + " Owner " + bucket.getOwner() + " Date " + bucket.getCreationDate());
        }
        Log.e("Size ", "" + s3.listBuckets().size());

        TransferNetworkLossHandler.getInstance(context);
        TransferUtility transferUtility = new TransferUtility(s3, context);


        for (ImageArr imgPath : imageList) {
            File file = imgPath.getImage();
           final String fileName = System.currentTimeMillis() + ".jpg";
            Log.e("TAG", "Image_Url_response: " + fileName);

            TransferObserver observer = transferUtility.upload(MY_BUCKET, fileName, file, CannedAccessControlList.PublicRead);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    String url = "https://shoppr-bucket.s3.ap-south-1.amazonaws.com/" + fileName;

                    if (state == TransferState.COMPLETED) {
                        imageUrlList.add(url);
                        Log.e("TAG", "Image_Url_response: " + url);
                    }

                    if (imageList.size() == imageUrlList.size()) {
                        Log.e("TAG", "Manually Called: ");
                        callBackImageUpload.onImageUpload(imageUrlList);
                    }
                }
                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    Log.e("TAG", "pre : " + percentage);
                }
                @Override
                public void onError(int id, Exception ex) {
                    // do something
                    Log.e("Error occured:   ", "" + ex.getMessage());
                    callBackImageUpload.onErrorOccured(ex.getMessage());

                }
            });
        }
        return imageUrlList;
    }
    @Override
    protected void onPostExecute(ArrayList<String> imagUrlList) {
        super.onPostExecute(imagUrlList);
    }
    public interface CallBackImageUpload {
        void onImageUpload(ArrayList<String> imageUrlList);
        void onErrorOccured(String error);
    }
}
