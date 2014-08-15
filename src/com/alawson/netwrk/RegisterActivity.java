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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {
	
	private static final String KEY_USERNAME 			= "username";
	private static final String KEY_PASSWORD			= "password";
	private static final String KEY_PASSWORD_CONFIRM	= "passwordConfirm";
	private static final String KEY_FNAME 				= "firstname";
	private static final String KEY_LNAME 				= "lastname";
	private static final String KEY_EMAIL 				= "email";
	
	private static final String KEY_ERROR = "error";
	private static final String KEY_MESSAGE = "message";
	
	private static final String URL_DATABASE = "http://www.aquainted.andrewlawson.us/index.php";
	
	EditText fieldUsername, fieldPassword, fieldPasswordConfirm, fieldFirstName, fieldLastName, fieldEmail;
	Button buttonRegister;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Input fields
		fieldUsername = (EditText) findViewById(R.id.editText_registerUsername);
		fieldPassword = (EditText) findViewById(R.id.editText_registerPassword);
		fieldPasswordConfirm = (EditText) findViewById(R.id.editText_registerPasswordConfirm);
		fieldFirstName = (EditText) findViewById(R.id.editText_registerFirstName);
		fieldLastName = (EditText) findViewById(R.id.editText_registerLastName);
		fieldEmail = (EditText) findViewById(R.id.editText_registerEmail);
		// Registration button leads to account creation
		buttonRegister = (Button) findViewById(R.id.button_registerRegister);
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fieldUsername.getText().toString() != "" && fieldPassword.getText().toString() != ""
						&& fieldEmail.getText().toString() != "" && fieldFirstName.getText().toString() != ""
						&& fieldLastName.getText().toString() != "") {
					new RegisterAccount().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "One or more fields are invalid.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	public class RegisterAccount extends AsyncTask<Object, Void, JSONObject> {
		ProgressDialog dialogRegister;
		InputStream inputStream;
		@Override
		protected void onPreExecute() {
			dialogRegister = new ProgressDialog(RegisterActivity.this);
			dialogRegister.setTitle("Registration");
			dialogRegister.setMessage("Registering account...");
			dialogRegister.setIndeterminate(false);
			dialogRegister.setCancelable(true);
			dialogRegister.show();
		}
		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject jsonRegister = null;
			String responseString = null;
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("request", "register"));
			parameters.add(new BasicNameValuePair(KEY_USERNAME, fieldUsername.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_PASSWORD, fieldPassword.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_PASSWORD_CONFIRM, fieldPasswordConfirm.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_EMAIL, fieldEmail.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_FNAME, fieldFirstName.getText().toString()));
			parameters.add(new BasicNameValuePair(KEY_LNAME, fieldLastName.getText().toString()));
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
			if (!fieldPassword.toString().equals(fieldPasswordConfirm.toString())) {
				dialogRegister.setMessage("Passwords did not match.");
			}
			try {
				String responseError = jsonResponse.getString(KEY_ERROR);
				String responseMessage = jsonResponse.getString(KEY_MESSAGE);
				if (responseError != null) {
					if (Integer.parseInt(responseError) == 0) {
						Log.e("RETURNED", "SUCCESS");
						dialogRegister.dismiss();
					}
					else if (Integer.parseInt(responseError) == 1) {
						Log.e("RETURNED", "USER EXISTS");
						dialogRegister.setMessage(responseMessage);
					}
					else if (Integer.parseInt(responseError) == 2) {
						Log.e("RETURNED", "EMAIL EXISTS");
					}
				}
				else {
					dialogRegister.dismiss();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
