package com.example.gmagic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FindAnonymous extends AppCompatActivity {

    private static final String TAG = "Avatar";
    private ArrayList<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;

    @Override
    protected void onStart() {
        super.onStart();

        /*
        * Intent intent = getIntent();
Bundle args = intent.getBundleExtra("BUNDLE");
ArrayList<Object> object = (ArrayList<Object>) args.getSerializable("ARRAYLIST");*/

        /*Intent intent = getIntent();
        //Bundle args = intent.getBundleExtra("BUNDLE");
        contactList = (ArrayList<Contact>) intent.getSerializableExtra("contactList");*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_anonymous);

        Intent intent = getIntent();
        //Bundle args = intent.getBundleExtra("BUNDLE");
        contactList = (ArrayList<Contact>) intent.getSerializableExtra("contactList");
        Log.d(TAG, "onStart: "+contactList);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ContactAdapter(contactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //mAdapter.notifyDataSetChanged();


    }
}
