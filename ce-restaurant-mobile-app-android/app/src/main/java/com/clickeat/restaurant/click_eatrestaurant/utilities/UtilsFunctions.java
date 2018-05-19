package com.clickeat.restaurant.click_eatrestaurant.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilsFunctions {

	public static String profilePicURL;
	private static final String USER_NAME_PATTERN = "^[_A-Za-z0-9-\\+]+$";

	public static boolean eMailValidation(String emailstring) {
		Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}

	public static boolean userNameValidation(String username) {

		Pattern emailPattern = Pattern.compile(USER_NAME_PATTERN);
		Matcher emailMatcher = emailPattern.matcher(username);
		return emailMatcher.matches();
	}

	/**
	 * Check Connectivity of network.
	 */
	public static boolean isOnline(Context context) {
		try {
			if (context == null)
				return false;

			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				if (cm.getActiveNetworkInfo() != null) {
					return cm.getActiveNetworkInfo().isConnected();
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("Exception", e + "");
			return false;
		}

	}

	public static Typeface getFont(Context context, int tag) {
		if (tag == 100) {
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Regular.ttf");
		}else if (tag == 200) {
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/RobotoCondensed-Light.ttf"/*Proxima Nova Condensed Black.otf*/);
		}else if (tag == 300) {
			return Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Light.ttf");
		}

		return Typeface.DEFAULT;
	}

	public static boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	// public static boolean isTablet(Context context) {
	// return (context.getResources().getConfiguration().screenLayout &
	// Configuration.SCREENLAYOUT_SIZE_MASK) >=
	// Configuration.SCREENLAYOUT_SIZE_LARGE;
	// }

	/*public static Bitmap downloadPic(Context context, String picURL,
			String pic, boolean isDowload) {

		Bitmap bitMap = null;
		HttpGet httpRequest = null;
		HttpClient httpclient = null;
		HttpResponse response1 = null;
		HttpEntity entity = null;
		BufferedHttpEntity bufHttpEntity = null;
		// FileOutputStream fo;
		File f;
		try {

			System.out.println(":: picURL : " + picURL + " :: pic: " + pic);
			if (isDowload)
				httpRequest = new HttpGet(URI.create(picURL + "/" + pic));
			else {
				System.out.println(":::::::::else::::false");
				httpRequest = new HttpGet(URI.create(picURL));
			}

			httpclient = new DefaultHttpClient();
			response1 = (HttpResponse) httpclient.execute(httpRequest);
			entity = response1.getEntity();
			bufHttpEntity = new BufferedHttpEntity(entity);
			bitMap = BitmapFactory.decodeStream(bufHttpEntity.getContent());
			httpRequest.abort();

			if (!isDowload)
				bitMap = getRoundedShape(bitMap);

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.PNG, 50, bytes);
			// you can create a new file name "test.jpg" in sdcard folder.

			f = new File(Const.SD_CARD_PATH);

			if (!f.exists())
				f.mkdir();

			byte[] data = bytes.toByteArray();
			// bitMap = resize(pic, data, context);

			// f = null;
			// f = new File(Const.SD_CARD_PATH + "/" + pic);
			// f.createNewFile();
			// fo = new FileOutputStream(f);
			// fo.write(bytes.toByteArray());

			return bitMap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bitMap = null;
			httpRequest = null;
			httpclient = null;
			response1 = null;
			entity = null;
			bufHttpEntity = null;
			// fo = null;
			f = null;
		}
		return null;
	}*/



	public static void createDirectoryAndSaveFile(Bitmap imageToSave,
			String fileName) {

		File file = new File(fileName);

		try {
			FileOutputStream out = new FileOutputStream(file);
			imageToSave.compress(Bitmap.CompressFormat.PNG, 50, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downloadFile(String src, String dest) {
		try {
			int count = 0;
			URL url = new URL(src);
			URLConnection conexion = url.openConnection();
			conexion.connect();

			int lenghtOfFile = conexion.getContentLength();

			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(dest); // save file in SD
																// Card

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				// publishProgress(""+(int)((total*100)/lenghtOfFile));
				System.out.println(":: progress"
						+ ((int) ((total * 100) / lenghtOfFile)));
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copy(String src, String dest) {

		FileInputStream in = null;
		FileOutputStream out = null;
		byte[] buf = null;
		int len;

		try {
			System.out.println(":: src ::" + src);
			System.out.println(":: dest ::" + dest);
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);

			buf = new byte[1024];

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			in = null;
			out = null;
			buf = null;
			len = 0;
			System.gc();
		}

	}


	public static String getDate(long time) {
		String mySelectedDate = "";
		  Calendar cal = Calendar.getInstance();
	       TimeZone tz = cal.getTimeZone();//get your local time zone.
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       sdf.setTimeZone(tz);//set time zone.
	       String localTime = sdf.format(time * 1000);
	       Date date = new Date();
	       try {
	            date = sdf.parse(localTime);//get local date
	            
	        	 mySelectedDate = sdf.format(date);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
//	       System.out.println("::: mySelectedDate :"+ mySelectedDate);
	       
	    return mySelectedDate;
	}

	public static String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	/**
	 * Convert Current Date to String Format Function
	 */
	public static String convertDateToString(Date objDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat, Locale.US).format(objDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Convert Current String to Date Function
	 */
	public static Date convertStringToDate(String strDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat, Locale.getDefault()).parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Convert Current String to String formate with apply new date formate
	 * Function
	 */
	public static String convertDateStringToString(String strDate,
			String currentFormat, String parseFormat) {
		try {
			return convertDateToString(
					convertStringToDate(strDate, currentFormat), parseFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}




}
