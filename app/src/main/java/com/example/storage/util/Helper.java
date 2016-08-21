package com.example.storage.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.storage.R;

public class Helper {
	private static Dialog mDialog;
	public static ProgressDialog mProgressDialog;

	public static String getPath(Context context, Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		cursor.close();
		return path;
	}

	public static void showDialog(Context context) {
		mDialog = new Dialog(context, R.style.NewDialog);
		mDialog.addContentView(
				new ProgressBar(context),
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
		);
		mDialog.setCancelable(true);
		mDialog.show();
	}

	public static void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	public static void initProgressDialog(Context context) {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(context.getString(R.string.loading));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	public static void setProgress(int i) {
		mProgressDialog.setProgress(i);
	}

	public static void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
}