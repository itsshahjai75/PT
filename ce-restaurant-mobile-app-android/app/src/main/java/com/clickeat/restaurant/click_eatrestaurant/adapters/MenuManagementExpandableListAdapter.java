package com.clickeat.restaurant.click_eatrestaurant.adapters;

/**
 * Created by android on 27/3/18.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.MenuMangmentExpandedMenuModel;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.clickeat.restaurant.click_eatrestaurant.MenuManagmentExpandable.data_id;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;

public class MenuManagementExpandableListAdapter extends BaseExpandableListAdapter {
private Context mContext;
private List<MenuMangmentExpandedMenuModel> mListDataHeader; // header titles
        SharedPreferences CONST_SHAREDPREFERENCES;


        // child data in format of header title, child title
private HashMap<MenuMangmentExpandedMenuModel, List<String>> mListDataChild;
        ExpandableListView expandList;

public MenuManagementExpandableListAdapter(Context context, List<MenuMangmentExpandedMenuModel> listDataHeader, HashMap<MenuMangmentExpandedMenuModel, List<String>> listChildData, ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList = mView;
        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

}

@Override
public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d("GROUPCOUNT", String.valueOf(i));
        return this.mListDataHeader.size();
        }

@Override
public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        if (groupPosition != 3) {
                childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }
        if (groupPosition != 4) {
                childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }
        return childCount;
        }

@Override
public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
        }

@Override
public Object getChild(int groupPosition, int childPosition) {
        Log.d("CHILD", mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition).toString());
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
        .get(childPosition);
        }

@Override
public long getGroupId(int groupPosition) {
        return groupPosition;
        }

@Override
public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
        }

@Override
public boolean hasStableIds() {
        return false;
        }

@SuppressLint("NewApi")
@Override
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        MenuMangmentExpandedMenuModel headerModel = (MenuMangmentExpandedMenuModel) getGroup(groupPosition);

        if (convertView == null) {
        LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.exp_listheader, null);
        }


        LabeledSwitch labeledSwitch = convertView.findViewById(R.id.switchheader);
        labeledSwitch.setLabelOn("");
        labeledSwitch.setLabelOff("");
        labeledSwitch.setColorOff(mContext.getColor(R.color.colorPrimary));
        labeledSwitch.setColorOn(mContext.getColor(R.color.md_grey_200));

        if(headerModel.getHeaderJson().has("CategoryStatus")){
                if(headerModel.getHeaderJson().get("CategoryStatus").getAsString().equalsIgnoreCase("available")){
                        labeledSwitch.setOn(true);
                }else{
                        labeledSwitch.setOn(false);
                }
        }else if(headerModel.getHeaderJson().has("menuMealStatus")){
                if (headerModel.getHeaderJson().get("menuMealStatus").getAsString().equalsIgnoreCase("available")){
                        labeledSwitch.setOn(true);
                }else{
                        labeledSwitch.setOn(false);
                }
        }


        labeledSwitch.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                        // Implement your switching logic here
                        if(isOn==true){
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                                } else {
                                        builder = new AlertDialog.Builder(mContext);
                                }
                                String edit="";
                                if(headerModel.getHeaderJson().has("drinkname")){

                                        edit=headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" drink" +
                                                "";
                                }else if(headerModel.getHeaderJson().has("dishname")){
                                        edit= headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" dish";
                                }else if(headerModel.getHeaderJson().has("meal")){
                                        edit=headerModel.getHeaderJson().get("meal").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" meal";
                                }

                                builder.setTitle("Modify Menu ?")
                                        .setMessage("Are you sure you want to available this "+edit+" category?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete


                                                        JsonObject reqJson = new JsonObject();

                                                        String category_id="";
                                                        if(headerModel.getHeaderJson().has("category")){
                                                                category_id = headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                                        .get("category_id").getAsString();

                                                                reqJson.addProperty("CategoryStatus","available");
                                                                reqJson.addProperty("category_id",category_id);



                                                                new UpdateMeunHeader().execute("editCategory",reqJson);

                                                        }else if(headerModel.getHeaderJson().has("meal")){


                                                                reqJson.addProperty("menuMealStatus","available");
                                                                reqJson.add("meal",
                                                                                headerModel.getHeaderJson().get("meal").getAsJsonObject());



                                                                new UpdateMeunHeader().execute("editMeal",reqJson);

                                                        }


                                                        dialog.dismiss();
                                                }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                       labeledSwitch.setOn(false);
                                                        dialog.dismiss();
                                                }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                        }else  if(isOn==false){
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                                } else {
                                        builder = new AlertDialog.Builder(mContext);
                                }
                                String edit="";
                                if(headerModel.getHeaderJson().has("drinkname")){

                                        edit=headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" dish";
                                }else if(headerModel.getHeaderJson().has("dishname")){
                                        edit= headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" dish";
                                }else if(headerModel.getHeaderJson().has("meal")){
                                        edit=headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                .get("en").getAsJsonObject().get("name").getAsString()
                                                +" meal";
                                }

                                builder.setTitle("Modify Menu ?")
                                        .setMessage("Are you sure you want to unavailable this "+edit+" category?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete


                                                        JsonObject reqJson = new JsonObject();

                                                        String category_id="";
                                                        if(headerModel.getHeaderJson().has("category")){
                                                                category_id = headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                                        .get("category_id").getAsString();

                                                                reqJson.addProperty("CategoryStatus","unavailable");
                                                                reqJson.addProperty("category_id",category_id);



                                                                new UpdateMeunHeader().execute("editCategory",reqJson);

                                                        }else if(headerModel.getHeaderJson().has("meal")){


                                                                reqJson.addProperty("menuMealStatus","unavailable");
                                                                reqJson.add("meal",
                                                                        headerModel.getHeaderJson().get("meal").getAsJsonObject().get("meal_id").getAsJsonObject());

                                                                new UpdateMeunHeader().execute("editMeal",reqJson);

                                                        }


                                                        dialog.dismiss();
                                                }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        labeledSwitch.setOn(true);
                                                        dialog.dismiss();
                                                }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                        }
                }
        });


        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        ImageView iconimagedropDown = (ImageView) convertView.findViewById(R.id.iconimagedropDown);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerModel.getIconName());
        //lblListHeader.setTextColor(mContext.getResources().getColor(R.color.colorPrimary,mContext.getTheme()));
        headerIcon.setImageResource(headerModel.getIconImg());

        /*if(groupPosition== 3 || groupPosition ==4){
                iconimagedropDown.setVisibility(View.GONE);
        }*/


        if(getChildrenCount(groupPosition)==0) {
                iconimagedropDown.setVisibility(View.GONE);
        }else {
                iconimagedropDown.setImageResource(R.drawable.ic_right_grey);
                lblListHeader.setTextColor(mContext.getResources().getColor(R.color.black, mContext.getTheme()));
                headerIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.md_grey_500));
        }

        if (isExpanded) {

                iconimagedropDown.setImageResource(R.drawable.ic_down_arrow_grey);

        }else {

                iconimagedropDown.setImageResource(R.drawable.ic_right_grey);

        }
        return convertView;
        }

@SuppressLint("NewApi")
@Override
public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
        LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.exp_list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.submenu);
        LabeledSwitch labeledSwitch = convertView.findViewById(R.id.switchchild);

        MenuMangmentExpandedMenuModel headerModel = (MenuMangmentExpandedMenuModel) getGroup(groupPosition);

        labeledSwitch.setLabelOn("");
        labeledSwitch.setLabelOff("");

        if(headerModel.getChiledJson().get("status").getAsString().equalsIgnoreCase("available")){
                labeledSwitch.setOn(true);
        }else if(headerModel.getChiledJson().get("status").getAsString().equalsIgnoreCase("unavailable")){
                labeledSwitch.setOn(false);
        }

        labeledSwitch.setColorOff(mContext.getColor(R.color.colorPrimary));
        labeledSwitch.setColorOn(mContext.getColor(R.color.md_grey_50));

        labeledSwitch.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                        // Implement your switching logic here
                        if(isOn==true){
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                                } else {
                                        builder = new AlertDialog.Builder(mContext);
                                }
                                String edit= headerModel.getChiledJson().get("en").getAsJsonObject().get("name").getAsString();

                                builder.setTitle("Modify Menu Item ?")
                                        .setMessage("Are you sure you want to unavailable this "+edit+" ?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete


                                                        JsonObject reqJson = new JsonObject();

                                                        String category_id="";
                                                        if(headerModel.getHeaderJson().has("category")){
                                                                category_id = headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                                        .get("category_id").getAsString();

                                                                reqJson.addProperty("parentIndex",groupPosition);
                                                                reqJson.addProperty("status","");
                                                                reqJson.addProperty("indexValue",childPosition);

                                                                headerModel.getChiledJson().addProperty("status","available");

                                                                reqJson.addProperty("CategoryStatus","available");

                                                                if(headerModel.getHeaderJson().has("dishname")){
                                                                        reqJson.add("dishname",headerModel.getChiledJson());
                                                                }else if(headerModel.getHeaderJson().has("drinkname")){
                                                                        reqJson.add("drinkname",headerModel.getChiledJson());

                                                                }

                                                                new UpdateSubMeunHeader().execute("editMenu",reqJson);
                                                                Log.e("menu Item JSON---",reqJson.toString());

                                                        }


                                                        dialog.dismiss();
                                                }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        labeledSwitch.setOn(true);
                                                        dialog.dismiss();
                                                }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                        }else if(isOn==false){
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                                } else {
                                        builder = new AlertDialog.Builder(mContext);
                                }
                                String edit= headerModel.getChiledJson().get("en").getAsJsonObject().get("name").getAsString();

                                builder.setTitle("Modify Menu Item ?")
                                        .setMessage("Are you sure you want to unavailable this "+edit+" ?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete


                                                        JsonObject reqJson = new JsonObject();

                                                        String category_id="";
                                                        if(headerModel.getHeaderJson().has("category")){
                                                                category_id = headerModel.getHeaderJson().get("category").getAsJsonObject()
                                                                        .get("category_id").getAsString();

                                                                reqJson.addProperty("parentIndex",groupPosition);
                                                                reqJson.addProperty("status","");
                                                                reqJson.addProperty("indexValue",childPosition);

                                                                headerModel.getChiledJson().addProperty("status","unavailable");

                                                                reqJson.addProperty("CategoryStatus","unavailable");

                                                                if(headerModel.getHeaderJson().has("dishname")){
                                                                        reqJson.add("dishname",headerModel.getChiledJson());
                                                                }else if(headerModel.getHeaderJson().has("drinkname")){
                                                                        reqJson.add("drinkname",headerModel.getChiledJson());

                                                                }

                                                                new UpdateSubMeunHeader().execute("editMenu",reqJson);
                                                                Log.e("menu Item JSON---",reqJson.toString());

                                                        }


                                                        dialog.dismiss();
                                                }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                        labeledSwitch.setOn(true);
                                                        dialog.dismiss();
                                                }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                        }
                }
        });

        txtListChild.setText(childText);

        /*if(childPosition==mListDataChild.size()){
                labeledSwitch.setVisibility(View.GONE);
                txtListChild.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark,mContext.getTheme()));
        }*/

        return convertView;
        }


        @Override

        public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
        }

        static String resUpdateMenuDetails;
        ProgressDialog progressDialog;
        public class UpdateMeunHeader extends AsyncTask<Object, Void, String> {

                @Override
                protected void onPreExecute()//execute thaya pela
                {
                        super.onPreExecute();
                        // Log.d("pre execute", "Executando onPreExecute ingredients");
                        //inicia diálogo de progress, mostranto processamento com servidor.
                        progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait...", true, false);

                }

                @Override
                protected String doInBackground(final Object... parametros) {

                        try {




                                String responseTableUnavialbe = ResponseAPIWithHeader(mContext, Const.SERVER_URL_API
                                                +"menu-managements/"+parametros[0].toString()+"/"+data_id+"/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,"")
                                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                                        , parametros[1].toString(),"post");
                                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                                resUpdateMenuDetails=responseTableUnavialbe;




                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        return resUpdateMenuDetails;
                }


                @SuppressLint("LongLogTag")
                @Override
                protected void onPostExecute(String result) {

                        String response_string = "";
                        // System.out.println("OnpostExecute----done-------");
                        super.onPostExecute(result);


                        try{
                                Log.i("RES resUpdateMenuDetails Body", ""+resUpdateMenuDetails);
                                JsonParser parser = new JsonParser();
                                // JsonArray rootObjArray = parser.parse(resUpdateBookingsDetails).getAsJsonArray();



                        }
                        catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        progressDialog.dismiss();
                }
        }

        static String resUpdateSubMenuDetails;
        public class UpdateSubMeunHeader extends AsyncTask<Object, Void, String> {

                @Override
                protected void onPreExecute()//execute thaya pela
                {
                        super.onPreExecute();
                        // Log.d("pre execute", "Executando onPreExecute ingredients");
                        //inicia diálogo de progress, mostranto processamento com servidor.
                        progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait...", true, false);

                }

                @Override
                protected String doInBackground(final Object... parametros) {

                        try {




                                String responseTableUnavialbe = ResponseAPIWithHeader(mContext, Const.SERVER_URL_API
                                                +"menu-managements/"+parametros[0].toString()+"/"+data_id+"/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,"")
                                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                                        , parametros[1].toString(),"post");
                                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                                resUpdateMenuDetails=responseTableUnavialbe;




                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        return resUpdateMenuDetails;
                }


                @SuppressLint("LongLogTag")
                @Override
                protected void onPostExecute(String result) {

                        String response_string = "";
                        // System.out.println("OnpostExecute----done-------");
                        super.onPostExecute(result);


                        try{
                                Log.i("RES resUpdateMenuDetails Body", ""+resUpdateMenuDetails);
                                JsonParser parser = new JsonParser();
                                // JsonArray rootObjArray = parser.parse(resUpdateBookingsDetails).getAsJsonArray();



                        }
                        catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        progressDialog.dismiss();
                }
        }

}
