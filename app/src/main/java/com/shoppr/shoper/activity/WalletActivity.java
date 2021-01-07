package com.shoppr.shoper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.shoppr.shoper.R;

import com.shoppr.shoper.adapter.RecyclerAdapter;
import model.RecyclerModel;

public class WalletActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        getSupportActionBar().setTitle("Wallet Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewData();
    }

    private void viewData() {
        RecyclerModel[]recyclerModels=new RecyclerModel[]{
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),
                new RecyclerModel("Thursday,Jul 05,2020",
                        "Add Money","transaction ID : #000111",
                        "45,523","45,523",
                        "5600","Rs 10,000 received from\nMohammad Imran\n(xxxxxx9876)"),

        };
        RecyclerAdapter recyclerAdapter=new RecyclerAdapter(WalletActivity.this,recyclerModels);
        recyclerView.setAdapter(recyclerAdapter);
    }

   /* public void back(View view) {
        onBackPressed();
    }*/
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle action bar item clicks here. The action bar will
       // automatically handle clicks on the Home/Up button, so long
       // as you specify a parent com.example.shoper.activity in AndroidManifest.xml.

       //noinspection SimplifiableIfStatement
       int id = item.getItemId();
       if (id==android.R.id.home){
           onBackPressed();
       }

       return super.onOptionsItemSelected(item);
   }
}