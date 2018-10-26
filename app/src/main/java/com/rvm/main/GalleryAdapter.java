package com.rvm.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rvm.main.activity.GalleryDisplayActivity;

import java.util.ArrayList;

public class GalleryAdapter  extends ArrayAdapter<ImageModel> {
        Context context;
        ViewHolder viewHolder;
        ArrayList<ImageModel> al_menu = new ArrayList<>();


    public GalleryAdapter(Context context, ArrayList<ImageModel> al_menu) {
            super(context, R.layout.row_gallery, al_menu);
            this.al_menu = al_menu;
            this.context = context;
            }

    @Override
    public int getCount() {

            Log.e("ADAPTER LIST SIZE", al_menu.size() + "");
            return al_menu.size();
            }

    @Override
    public int getItemViewType(int position) {
            return position;
            }

    @Override
    public int getViewTypeCount() {
            if (al_menu.size() > 0) {
            return al_menu.size();
            } else {
            return 1;
            }
            }

@Override
public long getItemId(int position) {
        return position;
        }


@Override
public View getView(final int position, View convertView, ViewGroup parent) {
    ImageModel imageModel=al_menu.get(position);
        if (convertView == null) {
        viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_gallery, parent, false);
        viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder_name);
        viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
        viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
        viewHolder.frameLayout_container=(FrameLayout)convertView.findViewById(R.id.frameLayout_container);
        viewHolder.frameLayout_container.setTag(imageModel.getStr_folder());
        convertView.setTag(viewHolder);
        } else {
        viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setText(imageModel.getStr_folder());
        viewHolder.tv_foldersize.setText(imageModel.getAl_imagepath().size()+"");
        viewHolder.frameLayout_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName=v.getTag().toString();
                Intent intentGalleryDisplay=new Intent(context, GalleryDisplayActivity.class);
                intentGalleryDisplay.putExtra("FOLDER_NAME",folderName);
                context.startActivity(intentGalleryDisplay);

            }
        });


        Glide.with(context).load("file://" + al_menu.get(position).getAl_imagepath().get(0))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(true)
                .thumbnail(0.5f)
        .into(viewHolder.iv_image);
        return convertView;

        }

private static class ViewHolder {
    TextView tv_foldern, tv_foldersize;
    ImageView iv_image;
    FrameLayout frameLayout_container;


}


    }

