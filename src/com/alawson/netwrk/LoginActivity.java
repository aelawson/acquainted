package com.alawson.netwrk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alawson.netwrk.RegisterActivity.RegisterAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {
	
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_ERROR = "error";
	private static final String KEY_MESSAGE = "message";
	private static final String URL_DATABASE = "http://www.aquainted.andrewlawson.us/index.php";
	
	EditText fieldUsername, fieldPassword;
	Button buttonLogin, buttonRegister, buttonForgot;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Instantiate fields
        fieldUsername = (EditText) findViewById(R.id.editText_loginUsername);
        fieldPassword = (EditText) findViewById(R.id.editText_loginPassword);
        // Login button leads to authentication and home activity
        buttonLogin = (Button) findViewById(R.id.button_loginLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fieldUsername.getText().toString() != "" && fieldPassword.getText().toString() != "") {
					new Login().execute();
				}
            }
        });
        // Registration button leads to registration activity
        buttonRegister = (Button) findViewById(R.id.button_loginRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentRegister = new Intent(getBaseContext(), RegisterActivity.class);
				startActivity(intentRegister);
			}
		});
        // Forgot password button
        buttonForgot = (Button) findViewById(R.id.button_loginForgot);
        buttonForgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentForgot = new Intent(getBaseContext(), ForgotActivity.class);
				startActivity(intentForgot);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public class Login extends AsyncTask<Object, Void, JSONObject> {
		ProgressDialog dialogRegister;
		InputStream inputStream;
		@Override
		protected void onPreExecute() {
			dialogRegister = new ProgressDialog(LoginActivity.this);
			dialogRegister.setTitle("Log-in");
			dialogRegister.setMessage("Logging in...");
			dialogRegister.setIndeterminate(false);
			dialogRegister.setCancelable(true);
			dialogRegister.show();
		}
		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject jsonRegister = null;
			String responseString = null;
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("request", "login"));
			parameters.add(new BasicNameValuePair(KEY_USERNAME, fieldUsername.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_PASSWORD, fieldPassword.getText().toString()));
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(URL_DATABASE);
				httpPost.setEntity(new UrlEncodedFormEntity(parameters));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
			} 
			catch (ClientProtocolException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BufferedReader httpReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder stringBuilder = new StringBuilder();
				String currentLine;
				while ((currentLine = httpReader.readLine()) != null) {
					stringBuilder.append(currentLine + "\n");
				}
				inputStream.close();
				responseString = stringBuilder.toString();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				jsonRegister = new JSONObject(responseString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonRegister;
		}
		@Override
		protected void onPostExecute(JSONObject jsonResponse) {
			try {
				String responseError = jsonResponse.getString(KEY_ERROR);
				String responseMessage = jsonResponse.getString(KEY_MESSAGE);
				CountDownTimer timer = 	new CountDownTimer(2000, 1000) {
			        @Override
			        public void onTick(long millisUntilFinished) {
			        }
			        @Override
			        public void onFinish() {
			        	dialogRegister.dismiss();
			        }
			    };
				if (Integer.parseInt(responseError) == 0) {
	                // Go to homescreen on click
		        	dialogRegister.dismiss();
	                Intent intentHome = new Intent(getBaseContext(), HomeActivity.class);
	                startActivity(intentHome);
				}
				else {
					dialogRegister.setMessage(responseMessage);
					timer.start();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}