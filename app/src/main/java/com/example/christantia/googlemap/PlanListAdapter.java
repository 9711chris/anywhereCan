package com.example.christantia.googlemap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.v4.widget.ExploreByTouchHelper.INVALID_ID;

/**
 * Created by shelinalusandro on 9/4/17.
 */

public class PlanListAdapter extends ArrayAdapter<DestinationItem>{

    Context context;
    int itemTemplateLayout;
    int destinationId;
    ArrayList<DestinationItem> data = null;
    PlanItem holder;
    final int INVALID_ID = -1;
    HashMap<DestinationItem, Integer> mIdMap = new HashMap<DestinationItem, Integer>();

    public PlanListAdapter(Context context, int itemTemplateLayout, int destinationId,
                        ArrayList<DestinationItem> data) {
        super(context, itemTemplateLayout, destinationId, data);
        this.itemTemplateLayout = itemTemplateLayout;
        this.destinationId = destinationId;
        this.context = context;
        this.data = data;
        for (int i = 0; i < data.size(); ++i) {
            mIdMap.put(data.get(i), i);
        }
    }

    public void removemIdMap(DestinationItem item)
    {
        mIdMap.remove(item);
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public DestinationItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        DestinationItem item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(itemTemplateLayout, parent, false);

            holder = new PlanItem();
            holder.name = (TextView) row.findViewById(R.id.destination_name);
            holder.check = (CheckBox) row.findViewById(R.id.checkbox1);
            holder.button = (ImageView) row.findViewById(R.id.toNavigation);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast toast = Toast.makeText(getContext(), "You clicked it at" + position + "!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            row.setTag(holder);

        } else {
            holder = (PlanItem) row.getTag();
        }

        holder.button.setTag(holder);
        String name1 = data.get(position).getDestinationName();
        holder.name.setText(name1);

        holder.check.setOnCheckedChangeListener(null);
        holder.check.setChecked(MapsActivity.ids.contains(position));

        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MapsActivity.ids.add(data.get(position));
                    System.out.println("IDS A: " + data.toString());
                } else {
                    if (MapsActivity.ids.contains(data.get(position))) {
                        //int i = ids.indexOf(position);
                        MapsActivity.ids.remove(data.get(position));
                        System.out.println("IDS R: "+ data.toString());
                            }
                        }

                    }
            });

        return row;
    }

    static class PlanItem {

        TextView name;
        CheckBox check;
        ImageView button;
    }
}
