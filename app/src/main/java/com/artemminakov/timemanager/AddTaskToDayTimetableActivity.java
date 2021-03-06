package com.artemminakov.timemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class AddTaskToDayTimetableActivity extends Activity {

    private ArrayList<Task> mTasks;

    private final String LOG_TAG = "myLogs";

    private TaskDatabaseHelper taskDBHelper;
    private SQLiteDatabase taskDB;


    public class TaskAdapter extends ArrayAdapter<Task> {
        public TaskAdapter(ArrayList<Task> tasks) {
            super(getApplicationContext(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_task, null);
            }

            Task task = getItem(position);


            TextView titleTextView = (TextView) convertView.findViewById(R.id.task_list_item_titleTextView);
            titleTextView.setText(task.getTitle());
            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.task_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(task.isSolved());

            return convertView;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_task_to_day_timetable_activity);
        mTasks = TaskLab.get(this).getTasks();
        final ListView listVievTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        Log.d(LOG_TAG, "In AddTaskToDayTimetable.class");
        taskDBHelper = new TaskDatabaseHelper(getApplicationContext());
        taskDB = taskDBHelper.getWritableDatabase();

        listVievTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) listVievTasks.getItemAtPosition(position);
                Intent intent = new Intent();
                StringBuilder taskPosition = new StringBuilder();
                taskPosition.append(position + 2);
                Log.d(LOG_TAG, "Task position " + taskPosition.toString());
                intent.putExtra("taskId", TaskDatabaseHelper.queryTaskId(task.getTitle(), taskDB));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        TaskDatabaseHelper.queryGetTasks(taskDB, getApplicationContext());
        ListView listViewTasks = (ListView) findViewById(R.id.listViewTasks_AddToDay);
        Collections.reverse(mTasks);
        TaskAdapter taskAdapter = new TaskAdapter(mTasks);
        listViewTasks.setAdapter(taskAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TaskLab.get(this).clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskLab.get(this).clear();
    }

}
