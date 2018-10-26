package com.rvm.main.activity;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;

import com.rvm.main.GalleryAdapter;
import com.rvm.main.ImageModel;
import com.rvm.main.R;
import com.rvm.main.RecyclerViewAdapter;

import java.util.ArrayList;

public class GalleryDisplayActivity extends AppCompatActivity {
    private String mFolderName="";
    public static ArrayList<ImageModel> al_images = new ArrayList<>();
    boolean boolean_folder;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_gallery_display);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
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
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                ImageModel obj_model = new ImageModel();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);
                al_images.add(obj_model);


        }


        mRecyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),al_images);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        return al_images;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
