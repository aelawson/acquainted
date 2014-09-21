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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotActivity extends ActionBarActivity {
	
	private static final String KEY_USERNAME = "username";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_MESSAGE = "message";
	private static final String URL_DATABASE = "http://www.aquainted.andrewlawson.us/index.php";
	
	EditText fieldUsername, fieldEmail;
	Button buttonForgot;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot);
		// Input fields
		fieldUsername = (EditText) findViewById(R.id.editText_forgotUsername);
		fieldEmail = (EditText) findViewById(R.id.editText_forgotEmail);
		// Registration button leads to account creation
		buttonForgot = (Button) findViewById(R.id.button_forgotForgot);
		buttonForgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fieldUsername.getText().toString() != "" && fieldEmail.getText().toString() != "") {
					new forgotAccount().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "One or more fields are empty.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	public class forgotAccount extends AsyncTask<Object, Void, JSONObject> {
		ProgressDialog dialogForgot;
		InputStream inputStream;
		@Override
		protected void onPreExecute() {
			dialogForgot = new ProgressDialog(ForgotActivity.this);
			dialogForgot.setTitle("Forgot password.");
			dialogForgot.setMessage("Resetting password...");
			dialogForgot.setIndeterminate(false);
			dialogForgot.setCancelable(true);
			dialogForgot.show();
		}
		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject jsonforgot = null;
			String responseString = null;
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("request", "forgot"));
			parameters.add(new BasicNameValuePair(KEY_USERNAME, fieldUsername.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_EMAIL, fieldEmail.getText().toString()));
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
				jsonforgot = new JSONObject(responseString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonforgot;
		}
		@Override
		protected void onPostExecute(JSONObject jsonResponse) {
			try {
				String responseMessage = jsonResponse.getString(KEY_MESSAGE);
				CountDownTimer timer = 	new CountDownTimer(2000, 1000) {
			        @Override
			        public void onTick(long millisUntilFinished) {
			        }
			        @Override
			        public void onFinish() {
			        	dialogForgot.dismiss();
			        }
			    };
				dialogForgot.setMessage(responseMessage);
				timer.start();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
