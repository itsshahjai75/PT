package com.clickeat.restaurant.click_eatrestaurant.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloadAndSave extends AsyncTask<Void, Void, Void> {

	ProgressDialog p;
	String url, imageName;

	Context context;
	
	Handler handler;
	
	String filePath;

	public ImageDownloadAndSave(Context context, String url, String imageName, Handler handler) {
		this.url = url;
		this.imageName = imageName;
		this.context = context;
		this.handler = handler;
	}

	private String downloadImagesToSdCard(String downloadUrl, String imageName) {
		try {
			URL url = new URL(downloadUrl);
			// / making a directory in sdcard /
//			String sdCard = Environment.getExternalStorageDirectory()
//					.toString();
			
			String sdCard=    Environment
		      .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
			
			File myDir = new File(sdCard, "HUT HUT");

			// / if specified not exist create new /
			if (!myDir.exists()) {
				myDir.mkdir();
				//BoxCoreLog.v("", "inside mkdir");
			}

			// / checks the file and if it already exist delete /
			String fname = imageName;
			File file = new File(myDir, fname);
			if (file.exists())
				file.delete();

			// / Open a connection /
			URLConnection ucon = url.openConnection();
			InputStream inputStream = null;
			HttpURLConnection httpConn = (HttpURLConnection) ucon;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}

			FileOutputStream fos = new FileOutputStream(file);
//			int totalSize = httpConn.getContentLength();
			int downloadedSize = 0;
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fos.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;

			}

			fos.close();
			
			return file.getPath();

		} catch (IOException io) {
			io.printStackTrace();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}

	@Override
	protected void onPreExecute() {

		if (ImageDownloadAndSave.this.isCancelled()) {
			return;
		}
		try {

			p = ProgressDialog.show(context, "Downloading Picture", "preparing to share....", true);

			p.setCancelable(false);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	protected Void doInBackground(Void... params) {

		try {

		 filePath =	downloadImagesToSdCard(this.url, "HUT HUT.png");
			return null;
		} catch (NullPointerException e) {

			if (p != null)
				p.dismiss();
			e.printStackTrace();

		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

		try {
			if (p != null) {
				p.dismiss();
				
				Message message = Message.obtain();
				
				if(filePath!=null&&filePath.equals("")==false)
				{
					
					Bundle data = new Bundle();
					data.putString("path", filePath);
					message.setData(data);

					handler.sendMessage(message);
				}
				
				else
				{
					Bundle data = new Bundle();
					data.putString("path", "error");
					message.setData(data);

					handler.sendMessage(message);
				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
