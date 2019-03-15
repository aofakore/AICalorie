package com.co.AICalorie.AICalorie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
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
import static com.co.AICalorie.AICalorie.Config.INPUT_SIZE;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.co.AICalorie.AICalorie.common.helpers.CameraPermissionHelper;
import com.co.AICalorie.AICalorie.model.Recognition;

import org.tensorflow.TensorFlow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

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
    private TensorFlowImageRecognizer recognizer;
    private Bitmap croppedBitmap = null;
    private String mSize;

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
            getNutritionInformation(mFood.getTitle(), data.getStringExtra("size"));
        }

        if (requestCode == REQUEST_PHOTO){
            //mSize = "working in photo";

            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.co.AICalorie.AICalorie.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
            runStuff();
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

                //Request camera permission
                if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
                    CameraPermissionHelper.requestCameraPermission(getActivity());
                    return;
                }

                Intent myIntent = new Intent(getActivity(), ArMeasureActivity.class);
                //startActivity(myIntent);
                startActivityForResult(myIntent, REQUEST_SIZE);

                startActivityForResult(captureImage, REQUEST_PHOTO);

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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getContext(), "Permission denied to use Camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

    private void updateFoodInfoView(String size, String calories) {
         mFood.setText(" " + mFood.getTitle() + "\n" +
                            " Size: " + size + " cm \n" +
                            " Calorie: " + calories + " kcal \n" +
                            "\n" +
                            " " + mFood.getDate());
         String text = mFood.getText();
         mFoodinfo.setText(text);
    }

    private void runStuff(){
        recognizer = TensorFlowImageRecognizer.create(getActivity().getAssets());
        Bitmap bitmap = PictureUtils.getScaledBitmap(
                mPhotoFile.getPath(), getActivity());
        croppedBitmap = Bitmap.createScaledBitmap(
                bitmap, INPUT_SIZE, INPUT_SIZE, true);

        List<Recognition> results = null;
        String result = "";
        try {
            results = recognizer.recognizeImage(croppedBitmap);
            HashMap<String, String> foodLabels = new HashMap<String, String>();
            foodLabels.put("apple","apple");
            foodLabels.put("banana","banana");
            foodLabels.put("sandwich","sandwich");
            foodLabels.put("orange","orange");
            foodLabels.put("broccoli","broccoli");
            foodLabels.put("carrot","carrot");
            foodLabels.put("hot dog","hot dog");
            foodLabels.put("pizza","pizza");
            foodLabels.put("donut","donut");
            foodLabels.put("cake","cake");
            if(foodLabels.containsKey(results.get(0).getTitle())){
                result = String.valueOf(results.get(0).getTitle());
            }
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            //result = String.valueOf(results.get(1).getTitle());
            //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            //result = String.valueOf(results);
            //result = Arrays.toString(results.toArray());


        } catch(Exception e) {
        }

        mFood.setTitle(result);
        //String text = mFood.getText();
        //mFoodinfo.setText(text);

        // return results;
        //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void sendSearchRequest(RequestQueue queue, String searchTerm, String qty){
        String searchUrl = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + searchTerm + "&ds=Standard%20Reference&max=1&sort=r&offset=0&api_key=CFvZE247DQ83dQH8FMAjsZngQLois9J6PgGpxaVg";

        JsonObjectRequest searchRequest = new JsonObjectRequest
            (Request.Method.GET, searchUrl, null,
                response -> {
                    if(response.has("list")){
                        try {
                            String ndbno = response.getJSONObject("list").getJSONArray("item").getJSONObject(0).getString("ndbno");
                            sendReportRequest(queue, ndbno, qty);
                        } catch (JSONException e) {
                            // Something failed, set calorie count to zero.
                            updateFoodInfoView(qty, "0");
                            e.printStackTrace();
                        }
                    } else {
                        // Something failed, set calorie count to zero.
                        updateFoodInfoView(qty, "0");
                    }
                },
                error -> {
                    // TODO: Handle error
                    System.out.println("ERROR");
                });

        queue.add(searchRequest);
    }

    private void sendReportRequest(RequestQueue queue, String ndbno, String qty){
        String reportUrl = "https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + ndbno + "&type=b&format=json&api_key=CFvZE247DQ83dQH8FMAjsZngQLois9J6PgGpxaVg";
        // Using NDB Number, find nutrient info for that food.
        // TODO: Third Search
        // Calories are measured in kcal under "Energy"

        JsonObjectRequest reportRequest = new JsonObjectRequest
            (Request.Method.GET, reportUrl, null,
                response -> {
                    try {
                        JSONArray nutrientArray = response.getJSONArray("foods").getJSONObject(0).getJSONObject("food").getJSONArray("nutrients");
                        JSONObject energyObj;
                        for (int i = 0; i < nutrientArray.length(); i++){
                            energyObj = nutrientArray.getJSONObject(i);
                            if(energyObj.getString("unit").equals("kcal")){
                                JSONArray measures = energyObj.getJSONArray("measures");
                                getCalCount(measures, qty);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        // Something failed, set calorie count to zero.
                        updateFoodInfoView(qty, "0");
                        e.printStackTrace();
                    }
                },
                error -> {
                    // TODO: Handle error
                    // Something failed, set calorie count to zero.
                    updateFoodInfoView(qty, "0");
                });

        queue.add(reportRequest);
    }

    private void getCalCount(JSONArray measures, String qty){
        try {
            // Check all portion sizes for ones with measurements
            int nIndex = -1;
            String pLabel = "";
            double nSize = 0;
            double qSize = Double.parseDouble(qty.replaceAll("[^0-9.]+", ""));
            for(int i = 0; i < measures.length(); i++){
                pLabel = measures.getJSONObject(i).getString("label");
                if(pLabel.contains("\"")){
                    // The portion size includes an inch measure. USDA does everything in inches.
                    //Parse label for size
                    String sizeString = pLabel.substring(pLabel.indexOf('('),pLabel.indexOf('"')).replaceAll("[^0-9-/.]+", "");
                    double sizeVal = 0;
                    if(sizeString.contains("-") && sizeString.split("-|/").length == 3){
                        sizeVal = (Double.parseDouble(sizeString.split("-|/")[0]) +
                                (Double.parseDouble(sizeString.split("-|/")[1]) /
                                Double.parseDouble(sizeString.split("-|/")[2]))) * 2.54;
                    } else {
                        sizeVal = Double.parseDouble(sizeString.replaceAll("[^0-9.]+", "")) * 2.54;
                    }
                    if(nSize == 0 || Math.abs(qSize-sizeVal) < Math.abs(qSize-nSize)){
                        nSize = sizeVal;
                        nIndex = i;
                    }
                }
            }
            // If no portion included a measure, just get one portion.
            if (nIndex == -1) {
                String energy = new Double(Math.round(Math.max(
                        Double.parseDouble(measures.getJSONObject(0).getString("value")),
                        Double.parseDouble(measures.getJSONObject(0).getString("value"))*200/measures.getJSONObject(0).getDouble("eqv")
                ))).toString();
                updateFoodInfoView(qty, energy);
            } else {
                String energy = measures.getJSONObject(nIndex).getString("value");
                updateFoodInfoView(qty, energy);
            }
        } catch (Exception e) {
            // Something failed, set calorie count to zero.
            updateFoodInfoView(qty, "0");
            e.printStackTrace();
        }
    }

    private void getNutritionInformation(String searchTerm, String qty) {
        if(searchTerm.equals("")){
            updateFoodInfoView(qty, "0");
            return;
        }
        String rawSearchUrl = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + searchTerm + ",raw&ds=Standard%20Reference&max=1&sort=r&offset=0&api_key=CFvZE247DQ83dQH8FMAjsZngQLois9J6PgGpxaVg";
        //Make search call to find NDB Number (ndbno)
        // TODO: First Search
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        JsonObjectRequest rawSearchRequest = new JsonObjectRequest
            (Request.Method.GET, rawSearchUrl, null,
            response -> {
                if(response.has("list")){
                    try {
                        String ndbno = response.getJSONObject("list").getJSONArray("item").getJSONObject(0).getString("ndbno");
                        sendReportRequest(queue, ndbno, qty);
                    } catch (JSONException e) {
                        // Something failed, set calorie count to zero.
                        updateFoodInfoView(qty, "0");
                        e.printStackTrace();
                    }
                } else {
                    sendSearchRequest(queue, searchTerm, qty);
                }
            },
            error -> {
                // TODO: Handle error
                // Something failed, set calorie count to zero.
                updateFoodInfoView(qty, "0");
            });

        queue.add(rawSearchRequest);
    }
}
