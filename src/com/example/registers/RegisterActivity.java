package com.example.registers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.string;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	static String nameSpace = "http://tempuri.org/";
	static String endPoint = "http://192.168.1.104/Account.asmx";
	File sdcardTempFile = new File("/mnt/sdcard/", "tmp_pic_"
			+ SystemClock.currentThreadTimeMillis() + ".jpg");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		setContentView(R.layout.activity_register);

		Button sureButton = (Button) this
				.findViewById(R.id.button_Register_Sure);
		sureButton.setOnClickListener(new SureClickListener());

		Button cancelButton = (Button) this
				.findViewById(R.id.button_Register_Cancel);
		cancelButton.setOnClickListener(new CancelClickListener());

		Button photoButton = (Button) this
				.findViewById(R.id.register_button_setphoto);

		ImageView photo = (ImageView) this
				.findViewById(R.id.register_image_photo);
		photo.setDrawingCacheEnabled(true);

		photoButton.setOnClickListener(new PhotoClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

	public class SureClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {

				String methodName = "Register";
				SoapObject rpc = new SoapObject(nameSpace, methodName);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				rpc.addProperty("email", ((EditText) RegisterActivity.this
						.findViewById(R.id.text_register_email)).getText()
						.toString());
				rpc.addProperty("nickname", ((EditText) RegisterActivity.this
						.findViewById(R.id.text_register_nickname)).getText()
						.toString());
				rpc.addProperty("password", ((EditText) RegisterActivity.this
						.findViewById(R.id.text_register_password)).getText()
						.toString());

				envelope.encodingStyle = "UTF-8";
				envelope.dotNet = true;
				envelope.bodyOut = rpc;
				envelope.setOutputSoapObject(rpc);
				HttpTransportSE transport = new HttpTransportSE(endPoint);
				transport.call(nameSpace + methodName, envelope);
				Object id = envelope.getResponse();
				String userId = id.toString();

				Bitmap image = RegisterActivity.this.findViewById(
						R.id.register_image_photo).getDrawingCache();
				rpc.addProperty("photo", image.getNinePatchChunk());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("userid", userId);
				Map<String, File> files = new HashMap<String, File>();
				files.put(RegisterActivity.this.sdcardTempFile.getAbsolutePath(), RegisterActivity.this.sdcardTempFile);
				Http.Post("http://192.168.1.104/UploadPhoto.aspx", parameter, stream.toByteArray(),"file");
				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG)
						.show();
					RegisterActivity.this.finish();

			} catch (Exception e) {
				System.out.println(e.toString());
				Toast.makeText(RegisterActivity.this, e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class CancelClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			RegisterActivity.this.finish();
		}
	}

	public class PhotoClickListener implements OnClickListener {
		public void onClick(View v) {
			AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
					.setItems(new String[] { "相机", "相册" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (which == 0) {
										Intent intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										intent.putExtra("output",
												Uri.fromFile(sdcardTempFile));
										startActivityForResult(intent, 100);

									} else {
										Intent intent = new Intent(
												Intent.ACTION_GET_CONTENT);
										intent.setDataAndType(
												MediaStore.Images.Media.INTERNAL_CONTENT_URI,
												"image/*");
										intent.putExtra("output",
												Uri.fromFile(sdcardTempFile));
										intent.putExtra("return-data", true);
										intent.putExtra("crop", "true");
										intent.putExtra("output",Uri.fromFile(sdcardTempFile));
										startActivityForResult(intent, 101);
									}
								}
							}).create();

			dialog.show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Toast.makeText(this, sdcardTempFile.toString(), Toast.LENGTH_LONG).show();
		Toast.makeText(this, sdcardTempFile.canRead()+"--", Toast.LENGTH_LONG).show();
		
		
		if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inSampleSize = 8;
				if (sdcardTempFile.exists()) {
					Bitmap cameraBitmap = BitmapFactory.decodeFile(
							sdcardTempFile.getPath(), bitmapOptions);
					((ImageView) this.findViewById(R.id.register_image_photo))
							.setImageBitmap(cameraBitmap);
				}
			}
		} else if (requestCode == 101) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap photo = data.getParcelableExtra("data");
				((ImageView) this.findViewById(R.id.register_image_photo))
						.setImageBitmap(photo);
			}
		}
	}
}