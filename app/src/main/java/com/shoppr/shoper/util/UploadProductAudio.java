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
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.util.List;

public class UploadProductAudio extends AsyncTask<File, String, String> {


    CallBackImageUpload callBackImageUpload;
    Context context;
    String returnImageUrl = "";
    public UploadProductAudio(Context context, CallBackImageUpload callBackImageUpload) {
        this.callBackImageUpload = callBackImageUpload;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(File... path) {


        String ACCESS_KEY = SocketInfo.AWS_SECRET_ACCESS_KEY;
        String    SECRET_KEY = SocketInfo.KEY_AWS_SECRET_KEY;
        String     MY_BUCKET = SocketInfo.AWS_BUCKET;
        Log.e("ACCESS_KEY ", "" + ACCESS_KEY);
        Log.e("SECRET_KEY ", "" + SECRET_KEY);
        Log.e("MY_BUCKET ", "" + MY_BUCKET);
        String     OBJECT_KEY = "unique_id";
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
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("audio/mpeg");
            final String fileName = System.currentTimeMillis() + ".mp3";
            //TransferObserver observer = transferUtility.upload(MY_BUCKET, fileName, path[0],metadata, CannedAccessControlList.PublicRead);
            TransferObserver observer = transferUtility.upload(MY_BUCKET, fileName, path[0], CannedAccessControlList.PublicRead);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    String url = "https://shoppr-bucket.s3.ap-south-1.amazonaws.com/" + fileName;

                    if (state == TransferState.COMPLETED) {
                        returnImageUrl = url;
                        Log.e("TAG", "Image Url: " + url);
                        callBackImageUpload.onImageUpload(returnImageUrl);
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
                    Log.e("Error_Response_message", "" + ex.getMessage());
                    returnImageUrl = "Error";
                    callBackImageUpload.onImageUpload(returnImageUrl);
                }

            });


        return returnImageUrl;
    }


    @Override
    protected void onPostExecute(String imageUrl) {
        super.onPostExecute(imageUrl);


    }


    public interface CallBackImageUpload {
        void onImageUpload(String imageUrl);

        void onErrorOccured(String error);
    }
}
