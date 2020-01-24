package com.example.gmagic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadContacts extends AppCompatActivity {

    TextView contacts;
    private static final String TAG = "LoadContact ";

    ArrayList StoreContacts = new ArrayList<Contact>();


    JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_contacts);
        contacts = findViewById(R.id.contacts);
        GetContactsIntoArrayList();
        contacts.setText(jsonArray.toString());


    }

    public void GetContactsIntoArrayList() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            Contact contact = new Contact();
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            try {
                jsonObject.put("name", name);
                jsonObject.put("num", phonenumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            contact.setName(name);
            contact.setNum(phonenumber);
            if (contact == null) {
                contact.setNum("no Name");
                contact.setNum("No number");
            }
            StoreContacts.add(contact);
            jsonArray.put(jsonObject);
//
        }
        Log.d(TAG, "GetContactsIntoArrayList: " + StoreContacts.toString());
        cursor.close();

    }


}
