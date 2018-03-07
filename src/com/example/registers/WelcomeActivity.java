package com.example.registers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		Intent intent = this.getIntent();
		String email = intent.getStringExtra("email");
		String id = intent.getStringExtra("id");
		TextView text = (TextView) this.findViewById(R.id.welcome_text_title);
		text.append(email);

		// set the path where we want to save the file
		// in this case, going to save it on the root directory of the
		// sd card.
		File SDCardRoot = Environment.getExternalStorageDirectory();
		// create a new file, specifying the path, and the filename
		// which we want to save the file as.

		String urlStr = "http://192.168.1.104/GetPhoto.aspx?id=" + id;
		ImageView view = (ImageView) this
				.findViewById(R.id.welcome_image_photo);

		/*
		 * File file = new File(SDCardRoot, id + ".jpg"); downloadFile(urlStr,
		 * file); Bitmap photo =
		 * BitmapFactory.decodeFile(file.getAbsolutePath());
		 * view.setImageBitmap(photo);
		 */

		byte[] buffer = new byte[1024 * 1024];
		//Toast.makeText(this, "Before download", Toast.LENGTH_LONG).show();
		int length = downloadFile(urlStr, buffer);
		//Toast.makeText(this, "After download", Toast.LENGTH_LONG).show();
		Toast.makeText(this, length+"_____", Toast.LENGTH_LONG).show();
		Bitmap photo = BitmapFactory.decodeByteArray(buffer, 0,length);

		view.setImageBitmap(photo);
	}

	public void downloadFile(String url, File saveTo) {
		try {
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded
			URL uri = new URL(url);

			// create the new connection
			HttpURLConnection urlConnection = (HttpURLConnection) uri
					.openConnection();

			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// and connect!
			urlConnection.connect();

			// this will be used to write the downloaded data into the file we
			// created
			FileOutputStream fileOutput = new FileOutputStream(saveTo);

			// this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// variable to store total downloaded bytes
			int downloadedSize = 0;

			// create a buffer...
			byte[] buffer = new byte[1024 * 4];
			int bufferLength = 0; // used to store a temporary size of the
									// buffer

			// now, read through the input buffer and write the contents to the
			// file
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				// add the data in the buffer to the file in the file output
				// stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);

			}
			// close the output stream when done
			fileOutput.close();

			// catch some possible errors...
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int downloadFile(String url) {
		
		int allLength = 0;
		try {
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded
			URL uri = new URL(url);

			// create the new connection
			HttpURLConnection urlConnection = (HttpURLConnection) uri
					.openConnection();

			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// and connect!
			urlConnection.connect();

			// this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// now, read through the input buffer and write the contents to the
			// file

			int readedLength =inputStream.read(buffer);
			allLength += readedLength;
			
			while (readedLength > 0) {
				readedLength = inputStream.read(buffer);
				allLength += readedLength;
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	//	Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
		return allLength;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_welcome, menu);
		return true;
	}
}
