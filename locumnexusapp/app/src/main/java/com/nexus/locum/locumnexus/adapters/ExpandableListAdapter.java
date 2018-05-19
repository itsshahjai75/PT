package com.nexus.locum.locumnexus.adapters;

/**
 * Created by android on 27/3/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.modelPOJO.ExpandedMenuModel;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
private Context mContext;
private List<ExpandedMenuModel> mListDataHeader; // header titles

// child data in format of header title, child title
private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
        ExpandableListView expandList;

public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader,
                             HashMap<ExpandedMenuModel, List<String>> listChildData, ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList = mView;
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

        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);

        if (convertView == null) {
        LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.exp_listheader, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);

        LabeledSwitch switchheaderincludExclud = convertView.findViewById(R.id.switchheaderincludExclud);
        switchheaderincludExclud.setLabelOn("Add.");
        switchheaderincludExclud.setLabelOff("Ignore.");

        switchheaderincludExclud.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                        // Implement your switching logic here
                }
        });


        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        ImageView iconimagedropDown = (ImageView) convertView.findViewById(R.id.iconimagedropDown);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getIconName());
        //lblListHeader.setTextColor(mContext.getResources().getColor(R.color.colorPrimary,mContext.getTheme()));
        headerIcon.setImageResource(headerTitle.getIconImg());

        /*if(groupPosition== 3 || groupPosition ==4){
                iconimagedropDown.setVisibility(View.GONE);
        }*/

        iconimagedropDown.setImageResource(R.drawable.ic_right_grey);
        lblListHeader.setTextColor(mContext.getResources().getColor(R.color.md_grey_500, mContext.getTheme()));
        headerIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.md_grey_500));




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
        ImageView btn_delete = (ImageView) convertView.findViewById(R.id.btn_delete);

        LabeledSwitch labeledSwitch = convertView.findViewById(R.id.switchincludExclud);
        labeledSwitch.setLabelOn("  Inclu. ");
        labeledSwitch.setLabelOff("  Exclu. ");
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                        // Implement your switching logic here
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
        }