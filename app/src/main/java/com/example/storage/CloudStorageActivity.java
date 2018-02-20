package com.example.storage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.storage.util.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CloudStorageActivity extends AppCompatActivity implements View.OnClickListener{
	private StorageReference storageRef, imageRef;
	private static final int RC_UPLOAD_FILE = 102;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_storage);
		mImageView = findViewById(R.id.imv);
		findViewById(R.id.btn_upload).setOnClickListener(this);

		// Get instance and specify regional
		FirebaseStorage storage = FirebaseStorage.getInstance();

		// Get reference
		storageRef = storage.getReference();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_upload:
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, RC_UPLOAD_FILE);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String path = Helper.getPath(this, data.getData());
			switch (requestCode) {
				case RC_UPLOAD_FILE:
					uploadFromFile(path);
					break;
			}
		}
	}

	private void uploadFromFile(String path) {
		Uri file = Uri.fromFile(new File(path));

		// Get image reference from file
		imageRef = storageRef.child(file.getLastPathSegment());

		// Create Upload Task
		UploadTask uploadTask = imageRef.putFile(file);

		Helper.initProgressDialog(this);
		Helper.mProgressDialog.show();

		uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				Helper.dismissProgressDialog();
				imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
					@Override
					public void onSuccess(Uri uri) {
						Glide.with(CloudStorageActivity.this).load(uri.toString()).thumbnail(0.1f).into(mImageView);
					}
				});
			}
		}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
				int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
				Helper.setProgress(progress);
			}
		});
		// Add upload listenter
	}
}