package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shoppr.shoper.Model.StoreListDetails.Image;
import com.shoppr.shoper.R;
import com.shoppr.shoper.util.SessonManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    SessonManager sessonManager;
    /*Todo:- CircleImageView*/
    CircleImageView circleImage;
    /*Todo:- ImageView*/
    ImageView choseImage;
    /*Todo:- EditText*/
    EditText editName,editEmail,editMobile;
    /*Todo:- Button*/
    Button updateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        sessonManager=new SessonManager(this);
        String mobile=sessonManager.getMobileNo();
        /*Todo:- CircleImageView find id*/
        circleImage=findViewById(R.id.circleImage);
        /*Todo:- ImageView find id*/
        choseImage=findViewById(R.id.choseImage);
        /*Todo:- EditText find id*/
        editName=findViewById(R.id.editName);
        editEmail=findViewById(R.id.editEmail);
        editMobile=findViewById(R.id.editMobile);
        editMobile.setText(mobile);
        /*Todo:- Button find id*/
        updateBtn=findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}