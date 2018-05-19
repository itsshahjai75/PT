package com.nexus.locum.locumnexus.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.FileChooser;
import com.nexus.locum.locumnexus.utilities.Const;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.POST_MULTIPART;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentsProfessionalProfile extends Fragment {


    ImageView img_upload_certificate,img_upload_resume,img_delete,img_delete_cv;

    private static final int REQUEST_PATH = 1;
    private static final int REQUEST_PATH_CV = 2;
    String curFileName,curFilePath;
    File sourceFile;

    //public DonutProgress donut_progress;
    private static final int REQUEST_WRITE_STORAGE = 112;

    private View m_myFragmentView;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myFragmentView = inflater.inflate(R.layout.fragment_documents_professional_profile, container, false);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        img_upload_certificate = (ImageView)m_myFragmentView.findViewById(R.id.img_upload_certificate);
        img_upload_resume = (ImageView)m_myFragmentView.findViewById(R.id.img_upload_resume);
        img_delete = (ImageView)m_myFragmentView.findViewById(R.id.img_delete);
        img_delete_cv = (ImageView)m_myFragmentView.findViewById(R.id.img_delete_cv);

        img_delete.setVisibility(View.GONE);
        img_delete_cv.setVisibility(View.GONE);



        Log.d("JSON Profile--",CONST_PROFILE_JSON.toString());
        JsonObject profileProeesionalDocs = CONST_PROFILE_JSON.get("professional").getAsJsonObject().get("documents").getAsJsonObject();

        if(profileProeesionalDocs.has("GMCReg")){
            String str_full_url_pathGMCReg = profileProeesionalDocs.get("GMCReg").getAsString();
            str_full_url_pathGMCReg=str_full_url_pathGMCReg.substring(1,str_full_url_pathGMCReg.length());

            String[] pathSplit = str_full_url_pathGMCReg.split("\\.");

            Log.d("splits screens",pathSplit[pathSplit.length-1].toString());

            String fileTypeFromUrl = pathSplit[pathSplit.length-1].toString();

            if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("pdf")) {
                img_upload_certificate.setImageResource(R.drawable.ic_pdf);
                img_delete.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("doc")) {
                img_upload_certificate.setImageResource(R.drawable.ic_doc);
                img_delete.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("docx")) {
                img_upload_certificate.setImageResource(R.drawable.ic_docx);
                img_delete.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("png") || fileTypeFromUrl.toLowerCase().equalsIgnoreCase("jpg")
                    || fileTypeFromUrl.toLowerCase().equalsIgnoreCase("jpeg")) {
                img_upload_certificate.setImageResource(R.drawable.ic_jpg);
                img_delete.setVisibility(View.VISIBLE);
            }

        }

        if(profileProeesionalDocs.has("GPCV")){

            String str_full_url_pathGPCV = profileProeesionalDocs.get("GPCV").getAsString();
            str_full_url_pathGPCV=str_full_url_pathGPCV.substring(1,str_full_url_pathGPCV.length());


            String[] pathSplit = str_full_url_pathGPCV.split("\\.");

            String fileTypeFromUrl = pathSplit[pathSplit.length-1].toString();

            if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("pdf")) {
                img_upload_resume.setImageResource(R.drawable.ic_pdf);
                img_delete_cv.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("doc")) {
                img_upload_resume.setImageResource(R.drawable.ic_doc);
                img_delete_cv.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("docx")) {
                img_upload_resume.setImageResource(R.drawable.ic_docx);
                img_delete_cv.setVisibility(View.VISIBLE);
            } else if (fileTypeFromUrl.toLowerCase().equalsIgnoreCase("png") || fileTypeFromUrl.toLowerCase().equalsIgnoreCase("jpg")
                    || fileTypeFromUrl.toLowerCase().equalsIgnoreCase("jpeg")) {
                img_upload_resume.setImageResource(R.drawable.ic_jpg);
                img_delete_cv.setVisibility(View.VISIBLE);
            }
        }



        Boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        img_upload_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(intent1,REQUEST_PATH);

            }
        });

        img_upload_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), FileChooser.class);
                startActivityForResult(intent1,REQUEST_PATH);

            }
        });


        return m_myFragmentView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(getContext(), "You must give access to storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PATH){
            if (resultCode == RESULT_OK) {
                curFileName = data.getStringExtra("GetFileName");
                curFilePath = data.getStringExtra("GetPath");

                sourceFile = new File(curFilePath+"/"+curFileName);

                //edittext.setText(curFileName);

                int file_size = Integer.parseInt(String.valueOf(sourceFile.length()/1024));

                if(file_size>2048){
                    Toast.makeText(getContext(), "Max file size is 2 MB. !", Toast.LENGTH_LONG).show();
                }else {

                    if (curFileName.contains(".pdf")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_pdf);
                        new FileUploadServer().execute("GMCReg");
                    } else if (curFileName.toLowerCase().contains(".doc")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_doc);
                        new FileUploadServer().execute("GMCReg");
                    } else if (curFileName.toLowerCase().contains(".docx")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_docx);
                        new FileUploadServer().execute("GMCReg");
                    } else if (curFileName.toLowerCase().contains(".png") || curFileName.toLowerCase().contains(".jpg")
                            || curFileName.toLowerCase().contains(".jpeg")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_jpg);
                        new FileUploadServer().execute("GMCReg");
                    } else {
                        //img_upload_certificate.setImageResource(R.drawable.ic_jpg);
                        Toast.makeText(getContext(), "File format is not supported !", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }

        if (requestCode == REQUEST_PATH_CV){
            if (resultCode == RESULT_OK) {
                curFileName = data.getStringExtra("GetFileName");
                curFilePath = data.getStringExtra("GetPath");

                sourceFile = new File(curFilePath+"/"+curFileName);
                //edittext.setText(curFileName);

                int file_size = Integer.parseInt(String.valueOf(sourceFile.length()/1024));

                if(file_size>2048){
                    Toast.makeText(getContext(), "Max file size is 2 MB. !", Toast.LENGTH_LONG).show();
                }else {

                    if (curFileName.contains(".pdf")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_pdf);
                        new FileUploadServer().execute("GPCV");
                    } else if (curFileName.toLowerCase().contains(".doc")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_doc);
                        new FileUploadServer().execute("GPCV");
                    } else if (curFileName.toLowerCase().contains(".docx")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_docx);
                        new FileUploadServer().execute("GPCV");
                    } else if (curFileName.toLowerCase().contains(".png") || curFileName.toLowerCase().contains(".jpg")
                            || curFileName.toLowerCase().contains(".jpeg")) {
                        img_upload_certificate.setImageResource(R.drawable.ic_jpg);
                        new FileUploadServer().execute("GPCV");
                    } else {
                        //img_upload_certificate.setImageResource(R.drawable.ic_jpg);
                        Toast.makeText(getContext(), "File format is not supported !", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }

    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    String resUPdatePractices;
    // ProgressDialog progressDialog;

    private class FileUploadServer extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                String responseUSerTitles = POST_MULTIPART(Const.SERVER_URL_API +"users/"+
                                CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                        +"/documents/"+parametros[0].toString(),curFilePath,sourceFile,
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,""));
                resUPdatePractices=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdatePractices;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.e("RES resUpdatepractices", resUPdatePractices);
                Toast.makeText(getContext(),"File Uploaded Successfully !"/*curFilePath+"/"+curFileName*/,Toast.LENGTH_LONG).show();
                new GetUserProfileDetails().execute();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

    /*static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                final OkHttpClient client = new OkHttpClient();

                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", Const.CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));

                MediaType JSON = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(JSON,getProfileDetails.toString());
                Log.e("request body profdet-",getProfileDetails.toString());


                final Request request = new Request.Builder()
                        .url(Const.SERVER_URL_API+"users/getProfile")
                        .post(body)
                        //.addHeader("Content-Type", "application/json")
                        .addHeader("Authorization","Bearer "+ Const.CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,""))
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        Log.w("failure Response", mMessage);
                        //call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response)
                            throws IOException {

                        Log.i("RES enque Code---", ""+response.code());
                        //
                        if (response.isSuccessful()){
                            try {
                                resUSerProfileDetails=response.body().string();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();


                ProfessionalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }
    }*/
    static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));



                String USerProfileDetails = ResponseWithAuthAPI(getContext(), CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/getProfile", getProfileDetails.toString(),"post");
                resUSerProfileDetails =USerProfileDetails;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();




            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            ProfessionalProfileActivity.viewPager.getAdapter().notifyDataSetChanged();
            //notifyDataSetChanged();

        }
    }


}
