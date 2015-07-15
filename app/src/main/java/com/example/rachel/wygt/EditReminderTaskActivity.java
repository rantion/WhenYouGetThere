package com.example.rachel.wygt;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Rachel on 11/8/14.
 */
public class EditReminderTaskActivity extends Activity {

    long taskId;
    TaskDataSource taskDataSource = MyApplication.getTaskDataSource();
    GPSTracker gpsTracker = MyApplication.getGpsTracker();
    TaskContactDataSource taskContactDataSource = MyApplication.getTaskContactDataSource();
    AlarmInfoDataSource alarmInfoDataSource = MyApplication.getAlarmInfoDataSource();
    TextView destination;
    GetDrivingDistances getDistance;
    CheckBox there, distance;
    LinearLayout thereL, distanceL;
    EditTextClear message, distanceM;
    Spinner spinner;
    AlarmInfo remindAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.crud_reminder);
        remindAlarm = new AlarmInfo();
        Location location = gpsTracker.getLocation();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng destinationLocation = null;
        thereL = (LinearLayout)findViewById(R.id.edit_reminder_there_layout);
        distanceL = (LinearLayout)findViewById(R.id.edit_reminder_distance_layout);
       // LinearLayout all = (LinearLayout)findViewById(R.id.distance_layout_reminder);
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        double width = metrics.widthPixels;
        thereL.setBackgroundColor(getResources().getColor(R.color.dark_purple));
        distanceL.setBackgroundColor(getResources().getColor(R.color.translucent_black));
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)thereL.getLayoutParams();
//        params.width=(int) width;
//        all.updateViewLayout(thereL, params);
//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)distanceL.getLayoutParams();
//        params1.width =(int) width;
//        all.updateViewLayout(distanceL, params1);
        destination = (TextView) findViewById(R.id.edit_reminder_destination);
        there = (CheckBox) findViewById(R.id.edit_reminder_there_checkbox);
        distance = (CheckBox) findViewById(R.id.edit_reminder_distance_checkbox);
        distanceM = (EditTextClear) findViewById(R.id.edit_reminder_distance_away);
        message = (EditTextClear) findViewById(R.id.edit_reminder_message);
        spinner = (Spinner) findViewById(R.id.edit_reminder_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.miles_minutes, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        initViews();
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
            message.setText(extras.getString("message"));
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
                remindAlarm = infos.get(0);
            }
        }
        setButtonsByAlarm();
        super.onCreate(savedInstanceState);
    }

    public void initViews(){
        LinearLayout display = (LinearLayout)findViewById(R.id.edit_reminder_display);
        LinearLayout destination = (LinearLayout)findViewById(R.id.edit_reminder_destination_layout);
        LinearLayout distance = (LinearLayout)findViewById(R.id.edit_distance_layout_reminder);
        //  LinearLayout recurrence = (LinearLayout)findViewById(R.id.crud_recurrence_call);
        LinearLayout message = (LinearLayout)findViewById(R.id.edit_message_reminder_layout);
        LinearLayout buttons = (LinearLayout)findViewById(R.id.edit_reminder_button_layout);

        display.setMinimumHeight(getPxByPercentage(.20));
        destination.setMinimumHeight(getPxByPercentage(.16));
        distance.setMinimumHeight(getPxByPercentage(.20));
        message.setMinimumHeight(getPxByPercentage(.20));
        buttons.setMinimumHeight(getPxByPercentage(.12));

    }

    public void setButtonsByAlarm(){
        Button monday = (Button)findViewById(R.id.crud_monday_reminder);
        Button tuesday = (Button)findViewById(R.id.crud_tuesday_reminder);
        Button wednesday = (Button)findViewById(R.id.crud_wednesday_reminder);
        Button thursday = (Button)findViewById(R.id.crud_thursday_reminder);
        Button friday = (Button)findViewById(R.id.crud_friday_reminder);
        Button saturday = (Button)findViewById(R.id.crud_saturday_reminder);
        Button sunday = (Button)findViewById(R.id.crud_sunday_reminder);
        if(remindAlarm.is_mon()){
            monday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_tue()){
            tuesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_wed()){
            wednesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_thu()){
            thursday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_fri()){
            friday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_sat()){
            saturday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }
        if(remindAlarm.is_sun()){
            sunday.setBackground(getResources().getDrawable(R.drawable.button_filled));
        }

    }

    public void thereCheckedEditReminder(View view) {
        thereL.setBackgroundColor(getResources().getColor(R.color.dark_purple));
        distanceL.setBackgroundColor(getResources().getColor(R.color.translucent_black));
        there.setChecked(true);
        distance.setChecked(false);
        distanceM.setEnabled(false);
        distanceM.setClickable(false);
        spinner.setClickable(false);
    }

    public void distanceCheckedEditReminder(View view) {
        thereL.setBackgroundColor(getResources().getColor(R.color.translucent_black));
        distanceL.setBackgroundColor(getResources().getColor(R.color.dark_purple));
        distance.setChecked(true);
        there.setChecked(false);
        distanceM.setEnabled(true);
        distanceM.setClickable(true);
        distanceM.setFocusableInTouchMode(true);
        spinner.setClickable(true);
    }

    public void setAlarmReminderCrud(View view){
        Button monday = (Button)findViewById(R.id.crud_monday_reminder);
        Button tuesday = (Button)findViewById(R.id.crud_tuesday_reminder);
        Button wednesday = (Button)findViewById(R.id.crud_wednesday_reminder);
        Button thursday = (Button)findViewById(R.id.crud_thursday_reminder);
        Button friday = (Button)findViewById(R.id.crud_friday_reminder);
        Button saturday = (Button)findViewById(R.id.crud_saturday_reminder);
        Button sunday = (Button)findViewById(R.id.crud_sunday_reminder);

        if(view.equals(monday)){
            boolean on = remindAlarm.is_mon();
            if(!on){
                monday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_mon(true);
            }
            else{
                monday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_mon(false);
            }
        }
        if(view.equals(tuesday)){
            boolean on = remindAlarm.is_tue();
            if(!on){
                tuesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_tue(true);
            }
            else{
                tuesday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_tue(false);
            }
        }
        if(view.equals(wednesday)){
            boolean on = remindAlarm.is_wed();
            if(!on){
                wednesday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_wed(true);
            }
            else{
                wednesday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_wed(false);
            }
        }
        if(view.equals(thursday)){
            boolean on = remindAlarm.is_thu();
            if(!on){
                thursday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_thu(true);
            }
            else{
                thursday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_thu(false);
            }
        }
        if(view.equals(friday)){
            boolean on = remindAlarm.is_fri();
            if(!on){
                friday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_fri(true);
            }
            else{
                friday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_fri(false);
            }
        }
        if(view.equals(saturday)){
            boolean on = remindAlarm.is_sat();
            if(!on){
                saturday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_sat(true);
            }
            else{
                saturday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_sat(false);
            }
        }
        if(view.equals(sunday)){
            boolean on = remindAlarm.is_sun();
            if(!on){
                sunday.setBackground(getResources().getDrawable(R.drawable.button_filled));
                remindAlarm.set_sun(true);
            }
            else{
                sunday.setBackground(getResources().getDrawable(R.drawable.button_outline));
                remindAlarm.set_sun(false);
            }
        }
    }

    public void deleteReminder(View view) {
        Task task = taskDataSource.getTaskById(taskId);
        if (task != null) {
            taskDataSource.deleteTask(task);
        }
        Toast.makeText(this, "Reminder Task Deleted", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }



    public void updateReminder(View view) {
        Task task = taskDataSource.getTaskById(taskId);
        long metersAway = 150;
        String radiusType = "there";
        int original = 0;
        String reminder = " ";
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
        if (message != null) {
            reminder = message.getText().toString();
        }
        int active = 1;
        if(remindAlarm.isHasAlarm()){
            active = 3;
        }
        task.setIsActive(active);
        alarmInfoDataSource.updateAlarmInfo(remindAlarm);
        task.setReminder(reminder);
        if (task != null) {
            taskDataSource.updateTask(task);
        }
        Toast.makeText(this, "Reminder Updated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }

    public int getPxByPercentage(double percentage) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float height = MyApplication.getHeight();
        float px = height * (metrics.densityDpi / 160f);

        return (int) (px * percentage);
    }


}