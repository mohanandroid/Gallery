package com.rvm.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rvm.main.activity.GalleryDisplayActivity;
import com.rvm.main.activity.SwipeGalleryActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<ImageModel> mImageModelList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        private ImageView imageViewDisplay;
        private FrameLayout frameLayout_container;

        public MyViewHolder(View view) {
            super(view);
            imageViewDisplay=(ImageView)view.findViewById(R.id.iv_image);
            frameLayout_container=(FrameLayout)view.findViewById(R.id.frameLayout_container);
        }
    }


    public RecyclerViewAdapter(Context context,List<ImageModel> imageModelList) {
        this.mImageModelList = imageModelList;
        this.mContext=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_selected_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageModel imageModel=mImageModelList.get(position);
        Glide.with(mContext).load("file://" + imageModel.getAl_imagepath().get(0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .into(holder.imageViewDisplay);
        holder.frameLayout_container.setTag(imageModel.getStr_folder());

        holder.frameLayout_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName=v.getTag().toString();
                Intent intentGalleryDisplay=new Intent(mContext, SwipeGalleryActivity.class);
                intentGalleryDisplay.putExtra("FOLDER_NAME",folderName);
                intentGalleryDisplay.putExtra("SELECTED_POSITION",position);
                mContext.startActivity(intentGalleryDisplay);
               // Toast.makeText(mContext,"Clicked : "+"position-->"+position+"----"+mImageModelList.get(position).getAl_imagepath() ,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageModelList.size();
    }
}
