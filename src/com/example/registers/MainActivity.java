package com.example.registers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		Button registerButton = (Button) this
				.findViewById(R.id.button_register);
		registerButton.setOnClickListener(new RegisterClickListener());

		Button loginButton = (Button) this.findViewById(R.id.button_login);
		loginButton.setOnClickListener(new LoginClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public class RegisterClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this,
					RegisterActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}

	public class LoginClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				String methodName = "Singin";
				SoapObject rpc = new SoapObject(RegisterActivity.nameSpace,
						methodName);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				String email = ((EditText) MainActivity.this
						.findViewById(R.id.main_text_email)).getText()
						.toString();
				String password = ((EditText) MainActivity.this
						.findViewById(R.id.main_text_password)).getText()
						.toString();

				rpc.addProperty("email", email);
				rpc.addProperty("password", password);

				envelope.encodingStyle = "UTF-8";
				envelope.dotNet = true;
				envelope.bodyOut = rpc;
				envelope.setOutputSoapObject(rpc);
				HttpTransportSE transport = new HttpTransportSE(
						RegisterActivity.endPoint);
				transport.call(RegisterActivity.nameSpace + methodName,
						envelope);
				Object object = envelope.getResponse();

				String result = object.toString();
				if (result == null || result.isEmpty()==true) {
					Toast.makeText(MainActivity.this, "Ê§°Ü", Toast.LENGTH_LONG).show();					
				} else {
					Toast.makeText(MainActivity.this, "³É¹¦", Toast.LENGTH_LONG).show();

					Intent intent = new Intent(MainActivity.this,
							WelcomeActivity.class);
					intent.putExtra("email",email);
					intent.putExtra("id", result);
					MainActivity.this.startActivity(intent);
					MainActivity.this.finish();

				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(MainActivity.this, e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}

/*
class FilePair implements NameValuePair{

	private name;
	private value;
	
	public Dictionary(String name,File value){
		
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
*/