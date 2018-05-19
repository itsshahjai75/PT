package com.nexus.locum.locumnexus.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.AddPreferedPractices;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.adapters.AddPreferedPracticesAdapter;
import com.nexus.locum.locumnexus.adapters.NHSSystemProfileAdapter;
import com.nexus.locum.locumnexus.modelPOJO.NHSSystemModel;
import com.nexus.locum.locumnexus.modelPOJO.PracticesModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class NHSsystemProfessionFragment extends Fragment {

    @BindView(R.id.recycler_view_addpreferpractices)
    RecyclerView recycler_view_addpreferpractices;

    private ArrayList<NHSSystemModel> mArrayList;
    private NHSSystemProfileAdapter mAdapter;
    FloatingActionButton fab_add_NHSSystem;
    SharedPreferences CONST_SHAREDPREFERENCES;


    public NHSsystemProfessionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_nhssystem_profession, container, false);

        ButterKnife.bind( this, convertView);

        CONST_SHAREDPREFERENCES  = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_addpreferpractices.hasFixedSize();
        recycler_view_addpreferpractices.setItemAnimator(new DefaultItemAnimator());
        recycler_view_addpreferpractices.setLayoutManager(llm);



        mArrayList = new ArrayList<>();
        mAdapter = new NHSSystemProfileAdapter(mArrayList,getContext());
        recycler_view_addpreferpractices.setAdapter(mAdapter);

        new GetNHSSytemDefault().execute();

        fab_add_NHSSystem = (FloatingActionButton)convertView.findViewById(R.id.fab_add_NHSSystem);
        fab_add_NHSSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectQualificationDialog();
            }
        });

        return convertView;

    }

    ArrayList<CharSequence> selectedNHSSystem;
    String[] qualificationLable;

    protected void showSelectQualificationDialog() {

        selectedNHSSystem = new ArrayList<CharSequence>();


        if(mArrayList.size()>0) {
            //split the string using separator, in this case it is ","
            String[] selectedNHSArray = new String[mArrayList.size()];
            for(int j=0;j<mArrayList.size();j++){
                selectedNHSArray[j] = mArrayList.get(j).getLabel();
            }

		/*
		 * Use asList method of Arrays class to convert Java String array to ArrayList
		 */
            selectedNHSSystem = new ArrayList<CharSequence>(Arrays.asList(selectedNHSArray));
        }

        qualificationLable = multiSelectedOptions.toArray(new String[multiSelectedOptions.size()]);

        boolean[] checkedQualificaiton = new boolean[qualificationLable.length];

        int count = qualificationLable.length;

        for(int i = 0; i < count; i++) {
            checkedQualificaiton[i] = selectedNHSSystem.contains(qualificationLable[i]);
        }

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if(isChecked)

                    selectedNHSSystem.add(qualificationLable[which]);

                else

                    selectedNHSSystem.remove(qualificationLable[which]);

                onChangeSelectedColours();

            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("SELECT NHS SYSTEM .");
        builder.setMultiChoiceItems(qualificationLable, checkedQualificaiton, coloursDialogListener);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mArrayList.clear();
                for (int a = 0; a < NHSSystemTypeJSONArray.size(); a++) {

                    if(selectedNHSSystem.contains(NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("label").getAsString())) {

                        String _id = NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("_id").getAsString();
                        String label = NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("label").getAsString();
                        String id = NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("id").getAsString();

                        String createdBy = NHSSystemTypeJSONArray.get(a).getAsJsonObject().has("createdBy")
                                ? NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("createdBy").getAsString() : "";
                        String __v = NHSSystemTypeJSONArray.get(a).getAsJsonObject().has("__v")
                                ? NHSSystemTypeJSONArray.get(a).getAsJsonObject().get("__v").getAsString() : "";
                        NHSSystemTypeJSONArray.get(a).getAsJsonObject().addProperty("level","basic");


                        mArrayList.add(new NHSSystemModel(_id,label,id,createdBy,__v,NHSSystemTypeJSONArray.get(a).getAsJsonObject()));
                        mAdapter.notifyDataSetChanged();
                    }
                }
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    protected void onChangeSelectedColours() {

        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence qualifications : selectedNHSSystem)

            stringBuilder.append(qualifications + ",");

        // et_Qualification.setText(stringBuilder.toString());

    }

    final List<String> multiSelectedOptions = new ArrayList<String>();
    public static JsonArray NHSSystemTypeJSONArray = new JsonArray();

    static String resGetNHS;
    ProgressDialog progressDialog;
    public class GetNHSSytemDefault extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(getContext(), "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                String responseUSerNHSsystem = ResponseWithAuthAPI(getContext(),CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"ulnhsitsystemss", "","get");

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGetNHS=responseUSerNHSsystem;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGetNHS;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            progressDialog.dismiss();

            try{
                Log.i("RES resGetNHS---", ""+resGetNHS);


                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resGetNHS).getAsJsonArray();

                NHSSystemTypeJSONArray= rootObjArray;

                Log.d("JSON Profile--",CONST_PROFILE_JSON.toString());
                JsonArray profileProeesionalNHSArray = CONST_PROFILE_JSON.get("professional").getAsJsonObject().get("nhsSys").getAsJsonArray();


                multiSelectedOptions.clear();

                mArrayList.clear();

                if(rootObjArray.size()>0) {

                    for (int a = 0; a < rootObjArray.size(); a++) {

                        String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                        String label = rootObjArray.get(a).getAsJsonObject().get("label").getAsString();
                        String id = rootObjArray.get(a).getAsJsonObject().get("id").getAsString();

                        String createdBy = rootObjArray.get(a).getAsJsonObject().has("createdBy")
                                ? rootObjArray.get(a).getAsJsonObject().get("createdBy").getAsString() : "";
                        String __v = rootObjArray.get(a).getAsJsonObject().has("__v")
                                ? rootObjArray.get(a).getAsJsonObject().get("__v").getAsString() : "";

                        multiSelectedOptions.add(label);

                        if(profileProeesionalNHSArray.size()>0) {
                            for (int b = 0; b < profileProeesionalNHSArray.size(); b++) {

                                String lableFromProfileArray = profileProeesionalNHSArray.get(b).getAsJsonObject().get("label").getAsString();
                                if(lableFromProfileArray.equalsIgnoreCase(label)){

                                    mArrayList.add(new NHSSystemModel(_id,label,id,createdBy,__v,profileProeesionalNHSArray.get(b).getAsJsonObject()));
                                    //mAdapter.notifyDataSetChanged();

                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Collections.sort(mArrayList, new Comparator<NHSSystemModel>() {

                @Override
                public int compare(NHSSystemModel lhs, NHSSystemModel rhs) {
                    // TODO Auto-generated method stub

                    return (lhs.getLabel().toLowerCase().compareTo(rhs.getLabel().toLowerCase()));
                }
            });

        }
    }

}