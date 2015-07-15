package com.example.rachel.wygt;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Rachel on 11/6/14.
 */
public class EditCallTaskActivity extends Activity {
    long taskId;
    TaskDataSource taskDataSource = MyApplication.getTaskDataSource();
    GPSTracker gpsTracker = MyApplication.getGpsTracker();
    TaskContactDataSource taskContactDataSource = MyApplication.getTaskContactDataSource();
    AlarmInfoDataSource alarmInfoDataSource = MyApplication.getAlarmInfoDataSource();
    TextView destination;
    GetDrivingDistances getDistance;
    CheckBox there, distance;
    AutoCompleteTextView contacts;
    LinearLayout thereL, distanceL;
    EditTextClear distanceM;
    private SimpleAdapter mAdapter;
    private AlarmInfo callAlarm;
    Spinner spinner;
    ArrayList<Map<String, String>> mPeopleList = MyApplication.getmPeopleList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callAlarm = new AlarmInfo();
        setContentView(R.layout.crud_call);
        Location location = gpsTracker.getLocation();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng destinationLocation = null;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        contacts = (AutoCompleteTextView) findViewById(R.id.edit_call_contact);
        (new PopulateContacts()).execute();
        destination = (TextView) findViewById(R.id.edit_call_destination);
        there = (CheckBox) findViewById(R.id.edit_call_there_checkbox);
        distance = (CheckBox) findViewById(R.id.edit_call_distance_checkbox);
        LinearLayout all = (LinearLayout)findViewById(R.id.edit_call_distance_layout);
        thereL = (LinearLayout) findViewById(R.id.edit_call_there_checkbox_layout);
        distanceL = (LinearLayout)findViewById(R.id.edit_call_distance_checkbox_layout);
        double width = MyApplication.getWidth();
        thereL.setMinimumWidth(getPxByPercentage(width));
        distanceL.setMinimumWidth(getPxByPercentage(width));
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)thereL.getLayoutParams();
//        params.height= getPxByPercentage(.50);
//        all.updateViewLayout(thereL, params);
//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)distanceL.getLayoutParams();
//        params1.height= getPxByPercentage(.50);
//        all.updateViewLayout(distanceL, params1);

        distanceM = (EditTextClear) findViewById(R.id.edit_call_distance_away);
        spinner = (Spinner) findViewById(R.id.edit_call_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.miles_minutes, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);
        initViews();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            taskId = extras.getLong("taskID");
            double lat = extras.getDouble("lat");
            double _long = extras.getDouble("long");
            destinationLocation = new LatLng(lat, _long);
            try {
                (getDistance = new GetDrivingDistances()).execute(destinationLocation, currentLocation).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            destination.setText(extras.getString("destination"));
            String radiusType = extras.getString("radiusType");
            contacts.setText(extras.getString("contact"));
            if (radiusType.equals("there")) {
                there.setChecked(true);
                distance.setChecked(false);
                distanceM.setEnabled(false);
                distanceM.setClickable(false);
                spinner.setClickable(false);
            } else {
                distance.setChecked(true);
                there.setChecked(false);
                distanceM.setEnabled(true);
                distanceM.setClickable(true);
                distanceM.setFocusableInTouchMode(true);
                spinner.setClickable(true);
                int distance = extras.getInt("original");
                distanceM.setText(String.valueOf(distance));
                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(radiusType);
                spinner.setSelection(spinnerPosition);
            }
          List<AlarmInfo> infos = alarmInfoDataSource.getAlarmInfoByTaskId(taskId);
          if(infos!= null) {
              callAlarm = infos.get(0);
          }


        }
       setButtonsByAlarm();
        super.onCreate(savedInstanceState);
    }

    public void setAlarmCallCrud(View view){
        Button monday = (Button)findViewById(R.id.crud_monday_call);
        Button tuesday = (Button)findViewById(R.id.crud_tuesday_call);
        Button wednesday = (Button)findViewById(R.id.crud_wednesday_call);
        Button thursday = (Button)findViewById(R.id.crud_thursday_call);
        Button friday = (Button)findViewById(R.id.crud_friday_call);
        Button saturday = (Button)findViewById(R.id.crud_saturday_call);
        Button sunday = (Button)findViewById(R.id.crud_sunday_call);
        if(view.equals(monday)){
            boolean on = callAlarm.is_mon();
            if(!on){
                monday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_mon(true);
            }
            else{
                monday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_mon(false);
            }
        }
        if(view.equals(tuesday)){
            boolean on = callAlarm.is_tue();
            if(!on){
                tuesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_tue(true);
            }
            else{
                tuesday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_tue(false);
            }
        }
        if(view.equals(wednesday)){
            boolean on = callAlarm.is_wed();
            if(!on){
                wednesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_wed(true);
            }
            else{
                wednesday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_wed(false);
            }
        }
        if(view.equals(thursday)){
            boolean on = callAlarm.is_thu();
            if(!on){
                thursday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_thu(true);
            }
            else{
                thursday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_thu(false);
            }
        }
        if(view.equals(friday)){
            boolean on = callAlarm.is_fri();
            if(!on){
                friday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_fri(true);
            }
            else{
                friday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_fri(false);
            }
        }
        if(view.equals(saturday)){
            boolean on = callAlarm.is_sat();
            if(!on){
                saturday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_sat(true);
            }
            else{
                saturday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_sat(false);
            }
        }
        if(view.equals(sunday)){
            boolean on = callAlarm.is_sun();
            if(!on){
                sunday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                callAlarm.set_sun(true);
            }
            else{
                sunday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                callAlarm.set_sun(false);
            }
        }
    }

    public void setButtonsByAlarm(){
        Button monday = (Button)findViewById(R.id.crud_monday_call);
        Button tuesday = (Button)findViewById(R.id.crud_tuesday_call);
        Button wednesday = (Button)findViewById(R.id.crud_wednesday_call);
        Button thursday = (Button)findViewById(R.id.crud_thursday_call);
        Button friday = (Button)findViewById(R.id.crud_friday_call);
        Button saturday = (Button)findViewById(R.id.crud_saturday_call);
        Button sunday = (Button)findViewById(R.id.crud_sunday_call);
        if(callAlarm.is_mon()){
            monday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_tue()){
            tuesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_wed()){
            wednesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_thu()){
            thursday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_fri()){
            friday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_sat()){
            saturday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(callAlarm.is_sun()){
            sunday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }

    }

    public void initViews(){
        LinearLayout display = (LinearLayout)findViewById(R.id.edit_call_display);
        LinearLayout destination = (LinearLayout)findViewById(R.id.edit_call_destination_layout);
        LinearLayout distance = (LinearLayout)findViewById(R.id.edit_call_distance_layout);
      //  LinearLayout recurrence = (LinearLayout)findViewById(R.id.crud_recurrence_call);
        LinearLayout contacts = (LinearLayout)findViewById(R.id.edit_call_contact_layout);
        LinearLayout buttons = (LinearLayout)findViewById(R.id.edit_call_button_layout);

        display.setMinimumHeight(getPxByPercentage(.20));
        destination.setMinimumHeight(getPxByPercentage(.16));
        distance.setMinimumHeight(getPxByPercentage(.20));
        contacts.setMinimumHeight(getPxByPercentage(.20));
        buttons.setMinimumHeight(getPxByPercentage(.12));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }


    public void editCallThereChecked(View view) {

        thereL.setBackgroundColor(getResources().getColor(R.color.dark_purple));
        distanceL.setBackgroundColor(getResources().getColor(R.color.translucent_black));
        distance.setChecked(false);
        there.setChecked(true);
        distanceM.setEnabled(false);
        distanceM.setClickable(false);
        spinner.setClickable(false);
    }

    public void editCallDistanceChecked(View view) {
        thereL.setBackgroundColor(getResources().getColor(R.color.translucent_black));
        distanceL.setBackgroundColor(getResources().getColor(R.color.dark_purple));
        there.setChecked(false);
        distance.setChecked(true);
        distanceM.setEnabled(true);
        distanceM.setClickable(true);
        distanceM.setFocusableInTouchMode(true);
        spinner.setClickable(true);
    }

    public void deleteCall(View view) {
        Task task = taskDataSource.getTaskById(taskId);
        if (task != null) {
            taskDataSource.deleteTask(task);
        }
        Toast.makeText(this, "Call Task Deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }


    public long getMinutesAwayRadius(int minutes) {
        String distanceAway = getDistance.getDestDistMeters();
        String _duration = getDistance.getDestDistSeconds();
        int meters = Integer.parseInt(distanceAway);
        int seconds = Integer.parseInt(_duration);
        double rate = (meters / seconds);
        double temp = rate * minutes;
        return (long) temp * 60;
    }

    public long convertToMeters(double miles) {
        long meters = (long) (miles * 1609.34);
        return meters;
    }


    public void updateCall(View view) {
        Task task = taskDataSource.getTaskById(taskId);
        List<TaskContact> contactTasks = taskContactDataSource.getTaskContactsByTaskId(task.getId());
        for (TaskContact contact : contactTasks) {
            taskContactDataSource.deleteTaskContact(contact);
        }
        long metersAway = 150;
        String radiusType = "there";
        int original = 0;
        String reminder = "filler";
        if (distance.isChecked()) {

            String miles = distanceM.getText().toString();
            if (miles.length() == 0) {
                Toast.makeText(this, "Please Enter a Distance", Toast.LENGTH_SHORT).show();
                return;
            }
            original = Integer.parseInt(miles);
            if (spinner.getSelectedItem().equals("miles")) {
                metersAway = convertToMeters(Double.valueOf(miles));
                radiusType = "miles";
            } else if (spinner.getSelectedItem().equals("minutes")) {
                metersAway = getMinutesAwayRadius(Integer.parseInt(miles));
                radiusType = "minutes";
            } else if (spinner.getSelectedItem().equals("meters")) {
                metersAway = Long.valueOf(miles);
                radiusType = "meters";
            }
        }
        task.setOriginalRadius(Integer.parseInt(distanceM.getText().toString()));
        task.setRadius_type(radiusType);
        task.setRadius(metersAway);
        task.setReminder(reminder);
        String _contacts = contacts.getText().toString();
        String[] num1 = _contacts.split("<");
        for (int i = 1; i < num1.length; i++) {
            String[] num2 = num1[i].split(">");
            String name = "";
            if (i == 1) {
                name = num1[0];
            } else {
                String[] num3 = num1[i - 1].split(">");
                String[] num4 = num3[1].split(",");
                name = num4[1];
            }
            TaskContact contact = taskContactDataSource.createTaskContact(num2[0], name, task.getId());
        }
        int active = 1;
        if(callAlarm.isHasAlarm()){
            active = 3;
        }
        task.setIsActive(active);
        alarmInfoDataSource.updateAlarmInfo(callAlarm);
        if (task != null) {
            taskDataSource.updateTask(task);
        }
        Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }

    private class PopulateContacts extends AsyncTask<Void, Void, ArrayList<Map<String, String>>> {
        @Override
        protected ArrayList<Map<String, String>> doInBackground(Void... params) {

            return EditCallTaskActivity.this.mPeopleList;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> maps) {
            mAdapter = new SimpleAdapter(MyApplication.getAppContext(), mPeopleList, R.layout.custcontview,
                    new String[]{"Name", "Phone", "Type"}, new int[]{
                    R.id.ccontName, R.id.ccontNo, R.id.ccontType}
            );

           contacts.setThreshold(1);
           contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, String> map = (Map<String, String>) parent
                            .getItemAtPosition(position);
                    String name = map.get("Name");
                    String number = map.get("Phone");
                    String current = name + "<" + number + "> ";
                    contacts.setText(current);
                    contacts.setSelection(current.length());
                }
            });
            contacts.setAdapter(mAdapter);
            EditCallTaskActivity.this.mPeopleList = maps;
        }
    }

    public int getPxByPercentage(double percentage) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float height = MyApplication.getHeight();
        float px = height * (metrics.densityDpi / 160f);

        return (int) (px * percentage);
    }

    private boolean isAlarmForToday(AlarmInfo info){
        boolean forToday = false;
        int day = getDay();
        if (day == 2 && info.is_mon()) {
            forToday = true;
        } else if (day == 3 && info.is_tue()) {
            forToday = true;
        } else if (day == 4 && info.is_wed()) {
            forToday = true;
        } else if (day == 5 && info.is_thu()) {
            forToday = true;
        } else if (day == 6 && info.is_fri()) {
            forToday = true;
        } else if (day == 7 && info.is_sat()) {
            forToday = true;
        } else if (day == 1 && info.is_sun()) {
            forToday = true;
        }
        return forToday;
    }

    public int getDay() {
        Date d = new Date();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }


}

