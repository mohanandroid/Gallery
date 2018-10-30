package com.rvm.main.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.params.TonemapCurve;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rvm.main.ImageModel;
import com.rvm.main.R;
import com.rvm.main.util.ExtendedViewPager;
import com.rvm.main.util.ScaleImageView;
import com.rvm.main.util.TouchImageView;

import java.io.File;
import java.util.ArrayList;

public class SwipeGalleryActivity extends AppCompatActivity {
    private String mFolderName = "";
    private int mSelectedPosition = 0;
    public static ArrayList<ImageModel> al_images = new ArrayList<>();
    AdView mAdView;
    AlertDialog.Builder builder;
    ExtendedViewPager mViewPager;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_swipe_gallery);
        if (getIntent().getIntExtra("SELECTED_POSITION", 0) != 0) {
            mSelectedPosition = getIntent().getIntExtra("SELECTED_POSITION", 0);
        }
        if (getIntent().getStringExtra("FOLDER_NAME") != null) {
            mFolderName = getIntent().getStringExtra("FOLDER_NAME").toLowerCase();
            this.setTitle(mFolderName);
            Log.v("mFolderName--->", mFolderName);
            getFilePath();
        }
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (al_images != null & al_images.size() > 0) {
                    alertConfirmDialog();

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<ImageModel> getFilePath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String[] selectionArgs = new String[]{"%" + mFolderName + "%"};
        String selection = MediaStore.Images.Media.DATA + " like?";
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
        if (al_images != null && al_images.size() > 0) {
           /* ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
            ImageAdapter adapter = new ImageAdapter(this,al_images);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(mSelectedPosition);*/
            mViewPager= (ExtendedViewPager) findViewById(R.id.view_pager);
            adapter = new ImageAdapter(this, al_images);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(mSelectedPosition);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.d("Position--->", "" + position);
                    mSelectedPosition = position;

                    if (position == 1) {  // if you want the second page, for example
                        //Your code here
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
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

        ImageAdapter(Context context, ArrayList<ImageModel> al_images) {
            this.context = context;
            this.mAllimages = al_images;
        }
        public void addView(View view, int index) {
           // mAllimages.add(index, view);
            notifyDataSetChanged();
        }

        public void removeView(int index) {
            Log.d("Image remove",""+index);
            mAllimages.remove(index);
            notifyDataSetChanged();
        }
        @Override
        public int getItemPosition(Object object) {
            if (mAllimages.contains(object)){
                return mAllimages.indexOf(object);
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public int getCount() {
            return mAllimages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageModel imageModel = mAllimages.get(position);
            TouchImageView img = new TouchImageView(container.getContext());

            Glide.with(context).load(Uri.parse("file:///" + imageModel.getAl_imagepath().get(0)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(img);
            Log.d("Image-->", "" + imageModel.getAl_imagepath().get(0));
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //img.setImageResource(R.mipmap.ic_launcher);

            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public void deleteImage(String imagePath) {
        Log.d("Image path-->",""+imagePath);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set up the projection (we only need the ID)
                String[] projection = {MediaStore.Images.Media._ID};

// Match on the file path
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[]{imagePath};

                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getApplication().getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    // We found the ID. Deleting the item via the content provider will also remove the file
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    contentResolver.delete(deleteUri, null, null);
                } else {
                    // File not found in media store DB
                }
                c.close();
            }
        }, 100);
    }


    public void alertConfirmDialog() {
       // builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
        builder = new AlertDialog.Builder(this);
        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to deleting permanently?")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Image size-->",""+al_images.size()+"pos--->"+mSelectedPosition);
                        ImageModel imageModel = al_images.get(mSelectedPosition);
                        Log.d("Image position-->", "" + imageModel.getAl_imagepath().get(0)+"Position--"+mSelectedPosition);
                       deleteImage(imageModel.getAl_imagepath().get(0));
                        al_images.remove(mSelectedPosition);
                        adapter.removeView(mSelectedPosition);


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("AlertDialogExample");
        alert.show();
    }
}
