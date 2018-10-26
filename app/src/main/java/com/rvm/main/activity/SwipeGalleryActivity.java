package com.rvm.main.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rvm.main.ImageModel;
import com.rvm.main.R;
import com.rvm.main.util.ScaleImageView;
import com.rvm.main.util.TouchImageView;

import java.util.ArrayList;

public class SwipeGalleryActivity extends AppCompatActivity {
    private String mFolderName="";
    private int mSelectedPosition=0;
    public static ArrayList<ImageModel> al_images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_swipe_gallery);
        if(getIntent().getIntExtra("SELECTED_POSITION",0)!=0){
            mSelectedPosition=getIntent().getIntExtra("SELECTED_POSITION",0);
        }
        if(getIntent().getStringExtra("FOLDER_NAME")!=null){
            mFolderName=getIntent().getStringExtra("FOLDER_NAME").toLowerCase();
            Log.v("mFolderName--->",mFolderName);
            getFilePath();
        }

    }

    public ArrayList<ImageModel> getFilePath(){
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String[] selectionArgs=new String[]{"%"+mFolderName+"%"};
        String selection=MediaStore.Images.Media.DATA +" like?";
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                   // boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                   // boolean_folder = false;
                }
            }
            ArrayList<String> al_path = new ArrayList<>();
            al_path.add(absolutePathOfImage);
            ImageModel obj_model = new ImageModel();
            obj_model.setStr_folder(cursor.getString(column_index_folder_name));
            obj_model.setAl_imagepath(al_path);
            al_images.add(obj_model);


        }
        if(al_images!=null&&al_images.size()>0){
            ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
            ImageAdapter adapter = new ImageAdapter(this,al_images);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(mSelectedPosition);
        }


       /* mRecyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),al_images);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);*/
        return al_images;
    }

    public class ImageAdapter extends PagerAdapter {
        Context context;
        ArrayList<ImageModel> mAllimages;
        private LayoutInflater inflater;
        ImageAdapter(Context context,ArrayList<ImageModel> al_images){
            this.context=context;
            this.mAllimages=al_images;
        }
        @Override
        public int getCount() {
            return mAllimages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ScaleImageView imgDisplay;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.row_display, container,
                    false);
            imgDisplay = (ScaleImageView) viewLayout.findViewById(R.id.imgDisplay);
            ImageModel imageModel=mAllimages.get(position);
            imgDisplay.setPadding(5,5,5,5);
            imgDisplay.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(context).load("file://" + imageModel.getAl_imagepath().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .into(imgDisplay);

            ((ViewPager) container).addView(viewLayout);
            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }
}
