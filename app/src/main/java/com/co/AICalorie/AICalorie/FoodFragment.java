package com.co.AICalorie.AICalorie;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class FoodFragment extends Fragment {

    private static final String ARG_FOOD_ID = "food_id";
    private static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_SIZE = 2;


    private Food mFood;
    private File mPhotoFile;
    private Day mCurrentDay;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private boolean mDelete;
    private TextView mFoodinfo;
    //public String mSize="";




    public static FoodFragment newInstance(UUID foodId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD_ID, foodId);

        FoodFragment fragment = new FoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentDay = DayLab.get(getActivity()).getCurrentDay();

        UUID foodId = (UUID) getArguments().getSerializable(ARG_FOOD_ID);
        mFood = FoodLab.get(getActivity()).getFood(foodId);

        mPhotoFile = FoodLab.get(getActivity()).getPhotoFile(mFood);

        setHasOptionsMenu(true);


    }

    @Override
    public void onPause() {
        super.onPause();
        FoodLab.get(getActivity()).updateFood(mFood);
        //String text = mFood.getText();
        //mFoodinfo.setText(text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SIZE){
            //Toast.makeText(getContext(), data.getStringExtra("size"), Toast.LENGTH_SHORT).show();
            //mSize = data.getStringExtra("size");
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

            updateFoodInfoView(data.getStringExtra("size"));

        }

        if (requestCode == REQUEST_PHOTO){
            //mSize = "working in photo";

            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.co.AICalorie.AICalorie.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_food, container, false);

        mDelete = false;

        PackageManager packageManager = getActivity().getPackageManager();

        mPhotoButton = (ImageButton) v.findViewById(R.id.food_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.co.AICalorie.AICalorie.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);

                Intent myIntent = new Intent(getActivity(), ArMeasureActivity.class);
                //startActivity(myIntent);
                startActivityForResult(myIntent, REQUEST_SIZE);

            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.food_image);
        updatePhotoView();

        mFoodinfo = (TextView) v.findViewById(R.id.food_info);
        String text = mFood.getText();
        mFoodinfo.setText(text);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_food, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.delete_food:
                showDeleteFoodDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showDeleteFoodDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if(!mDelete) {  //prevent double clicks
                            mDelete = true;
                            //FoodLab.get(getActivity()).deleteFood(mFood);
                            //Intent intent = FoodListActivity
                             //       .newIntent(getActivity(), mCurrentDay.getId());
                            //startActivity(intent);
                            FoodLab.get(getActivity()).deleteFood(mFood);
                            getActivity().finish();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("This action will delete the current food. Continue?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        alert.setTitle("Delete Food");


    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }

    private void updateFoodInfoView(String size) {
         mFood.setText(mFood.getTitle() + "\n" +
                            "Size: " + size + " cm \n" +
                            "Calorie: " + "Food API" + "\n" +
                            "\n" +
                            mFood.getDate());
         String text = mFood.getText();
         mFoodinfo.setText(text);

    }
}
