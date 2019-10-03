package com.example.databasephp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";
    Button button1,button2;
    EditText editText,editText2;
    private String login_url = "https://mayurichintalwar.000webhostapp.com/dj/login.php";
    String Username,Passwaord;
    ProgressDialog pDialog;
    private SessionHandler session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.btnLogin);
        button2=findViewById(R.id.btnLoginRegister);
        editText=findViewById(R.id.etLoginUsername);
        editText2=findViewById(R.id.etLoginPassword);



        if(session.isLoggedIn()){
            loadDashboard();

        }
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Successfully Loged In", Toast.LENGTH_SHORT).show();
                Username = editText.getText().toString().toLowerCase().trim();
                Passwaord = editText2.getText().toString().trim();
                if(validInputs()){
                    login();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadDashboard() {

        Intent i=new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, Username);
            request.put(KEY_PASSWORD, Passwaord);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                session.loginUser(Username,response.getString(KEY_FULL_NAME));
                                loadDashboard();

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private boolean validInputs() {
        if(KEY_EMPTY.equals(Username)){
            editText.setError("Username cannot be empty");
            editText.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(Passwaord)){
            editText2.setError("Password cannot be empty");
            editText2.requestFocus();
            return false;
        }
        return true;
    }
}
