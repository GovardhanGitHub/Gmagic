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
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchNumber extends AppCompatActivity {


    private static final String TAG = "Search_Num";
    ArrayList<Contact> contacts;
    String myResponse;
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    String hitFailure, num;
    ACProgressFlower dialog;


    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor("elastic", "hCKQi4CqydovNvoWugyOegPd"))
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    EditText searchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_number);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY)
                .build();
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

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void serachclick(View view) throws IOException {
        String name = searchName.getText().toString().trim();
        if (name.isEmpty() || name.length() != 10) {
            searchName.setError("Enter a valid Number");
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
        String url = "https://956aca904d144aed97754cce8e149942.ap-southeast-1.aws.found.io:9243/gmagic/_doc/" + id + "?filter_path=_id,_source.name";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                SearchNumber.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //resultTV.setText("Fail! CHECK INTERNET CONNECTIVITY...");
                        dialog.dismiss();
                        Toast.makeText(SearchNumber.this, "Failure : Check Internet Connectivity.. ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    myResponse = response.body().string();
                    SearchNumber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: " + myResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(myResponse.toString());
                                String id = jsonObject.getString("_id");
                                JSONObject source = jsonObject.getJSONObject("_source");
                                String name = source.getString("name");
                                Log.e(TAG, "run: actual data" + myResponse);
                                Contact contact = new Contact();
                                List<Contact> contacts = new ArrayList<>();
                                contact.setName(name);
                                contact.setNum(id);
                                contacts.add(contact);
                                Log.d(TAG, "run: " + contacts.size());
                                if (contacts.size() == 0) {
                                    Toast.makeText(SearchNumber.this, "No Contacts this Number(Try pro version)", Toast.LENGTH_SHORT).show();

                                } else {
                                    Log.d(TAG, "run: " + contacts);
                                    // Toast.makeText(SearchNumber.this, "List of contacts " + contacts, Toast.LENGTH_SHORT).show();
                                    //connect recyclerView
                                    recyclerView = (RecyclerView) findViewById(R.id.Search_Number_recycler_view);
                                    mAdapter = new ContactAdapter(contacts);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                    dialog.dismiss();
                                    //Toast.makeText(SearchNumber.this, "Cannot load contacts..(something went wrong)", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    SearchNumber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(SearchNumber.this, "No Contacts this Number..(Try in  Searchnumber(pro) version)", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }

        });


    }

}
