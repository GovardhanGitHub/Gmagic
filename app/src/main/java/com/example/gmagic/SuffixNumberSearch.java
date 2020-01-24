package com.example.gmagic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.cloudist.acplibrary.ACProgressCustom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SuffixNumberSearch extends AppCompatActivity {




        private static final String TAG = "Suffix_Search_Num";
        ArrayList<Contact> contacts;
        String myResponse;
        private RecyclerView recyclerView;
        private ContactAdapter mAdapter;
        String hitFailure,num;
        //List<WholeContact> wholeContacts;

        Set<Contact> contactSet;

        ACProgressCustom dialog;


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("elastic", "hCKQi4CqydovNvoWugyOegPd"))
                .build();
        public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        EditText searchNumber;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suffix_number_search);


        dialog = new ACProgressCustom.Builder(this)
                .useImages(R.drawable.load).build();



        searchNumber = findViewById(R.id.num_search);
        searchNumber.setOnEditorActionListener(
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
        String name = searchNumber.getText().toString().trim();

        //|| name.length() != 3
        if (name.isEmpty() || name.length() != 3) {
            searchNumber.setError("Suffix mobile number search feature need  only last 3 digits  ");
            searchNumber.requestFocus();
            return;
        }
        hideSoftKeyboard(view);
        num = name;
        searchNumber.getText().clear();
        dialog.show();
        findNumberThroughMasterName(num);
    }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        void findNumberThroughMasterName(final String id) throws IOException {



            final String wildid = "*"+id;
            String qp = "{\n" +
                    "    \"query\": {\n" +
                    "       \"wildcard\": {\n" +
                    "            \"contactList.num\": {\n" +
                    "                \"value\": \""+wildid+"\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";


        String url ="https://956aca904d144aed97754cce8e149942.ap-southeast-1.aws.found.io:9243/gmagic/_search";
            RequestBody requestBody = RequestBody.create(JSON,qp);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            Request request = builder
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: (Bro check internet Connectivity..)"+e.getMessage());
                SuffixNumberSearch.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //resultTV.setText("Fail! CHECK INTERNET CONNECTIVITY...");
                        dialog.dismiss();
                        Toast.makeText(SuffixNumberSearch.this, "Failure : Check Internet Connectivity.. ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                myResponse = response.body().string();


                if (!myResponse.equals("{}")) {






                    SuffixNumberSearch.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: " + myResponse);
                            try {
                                String ends = "495";

                                JSONObject mainObj = new JSONObject(myResponse);
                                JSONObject hitsObject = mainObj.getJSONObject("hits");
                                JSONArray hitsArray = hitsObject.getJSONArray("hits");
                                //JSONArray dir = mainObj.getJSONArray("hits.hits");

                                if (hitsArray.length() == 0)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(SuffixNumberSearch.this, wildid+" did not ends with any contact numbers", Toast.LENGTH_SHORT).show();
                                }

                                else {
                                   // wholeContacts = new ArrayList<>();
                                    contactSet = new HashSet<>();
                                    for (int i = 0; i < hitsArray.length(); i++) {
                                        //WholeContact wholeContact = new WholeContact();
                                        JSONObject mainData = hitsArray.getJSONObject(i);
                                        Log.e(TAG, "run: " + mainData);
                                        //String mainMobileNum = mainData.getString("_id");
                                        //wholeContact.num = mainData.getString("_id");
                                        JSONObject sourceobj = mainData.getJSONObject("_source");
                                        // String name = sourceobj.getString("name");
                                       // wholeContact.name = sourceobj.getString("name");
                                        JSONArray listofcontacts = sourceobj.getJSONArray("contactList");
                                        int j = 0;
                                        for (; j < listofcontacts.length(); ) {
                                            // WholeContact.Subcontact subcontact = new WholeContact.Subcontact();
                                            Contact contact = new Contact();
                                            //subcontact.subName =
                                            JSONObject subJOSNContact = listofcontacts.getJSONObject(j);
                                            if (subJOSNContact.has("name")) {
                                                contact.name = subJOSNContact.getString("name");
                                            } else {
                                                contact.name = "Name not mentioned";
                                            }
                                            if (subJOSNContact.has("num")) {
                                                contact.num = subJOSNContact.getString("num");
                                            } else {
                                                contact.num = "Number not mentioned";
                                            }
                                            //check contact prefix condition
                                            String contactNum = contact.getNum();
                                            if (contactNum.endsWith(id)) {
                                                contactSet.add(contact);
                                            }
                                           /* if (contactNum.endsWith(id)) {
                                                wholeContact.subcontacts.add(contact);

                                            }*/
                                            j++;

                                        }
                                        //wholeContacts.add(wholeContact);
                                    }
                                    Toast.makeText(SuffixNumberSearch.this, "set list" + contactSet, Toast.LENGTH_SHORT).show();
                                    recyclerView = (RecyclerView) findViewById(R.id.suffix_Search_Number_recycler_view);
                                    mAdapter = new ContactAdapter(contactSet);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                    dialog.dismiss();
                                }

                            } catch (JSONException e) {
                                dialog.dismiss();
                                Toast.makeText(SuffixNumberSearch.this, "Something went wrong"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                        }
                    });

                }else {
                    SuffixNumberSearch.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: "+myResponse);
                            dialog.dismiss();
                            Toast.makeText(SuffixNumberSearch.this, num+" Details not Found.. ", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }

        });


    }

    }
