package com.med.fast;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.med.fast.customviews.CustomFontTextView;

import java.util.List;

/**
 * Created by KM on 9/5/2017. KSM
 */

public class MenuGridAdapter extends ArrayAdapter<MenuModel> {

    private Context context;
    private MenuOnclick intf;

    public interface MenuOnclick {
        void onMenuClick(int position, View clickedView, MenuModel menuModel);
    }

    public MenuGridAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<MenuModel> objects, MenuOnclick intf) {
        super(context, resource, objects);
        this.context = context;
        this.intf = intf;
    }

    @Nullable
    @Override
    public MenuModel getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.home_item, parent, false);
        }

        MenuModel menuModel = getItem(position);

        if (menuModel != null) {
            CustomFontTextView name = convertView.findViewById(R.id.home_item_name);
            name.setText(menuModel.getName());

            final ImageView icon = convertView.findViewById(R.id.home_item_icon);
            icon.setImageDrawable(ContextCompat.getDrawable(context, menuModel.getImgRes()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intf.onMenuClick(position, icon, getItem(position));
                }
            });
        }

        return convertView;
    }
}
