package com.example.storage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
	private static final int RC_STORAGE_PERMS1 = 101;
	private static final int RC_STORAGE_PERMS2 = 102;
	private int hasWriteExtStorePMS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_upload).setOnClickListener(this);
		findViewById(R.id.btn_download).setOnClickListener(this);
		findViewById(R.id.btn_cloud_storage).setOnClickListener(this);
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case RC_STORAGE_PERMS1:
			case RC_STORAGE_PERMS2:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (requestCode == RC_STORAGE_PERMS1) {
						startActivity(new Intent(this, UploadActivity.class));
					} else {
						startActivity(new Intent(this, DownloadActivity.class));
					}
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setMessage("You need to allow permission");
					alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intent.setData(Uri.parse("package:" + getPackageName()));
							startActivityForResult(intent, requestCode);
						}
					});
					alert.setCancelable(false);
					alert.show();
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case RC_STORAGE_PERMS1:
			case RC_STORAGE_PERMS2:
				hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
					if (requestCode == RC_STORAGE_PERMS1) {
						startActivity(new Intent(this, UploadActivity.class));
					} else {
						startActivity(new Intent(this, DownloadActivity.class));
					}
				} else {
					ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS2);
				}
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_upload:
				hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
					startActivity(new Intent(this, UploadActivity.class));
				} else {
					ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS1);
				}
				break;
			case R.id.btn_download:
				hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (hasWriteExtStorePMS == PackageManager.PERMISSION_GRANTED) {
					startActivity(new Intent(this, DownloadActivity.class));
				} else {
					ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS2);
				}
				break;
			case R.id.btn_cloud_storage:
				startActivity(new Intent(this, CloudStorageActivity.class));
				break;
		}
	}
}