package com.example.gmagic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.gmagic.VerifyPhoneActivity.SHARED_KEY;

public class Main3Activity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    String PostDataUrl;

    ACProgressFlower dialog;

    int status;


    //ListView listView;
    ArrayList<ListOFContacts> storeListOFContacts = new ArrayList<ListOFContacts>();
    //ArrayAdapter<String> arrayAdapter;
    Cursor cursor;

    TextView contactTV;
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor("elastic", "hCKQi4CqydovNvoWugyOegPd"))
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    // ListOFContacts[] listOFContacts;

    String HeadNum, HeadName;
    TextView resultTV;
    // public static final String HeadName = "Govardhan";
    //public static final String HeadNum = "+919110517177";

    String baseUrl = "https://956aca904d144aed97754cce8e149942.ap-southeast-1.aws.found.io:9243/gmagic/contacts/";


    String name, phonenumber;
    public static final int RequestPermissionCode = 1;

    Button findAnonymous;
    // private JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY)
                .build();
        //resultTV = findViewById(R.id.resultTV);
        TextView nameTv = findViewById(R.id.namelabel);
        TextView numTv = findViewById(R.id.numlabel);
        findAnonymous = findViewById(R.id.findAnonymous);
        SharedPreferences prefs = getSharedPreferences(VerifyPhoneActivity.SHARED_KEY, MODE_PRIVATE);
        HeadName = prefs.getString(VerifyPhoneActivity.NAME, "No name defined");//"No name defined" is the default value.
        status = prefs.getInt("FirstTime", 0);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null) {
            HeadNum = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        }
        PostDataUrl = baseUrl.concat(HeadNum);
        if (HeadNum != null) {
            nameTv.setText(HeadName);
            numTv.setText(HeadNum);


        }
        Log.d(TAG, "onCreate: " + status);
        //Toast.makeText(this, "status value "+status, Toast.LENGTH_SHORT).show();
        if (status == 1 || status == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                dialog.show();
                convertJsonAndLoadData();
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_KEY, MODE_PRIVATE).edit();
                editor.putInt("FirstTime", 2);
                editor.apply();

            }
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        //textView.setText("sorry....");
        // textView.setText("\n MobileNum : "+mobNum);
        findAnonymous.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (HeadNum != null) {
                    try {
                        dialog.show();
                        findAnonymous(HeadNum);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                dialog.show();
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
                Toast.makeText(this, "logout successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.delete:
                //Toast.makeText(this, "You have selected delete", Toast.LENGTH_SHORT).show();
                if (HeadNum != null) {
                    try {
                        dialog.show();
                        delete(baseUrl, HeadNum);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case R.id.load:
                dialog.show();
                convertJsonAndLoadData();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void convertJsonAndLoadData() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        if (!storeListOFContacts.isEmpty()) {
            storeListOFContacts = new ArrayList<ListOFContacts>();
        }
        GetContactsIntoArrayList();
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray contactListArray = gson.toJsonTree(storeListOFContacts).getAsJsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", HeadName);
            jsonObject.addProperty("num", HeadNum);
            jsonObject.add("contactList", contactListArray);
            Log.d(TAG, "onClick: " + jsonObject);
            post(PostDataUrl, jsonObject.toString());


        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e.getMessage());
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public void GetContactsIntoArrayList() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int i = 0;
        assert cursor != null;
        while (cursor.moveToNext()) {
            ListOFContacts listOFContacts = new ListOFContacts();
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            listOFContacts.setName(name);
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (!phonenumber.isEmpty()) {
                try {
                    Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phonenumber, "IN");
                    String formatNumber = phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                    listOFContacts.setNum(formatNumber);
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
            } else {
                phonenumber = "+914044044044";
            }
            storeListOFContacts.add(listOFContacts);
            //jsonArray.put(gson.toJson(listOFContacts));
//
        }
        Log.d(TAG, "GetContactsIntoArrayList: " + storeListOFContacts.toString());
        cursor.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                Main3Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(Main3Activity.this, "loading Fail... : (Check Internet Connectivity) ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.toString());
                Main3Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(Main3Activity.this, "Successfully Refreshed : ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void delete(String baseUrl,
                String id) throws IOException {
        String main_URL = baseUrl.concat(id);
        Request request = new Request.Builder()
                .url(main_URL)
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                Main3Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(Main3Activity.this, "Failure:(Bro check internet connectivity..) ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.toString());
                Main3Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(Main3Activity.this, "Successfully deleted your whole data..BYE Miss u: ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void findAnonymous(
            String id) throws IOException {
        String main_URL = "https://956aca904d144aed97754cce8e149942.ap-southeast-1.aws.found.io:9243/gmagic/contacts/_search?q=contactList.num:" + id + "&filter_path=hits.hits._id,hits.hits._source.num,hits.hits._source.name";
        Request request = new Request.Builder()
                .url(main_URL)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                Main3Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //resultTV.setText("Fail! CHECK INTERNET CONNECTIVITY...");
                        dialog.dismiss();
                        Toast.makeText(Main3Activity.this, "Failure : Check Internet Connectivity.. ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Main3Activity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: " + myResponse);
                            if (myResponse.equals("{}")) {
                                dialog.dismiss();
                                Toast.makeText(Main3Activity.this, "no contacts persist at this number", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(myResponse);
                                    JSONObject actualdata = jsonObject.getJSONObject("hits");
                                    JSONArray data = actualdata.getJSONArray("hits");
                                    Log.e(TAG, "run: actual data" + myResponse);
                                    // JSONArray dataHits = data.getJSONArray("hits");
                                    List<Contact> contacts = new ArrayList<>();
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject cont = data.getJSONObject(i);
                                        Contact contact = new Contact();
                                        JSONObject source = cont.getJSONObject("_source");
                                        contact.setName(source.getString("name"));
                                        contact.setNum(cont.getString("_id"));
                                        contacts.add(contact);


                                    }
                                    dialog.dismiss();
                                    Log.d(TAG, "run: " + contacts);
                                    findAnonymousIntent(contacts);
                                /*StringBuilder stringBuilder = new StringBuilder();
                                for (Contact con : contacts) {
                                    String str = con.getName() + " : " + con.getNum();
                                    stringBuilder.append("\n" + str);

                                }
//                                resultTV.setText(stringBuilder + "");*/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                }
            }

        });


    }


    public void searchNumber(View view) {
        startActivity(new Intent(this, SearchNumber.class));
    }

    public void searchName(View view) {
        startActivity(new Intent(this, SearchName.class));
    }

    public void findAnonymous(View view) {
        if (HeadNum != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    findAnonymous(HeadNum);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //findAnonymousIntent();
    }

    private void findAnonymousIntent(List<Contact> contacts) {
        Log.d(TAG, "findAnonymousIntent: " + contacts);
        if (contacts.size() > 0) {
            Intent intent = new Intent(this, FindAnonymous.class);
            /*Bundle args = new Bundle();
            args.putSerializable("contactList",(Serializable)contacts);*/
            intent.putExtra("contactList", (ArrayList<Contact>) contacts);
            startActivity(intent);


        } else {
            Toast.makeText(this, "sorry no contacts loaded...", Toast.LENGTH_SHORT).show();
        }
    }

    public void advancedsearchName(View view) {
        Intent intent = new Intent(this, AdvancedSearchNumber.class);
        startActivity(intent);
    }

    public void prefixNameSearch(View view) {
        Intent intent = new Intent(this, PrefixNameSearch.class);
        startActivity(intent);
    }


    public void endedWithThreeNumberSearch(View view) {
        Intent intent = new Intent(this, SuffixNumberSearch.class);
        startActivity(intent);
    }

}






