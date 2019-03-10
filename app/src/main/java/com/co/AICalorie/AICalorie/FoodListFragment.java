package com.co.AICalorie.AICalorie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class FoodListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mFoodRecyclerView;
    private FoodAdapter mAdapter;
    private Day mDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID dayId = (UUID) getActivity().getIntent()
                .getSerializableExtra(FoodListActivity.EXTRA_DAY_ID);
        mDay = DayLab.get(getActivity()).getDay(dayId);

        getActivity().setTitle(mDay.getTitle());


        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);

        mFoodRecyclerView = (RecyclerView) view
                .findViewById(R.id.food_recycler_view);
        mFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_food_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_food: {

                //String foodTitle = "";
                String foodTitle ="Name\n";
                if (!foodTitle.isEmpty()) {
                    Food food = new Food();
                    //food.setTitle(foodTitle + food.getDate());
                    food.setTitle(foodTitle);
                    food.setDAY_uuid(mDay.getId());
                    food.setShown(true);
                    FoodLab.get(getActivity()).addFood(food);

                    Intent intent = FoodPagerActivity
                            .newIntent(getActivity(), food.getId());
                    startActivity(intent);

                }
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    private void updateUI() {
        //we need foods here...
        List<Food> foods = FoodLab.get(getActivity()).getFoods(mDay.getId());

        if (mAdapter == null) {
            mAdapter = new FoodAdapter(foods);
            mFoodRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFoods(foods);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class FoodHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Food mFood;
        private TextView mTextView;
        private ImageView mImageView;
        private File mPhotoFile;


        public FoodHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_food, parent, false));
            itemView.setOnClickListener(this);
            mTextView = (TextView) itemView.findViewById(R.id.food_text);  //TODO something not right need TITLE
            mImageView = (ImageView) itemView.findViewById(R.id.food_shown);
        }

        public void bind(Food food) {
            mFood = food;
            //mTextView.setText(mFood.getText()); //TODO changed to title but not appearing
            mTextView.setText(mFood.getTitle());
            //mImageView.setVisibility(food.isShown() ? View.VISIBLE : View.GONE);
            mPhotoFile = FoodLab.get(getActivity()).getPhotoFile(mFood);
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.co.AICalorie.AICalorie.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            if (mPhotoFile == null || !mPhotoFile.exists()) {
                mImageView.setImageDrawable(null);
            } else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(
                        mPhotoFile.getPath(), getActivity());
                mImageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onClick(View view){
            // Toast.makeText(getActivity(),
            //         mFood.getText() + " clicked!", Toast.LENGTH_SHORT).show();

            //Intent intent = new Intent(getActivity(), FoodActivity.class);

            //Intent intent = FoodActivity.newIntent(getActivity(), mFood.getId());

            Intent intent = FoodPagerActivity.newIntent(getActivity(), mFood.getId());
            startActivity(intent);
        }
    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {

        private List<Food> mFoods;

        public FoodAdapter(List<Food> foods) {
            mFoods = foods;
        }

        public void setFoods(List<Food> foods) {
            mFoods = foods;
        }

        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FoodHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            Food food = mFoods.get(position);
            holder.bind(food);
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }



}
