// Copyright (C) 2015 Peter Robinson
package com.nelladragon.showertimertalking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nelladragon.showertimertalking.users.UserController;
import com.nelladragon.showertimertalking.users.UserPhotoUtil;
import com.nelladragon.showertimertalking.users.UserProfile;
import com.nelladragon.common.util.Pixels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a button to allow users to select a photo from gallery
 * or from a list of images which are shipped with the app.
 *
 */
public class ChoosePhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;


    /**
     * Adapter class to allow data to be transferred from the list and the activity.
     */
    private class PhotoListAdapter extends BaseAdapter {
        private class ViewHolder {
            protected ImageView iconImage;
            protected TextView iconName;
            protected RadioButton radioButton;
        }

        private List<IconItem> icons;
        private LayoutInflater inflater;
        private Context context;

        public PhotoListAdapter(Context context, List<IconItem> iconItems) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.icons = iconItems;
        }


        public int getCount() {
            return this.icons.size();
        }

        // Get the item associated with the position in the dataset.
        public Object getItem(int position) {
            return this.icons.get(position);
        }

        public long getItemId(int position) {
            return UserPhotoUtil.drawableNameToPhotoId(this.icons.get(position).file);

        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_photo, parent, false);

                holder = new ViewHolder();
                holder.iconName = (TextView) convertView.findViewById(R.id.iconName);
                holder.iconImage = (ImageView) convertView.findViewById(R.id.iconImage);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.iconRadio);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final IconItem item = this.icons.get(position);
            holder.iconName.setText(item.name);

            holder.iconImage.setImageResource(UserPhotoUtil.drawableNameToId(context, item.file));

            holder.radioButton.setChecked(item.isChecked);

//            convertView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    ViewHolder holder = (ViewHolder) v.getTag();
//                    for (int i = 0; i < icons.size(); i++) {
//                        if (i == position)
//                            icons.get(i).isChecked = true;
//                        else
//                            icons.get(i).isChecked = false;
//                    }
//                    getActiv
//                    //getDialog().dismiss();
//                }
//            });

            return convertView;
        }
    }


    private static class IconItem {

        private String file;
        private boolean isChecked;
        private String name;

        public IconItem(String name, String file, boolean isChecked) {
            this.name = name;
            this.file = file;
            this.isChecked = isChecked;
        }
    }


    ListView listView;
    PhotoListAdapter adapter;


    String[] photoDescriptions;
    String[] photoDrawableNames;
    UserProfile activeProfile;
    UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources resources = getResources();
        this.photoDescriptions = resources.getStringArray(R.array.profile_photo_descriptions);
        this.photoDrawableNames = resources.getStringArray(R.array.profile_photo_drawable_name);

        this.userController = UserController.getInstance(ChoosePhotoActivity.this);
        this.activeProfile = this.userController.getActiveProfile();

        this.adapter = new PhotoListAdapter(this, loadPhotoInfo());
        this.listView = (ListView) findViewById(R.id.listViewChoosePhoto);
        this.listView.setAdapter(this.adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long photoId) {
                activeProfile.setPhotoId((int) photoId);
                activeProfile.setPhotoDescription(photoDescriptions[position]);
                userController.syncActiveProfile();

                ChoosePhotoActivity.this.setResult(RESULT_OK);
                ChoosePhotoActivity.this.finish();
            }
        });

        Button choosePhoto = (Button) findViewById(R.id.buttonChoosePhoto);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }


    private List<IconItem> loadPhotoInfo() {
        String photoDescriptionOfActiveProfile = activeProfile.getPhotoDesc();
        List<IconItem> photosInfo = new ArrayList<>();
        for (int i=0; i < this.photoDescriptions.length; i++) {
            boolean currentItem = photoDescriptionOfActiveProfile.equals(this.photoDescriptions[i]);
            IconItem item = new IconItem(this.photoDescriptions[i], this.photoDrawableNames[i], currentItem);
            photosInfo.add(item);
        }
        return photosInfo;
    }


    // TODO need to replace strings here with String resources.
    private static final String TAKE_PHOTO = "Take Photo";
    private static final String FROM_GALLERY = "Choose from Gallery";
    private static final String CANCEL = "Cancel";

    private void selectImage() {
        final CharSequence[] items = { TAKE_PHOTO, FROM_GALLERY, CANCEL};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChoosePhotoActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(TAKE_PHOTO)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(FROM_GALLERY)) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals(CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                path = f.getAbsolutePath();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                path = getPath(selectedImageUri, ChoosePhotoActivity.this);
            }
            if (path == null) {
                return;
            }

            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            btmapOptions.inJustDecodeBounds = false;
            btmapOptions.inSampleSize = 3;
            Bitmap bm = BitmapFactory.decodeFile(path, btmapOptions);

            int iconSize = (int) Pixels.dipToPixels(this, UserPhotoUtil.PHOTO_SIZE_DP);

            bm = Bitmap.createScaledBitmap(bm, iconSize, iconSize, true);
            Drawable d = new BitmapDrawable(ChoosePhotoActivity.this.getResources(), bm);

            UserProfile currentProfile = userController.getActiveProfile();
            currentProfile.setPhotoDescription("Photo");
            currentProfile.setPhotoDrawable(d);
            userController.syncActiveProfile();

            ChoosePhotoActivity.this.setResult(RESULT_OK);
            ChoosePhotoActivity.this.finish();


        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
