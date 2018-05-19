package com.nexus.locum.locumnexus;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.adapters.AddPreferedPracticesAdapter;
import com.nexus.locum.locumnexus.modelPOJO.PracticesModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;

public class AddPreferedPractices extends AppCompatActivity {


    @BindView(R.id.recycler_view_addpreferpractices)
    RecyclerView recycler_view_addpreferpractices;

    private ArrayList<PracticesModel> mArrayList;
    private AddPreferedPracticesAdapter mAdapter;

    SearchView search_practice;
    SharedPreferences CONST_SHAREDPREFERENCES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prefered_practices);
        ButterKnife.bind(this);

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_view_BookTable.addItemDecoration(new VerticalSpaceItemDecoration(48));
        recycler_view_addpreferpractices.hasFixedSize();
        recycler_view_addpreferpractices.setItemAnimator(new DefaultItemAnimator());
        recycler_view_addpreferpractices.setLayoutManager(llm);

        search_practice= (SearchView)this.findViewById(R.id.search_practice);


        mArrayList = new ArrayList<>();
        mAdapter = new AddPreferedPracticesAdapter(mArrayList,getBaseContext());
        recycler_view_addpreferpractices.setAdapter(mAdapter);

        new GetPractices().execute();

        search_practice.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    static String resPractices;
    ProgressDialog progressDialog;
    public class GetPractices extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(AddPreferedPractices.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                final OkHttpClient client = new OkHttpClient();

                final Request request = new Request.Builder()
                        .url(Const.SERVER_URL_API+"practice-lists")
                        .get()
                        //.addHeader("Content-Type", "application/json")
                        .addHeader("Authorization","Bearer "+ CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,""))
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
                                resPractices=response.body().string();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resPractices;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            progressDialog.dismiss();

            try{
                Log.i("RES resPractices---", ""+resPractices);


                JsonParser parser = new JsonParser();
                JsonArray rootObjArray = parser.parse(resPractices).getAsJsonArray();


                if(rootObjArray.size()>0) {
                    for (int a = 0; a < rootObjArray.size(); a++) {

                        String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                        String practice_code = rootObjArray.get(a).getAsJsonObject().get("practice_code").getAsString();
                        String practice_name = rootObjArray.get(a).getAsJsonObject().get("practice_name").getAsString();

                        String interim_ccg_code = rootObjArray.get(a).getAsJsonObject().has("interim_ccg_code")
                                ? rootObjArray.get(a).getAsJsonObject().get("interim_ccg_code").getAsString() : "";
                        String proposed_ccg_name = rootObjArray.get(a).getAsJsonObject().has("proposed_ccg_name")
                                ? rootObjArray.get(a).getAsJsonObject().get("proposed_ccg_name").getAsString() : "";
                        String practiceStatu = rootObjArray.get(a).getAsJsonObject().has("practiceStatu")
                                ? rootObjArray.get(a).getAsJsonObject().get("practiceStatu").getAsString() : "";

                        String __v = rootObjArray.get(a).getAsJsonObject().has("__v")
                                ? rootObjArray.get(a).getAsJsonObject().get("__v").getAsString() : "";

                        mArrayList.add(new PracticesModel(false, practice_name, practice_code, _id, "",
                                "", "",rootObjArray.get(a).getAsJsonObject()));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Collections.sort(mArrayList, new Comparator<PracticesModel>() {

                @Override
                public int compare(PracticesModel lhs, PracticesModel rhs) {
                    // TODO Auto-generated method stub

                    return (lhs.getPractice_name().toLowerCase().compareTo(rhs.getPractice_name().toLowerCase()));
                }
            });

        }
    }

}
