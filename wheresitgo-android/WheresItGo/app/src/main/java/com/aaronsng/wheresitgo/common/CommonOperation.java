/* Copyright (C) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Source code Written by Eric Lee Zheng Xian <eric@Lzxe.net>, Written by YngTzer Tan <tanyngtzer@gmail.com>
 * Designed by Lai Sen Hui <mrlai999@gmail.com>
 * December 2013
 */
package com.aaronsng.wheresitgo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonOperation {
	public String sha1Hash(String toHash) {
		String hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("UTF-8");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02X", b));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash;
	}

	public String retriveImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
		// return "354745050077454";
	}


	public static String getStringImage(String filePath){
		Log.e(Config.log_id,"file path "+filePath);
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}
	public boolean isProviderEnabled(Context context) {

		try {
			LocationManager lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				return true;
			}

		}

		catch (Exception e) {
			return true;
		}

		return false;
	}

	public void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	public void showSnackBar(CoordinatorLayout snackbarCoordinatorLayout, String text) {

		Snackbar snackbar = Snackbar.make(
				snackbarCoordinatorLayout,
				text,
				Snackbar.LENGTH_LONG);
		snackbar.show();
	}


}
