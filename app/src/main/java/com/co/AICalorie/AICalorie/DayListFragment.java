package com.co.AICalorie.AICalorie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class DayListFragment extends Fragment {

    private RecyclerView mDayRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private DayAdapter mAdapter;
    private boolean mDeleteDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_day_list, container, false);

        mDayRecyclerView = (RecyclerView) view
                .findViewById(R.id.day_recycler_view);
        mDayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFloatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.floating_add_day_action_button);

        updateUI();

        mDayRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                }else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDay();
            }
        });

        mDeleteDay = false;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_day_list, menu);
    }

    public void AddDay(){

        Day day = new Day();
        String dayTitle = day.getDate();
        day.setTitle(dayTitle);
        DayLab.get(getActivity()).addDay(day);

        Intent intent = FoodListActivity.newIntent(getActivity(), day.getId());
        startActivity(intent);

    }

    public void showAddDayDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());

        //Limit input size here...
        InputFilter[] fa = new InputFilter[1];
        fa[0] = new InputFilter.LengthFilter(20);
        editText.setFilters(fa);

        alert.setMessage("Enter Day (required)");
        alert.setTitle("New Day");
        alert.setView(editText);
        alert.setPositiveButton("Add Day", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String dayTitle = editText.getText().toString();
                if(!dayTitle.isEmpty()){
                    Day day = new Day();
                    day.setTitle(dayTitle);
                    DayLab.get(getActivity()).addDay(day);

                    Intent intent = FoodListActivity.newIntent(getActivity(), day.getId());
                    startActivity(intent);
                }else{
                    //nothing
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_day:
                AddDay();
                return true;

            case R.id.delete_food:
                //showDeleteDayDialog();
                if(mAdapter.getDayCount() > 0) {
                    showDeleteDayDialog();
                }
                return true;
        }
        return true;
    }

    private void newDayAlert(){
        //final View view = getLayoutInflater().inflate(R.layout.alert_new_day, null);
        //AlertDialog alertDialog = new AlertDialog().Builder(getContext()).
    }

    private void showDeleteDayDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        //DayLab.get(getActivity()).deleteDay(mDay);
                        mDeleteDay = true;

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Once you select a day from the list it will be deleted. Continue?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        alert.setTitle("Delete Day");


    }

    private void updateUI() {
        DayLab dayLab = DayLab.get(getActivity());
        List<Day> days = dayLab.getDays();

        if (mAdapter == null) {
            mAdapter = new DayAdapter(days);
            mDayRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDays(days);
            mAdapter.notifyDataSetChanged();
        }

        if( 0 == mAdapter.getItemCount()){
            //make button visible
        }else{
            //hide button
        }
    }

    private class DayHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Day mDay;
        private TextView mDayTitleTextView;

        public DayHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_day, parent, false));
            itemView.setOnClickListener(this);
            mDayTitleTextView = (TextView) itemView.findViewById(R.id.day_title);
        }

        public void bind(Day day) {
            mDay = day;
            mDayTitleTextView.setText(mDay.getTitle());
        }

        @Override
        public void onClick(View view) {
            if(mDeleteDay){
                DialogInterface.OnClickListener dialogDeleteClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                DayLab.get(getActivity()).deleteDay(mDay);
                                mDeleteDay = false;
                                mAdapter.removeDay(mDay);
                                mAdapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                mDeleteDay = false;
                                break;
                        }
                    }
                };
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage("Are you sure you want to delete " + mDay.getTitle() + " ?")
                        .setPositiveButton("Yes", dialogDeleteClickListener)
                        .setNegativeButton("No", dialogDeleteClickListener).show();
                alert.setTitle("Delete Day");
            }else{
                Intent intent = FoodListActivity.newIntent(getActivity(), mDay.getId());
                startActivity(intent);
            }
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {

        private List<Day> mDays;

        public DayAdapter(List<Day> days) {
            mDays = days;
        }

        @Override
        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DayHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DayHolder holder, int position) {
            Day day = mDays.get(position);
            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }

        public void setDays(List<Day> days) {
            mDays = days;
        }

        public void removeDay(Day d){
            mDays.remove(d);
        }

        public int getDayCount(){
            return mDays.size();
        }
    }



}
