package com.example.storage;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storage.util.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{
	private ImageView mImageView;
	private StorageReference imageRef;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		bindWidget();
		FirebaseStorage storage = FirebaseStorage.getInstance();
		//FirebaseStorage storage = FirebaseStorage.getInstance("gs://jirawatee-eastern");
		//FirebaseStorage storage = FirebaseStorage.getInstance("gs://jirawatee-northeastern");
		//FirebaseStorage storage = FirebaseStorage.getInstance("gs://jirawatee-europe");
		//FirebaseStorage storage = FirebaseStorage.getInstance("gs://jirawatee-easternus");
		//FirebaseStorage storage = FirebaseStorage.getInstance("gs://jirawatee-europeunion");

		StorageReference storageRef = storage.getReference();
		imageRef = storageRef.child("photos/IMG_20170129_151813.jpg");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_download_in_memory:
				downloadInMemory();
				break;
			case R.id.button_download_in_file:
				downloadInLocalFile();
				break;
			case R.id.button_download_via_url:
				downloadDataViaUrl();
				break;
			case R.id.button_get_metadata:
				getMetadata();
				break;
			case R.id.button_update_metadata:
				updateMetaData();
				break;
			case R.id.button_delete_file:
				deleteFile();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Helper.dismissProgressDialog();
		Helper.dismissDialog();
	}

	private void bindWidget() {
		mImageView = findViewById(R.id.imageview);
		mTextView = findViewById(R.id.textview);
		findViewById(R.id.button_download_in_memory).setOnClickListener(this);
		findViewById(R.id.button_download_in_file).setOnClickListener(this);
		findViewById(R.id.button_download_via_url).setOnClickListener(this);
		findViewById(R.id.button_get_metadata).setOnClickListener(this);
		findViewById(R.id.button_update_metadata).setOnClickListener(this);
		findViewById(R.id.button_delete_file).setOnClickListener(this);
	}

	private void downloadInMemory() {
		//long ONE_MEGABYTE = 1024 * 1024;
		Helper.showDialog(this);
		imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] bytes) {
				Helper.dismissDialog();
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				mImageView.setImageBitmap(bitmap);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		});
	}

	private void downloadInLocalFile() {
		File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
		final File file = new File(dir, UUID.randomUUID().toString() + ".png");
		try {
			if (!dir.exists()) {
				dir.mkdir();
			}
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final FileDownloadTask fileDownloadTask = imageRef.getFile(file);
		Helper.initProgressDialog(this);
		Helper.mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				fileDownloadTask.cancel();
			}
		});
		Helper.mProgressDialog.show();

		fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
				Helper.dismissProgressDialog();
				mTextView.setText(file.getPath());
				mImageView.setImageURI(Uri.fromFile(file));
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissProgressDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		}).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
				int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
				Helper.setProgress(progress);
			}
		});
	}

	private void downloadDataViaUrl() {
		Helper.showDialog(this);
		imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
			@Override
			public void onSuccess(Uri uri) {
				Helper.dismissDialog();
				mTextView.setText(uri.toString());
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		});
	}

	private void getMetadata() {
		Helper.showDialog(this);
		imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
			@Override
			public void onSuccess(StorageMetadata storageMetadata) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Country: %s", storageMetadata.getCustomMetadata("country")));
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		});
	}

	private void updateMetaData() {
		Helper.showDialog(this);
		StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("country", "Thailand").build();
		imageRef.updateMetadata(metadata).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
			@Override
			public void onSuccess(StorageMetadata storageMetadata) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Country: %s", storageMetadata.getCustomMetadata("country")));
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		});
	}

	private void deleteFile() {
		Helper.showDialog(this);
		imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Helper.dismissDialog();
				mImageView.setImageDrawable(null);
				mTextView.setText(String.format("%s was deleted.", imageRef.getPath()));
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Helper.dismissDialog();
				mTextView.setText(String.format("Failure: %s", exception.getMessage()));
			}
		});
	}
}