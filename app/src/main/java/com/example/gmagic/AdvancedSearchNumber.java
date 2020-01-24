package com.example.gmagic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressCustom;
import cc.cloudist.acplibrary.ACProgressFlower;
import cc.cloudist.acplibrary.ACProgressPie;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdvancedSearchNumber extends AppCompatActivity {


    private static final String TAG = "Search_Num";
    ArrayList<Contact> contacts;
    String myResponse;
    private RecyclerView recyclerView;
    private WholeContactAdapter mAdapter;
    String hitFailure,num;
    List<WholeContact> wholeContacts;

    ACProgressCustom dialog;


    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor("elastic", "hCKQi4CqydovNvoWugyOegPd"))
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    EditText searchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_number);


         dialog = new ACProgressCustom.Builder(this)
                .useImages(R.drawable.load).build();



        searchName = findViewById(R.id.num_search);
        searchName.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            try {

                                serachclick(v);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });


    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void serachclick(View view) throws IOException {
        String name = searchName.getText().toString().trim();

        if (name.isEmpty() || name.length() != 10) {
            searchName.setError("Enter a valid name");
            searchName.requestFocus();
            return;
        }
        hideSoftKeyboard(view);
        num = "+91".concat(name);
        searchName.getText().clear();
        dialog.show();
        findNumberThroughMasterName(num);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void findNumberThroughMasterName(String id) throws IOException {

        String url ="https://956aca904d144aed97754cce8e149942.ap-southeast-1.aws.found.io:9243/gmagic/_search?q=contactList.num:"+id+"&filter_path=hits.hits._id,hits.hits._source";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: (Bro check internet Connectivity..)"+e.getMessage());
                AdvancedSearchNumber.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //resultTV.setText("Fail! CHECK INTERNET CONNECTIVITY...");
                        dialog.dismiss();
                        Toast.makeText(AdvancedSearchNumber.this, "Failure : Check Internet Connectivity.. ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                myResponse = response.body().string();


                if (!myResponse.equals("{}")) {






                    AdvancedSearchNumber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: " + myResponse);
                            try {

                                JSONObject mainObj = new JSONObject(myResponse);
                                JSONObject hitsObject = mainObj.getJSONObject("hits");
                                JSONArray hitsArray = hitsObject.getJSONArray("hits");
                                //JSONArray dir = mainObj.getJSONArray("hits.hits");

                                wholeContacts = new ArrayList<>();

                                for (int i = 0; i < hitsArray.length(); i++) {
                                    WholeContact wholeContact = new WholeContact();

                                    JSONObject mainData = hitsArray.getJSONObject(i);
                                    Log.e(TAG, "run: "+mainData);
                                    //String mainMobileNum = mainData.getString("_id");
                                    wholeContact.num = mainData.getString("_id");


                                    JSONObject sourceobj = mainData.getJSONObject("_source");

                                   // String name = sourceobj.getString("name");
                                    wholeContact.name = sourceobj.getString("name");


                                    JSONArray listofcontacts = sourceobj.getJSONArray("contactList");

                                    int j = 0;

                                    for (; j < listofcontacts.length(); ) {
                                       // WholeContact.Subcontact subcontact = new WholeContact.Subcontact();
                                        Contact contact = new Contact();
                                        //subcontact.subName =

                                        JSONObject subJOSNContact = listofcontacts.getJSONObject(j);

                                        if (subJOSNContact.has("name"))
                                        {
                                            contact.name= subJOSNContact.getString("name");
                                        }else {
                                            contact.name = "Name not mentioned";
                                        }

                                        if (subJOSNContact.has("num"))
                                        {
                                            contact.num= subJOSNContact.getString("num");
                                        }else {
                                            contact.num = "num not mentioned";
                                        }

                                        //contact.num = subJOSNContact.getString("num");


                                        if(num.equals(contact.num)){
                                            wholeContact.subcontacts.add(contact);
                                            break;
                                        }


                                        j++;

                                    }
                                    wholeContacts.add(wholeContact);
                                }



                                    recyclerView = (RecyclerView) findViewById(R.id.Advanced_Search_Number_recycler_view);

                                    mAdapter = new WholeContactAdapter(wholeContacts);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                    dialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }else {
                    AdvancedSearchNumber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: "+myResponse);
                            dialog.dismiss();
                            Toast.makeText(AdvancedSearchNumber.this, num+" Details not Found.. ", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }

        });


    }

}
