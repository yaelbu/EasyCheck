package com.example.easycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {


    Toast toast;

    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private TextView user_name,returned_value;
    DocumentReference ref;
    Button check_btn;
    EditText car_plate_number;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    //this is api link to check weather the car is
    //https://data.gov.il/api/3/action/datastore_search?resource_id=c8b9f9c8-4612-4068-934f-d4acd2e3c06e&q=%222226721%22


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_name = findViewById(R.id.user_name);
        db = FirebaseFirestore.getInstance();
        check_btn = findViewById(R.id.check_button);
        car_plate_number = findViewById(R.id.carplate);
        String userpath = "users"+"/"+WelcomeScreen.uuid;
        ref = db.collection("users").document(WelcomeScreen.uuid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        user_name.setText(doc.getData().get("firstname").toString());
                    }
                }
            }
        });

        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car_plate_number.getText().toString().isEmpty()){
                    car_plate_number.setError(getString(R.string.car_plate_field));
                    return;
                }

                new yourDataTask()
                        .execute();
            }
        });

}


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }
    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                System.out.println("Response : "+line);
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pd.isShowing()){
            pd.dismiss();
        }
        txtJson.setText(result);
    }
}

    protected class yourDataTask extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {

            String str="https://data.gov.il/api/3/action/datastore_search?resource_id=c8b9f9c8-4612-4068-934f-d4acd2e3c06e&q=%22"+car_plate_number.getText().toString()+"%22";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {

            if(response != null)
            {
                try {
                    JSONObject jsonObj = response.getJSONObject("result");
                    JSONArray array = (JSONArray )jsonObj.get("records");
                    if(array.length() == 0){
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.zero_cust_toast_layout,
                                (ViewGroup)findViewById(R.id.relativeLayout1));

                        Toast toast = new Toast(MainActivity.this);
                        toast.setView(view);
                        toast.show();
                    }else {

                        if(array.getJSONObject(0).get("SUG TAV").toString().equals("01")){
                            try{

                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.cust_toast_layout,
                                    (ViewGroup)findViewById(R.id.relativeLayout1));

                            Toast toast = new Toast(MainActivity.this);
                            toast.setView(view);
                            toast.show();
                            }catch(Exception ex){
                                System.out.println(ex.toString());
                            }
                        }
                        else if(array.getJSONObject(0).get("SUG TAV").toString().equals("02")) {
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.cust1_toast_layout,
                                    (ViewGroup) findViewById(R.id.relativeLayout1));

                            Toast toast = new Toast(MainActivity.this);
                            toast.setView(view);
                            toast.show();
                        }
                        else if(array.getJSONObject(0).get("SUG TAV").toString().equals("03")) {
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.cust2_toast_layout,
                                    (ViewGroup) findViewById(R.id.relativeLayout1));

                            Toast toast = new Toast(MainActivity.this);
                            toast.setView(view);
                            toast.show();
                        }
                        else  {
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.cust3_toast_layout,
                                    (ViewGroup) findViewById(R.id.relativeLayout1));

                            Toast toast = new Toast(MainActivity.this);
                            toast.setView(view);
                            toast.show();
                        }

                    }

                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }
    }
