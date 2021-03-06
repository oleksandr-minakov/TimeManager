package com.artemminakov.timemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "timtmanager.sqlite";
    private static final int VERSION = 1;
    private static final String LOG_TAG = "TaskDatabaseHelper";

    private static int positionInTaskSolve = 0;

    //table tasks
    private static final String TABLE_TASK = "tasks";
    private static final String COLUMN_TASK_ID = "idTask";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_PRIORITY = "priority";
    private static final String COLUMN_TASK_QUANTITY_HOURS = "quantityHours";
    private static final String COLUMN_TASK_IS_SOLVED = "isSolved";
    private static final String COLUMN_TASK_SPENT_ON_SOLUTION = "spentOnSolution";


    private static final String TABLE_TIMETABLE = "timetable";
    private static final String TABLE_TIMETABLESOLVE = "timetableSolve";


    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table tasks (idTask integer primary key autoincrement, title text, priority text, " +
                "quantityHours integer, isSolved integer, spentOnSolution integer);");

        db.execSQL("create table timetable (idTimetable integer primary key autoincrement, date text, taskId1 integer, " +
                "taskId2 integer, taskId3 integer, taskId4 integer, taskId5 integer, taskId6 integer, taskId7 integer, " +
                "taskId8 integer, taskId9 integer, taskId10 integer, taskId11 integer, taskId12 integer, taskId13 integer, " +
                "taskId14 integer, taskId15 integer);");

        db.execSQL("create table timetableSolve (idTimetableSolve integer primary key autoincrement, date text, taskId1 integer, " +
                "taskId2 integer, taskId3 integer, taskId4 integer, taskId5 integer, taskId6 integer, taskId7 integer, " +
                "taskId8 integer, taskId9 integer, taskId10 integer, taskId11 integer, taskId12 integer, taskId13 integer, " +
                "taskId14 integer, taskId15 integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean queryIsNotCreateTasks(SQLiteDatabase taskDB){
        Cursor cursor = taskDB.rawQuery("select * from tasks where idTask = \"" + "1" + "\"", null);
        Log.d(LOG_TAG, "isNoCreateTasks!");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    public static void queryAddTaskToDatabase(Task task, SQLiteDatabase taskDB) {
        Log.d(LOG_TAG, "addTaskToDatabase!");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TASK_TITLE, task.getTitle());
        contentValues.put(COLUMN_TASK_PRIORITY, task.getPriority());
        contentValues.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        contentValues.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));
        contentValues.put(COLUMN_TASK_SPENT_ON_SOLUTION, 0);
        taskDB.insert(TABLE_TASK, null, contentValues);
    }

    public static void queryGetTasks(SQLiteDatabase taskDB, Context context) {

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        Log.d(LOG_TAG, "queryTaskDBHelper!");

        if (cursor.moveToFirst()) {

            int idTask = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);
            int priorityColIndex = cursor.getColumnIndex(COLUMN_TASK_PRIORITY);
            int quantityHColIndex = cursor.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
            int isSolvedColIndex = cursor.getColumnIndex(COLUMN_TASK_IS_SOLVED);

            do {
                if (cursor.getString(idTask).equals("1")) {
                    continue;
                }
                Task resTask = new Task();
                resTask.setTitle(cursor.getString(titleColIndex));
                resTask.setPriority(cursor.getString(priorityColIndex));
                resTask.setNumberOfHoursToSolve(cursor.getInt(quantityHColIndex));
                resTask.setIsSolved((cursor.getInt(isSolvedColIndex) != 0));
                TaskLab.get(context).addTask(resTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static int queryTaskId(String taskTitle, SQLiteDatabase taskDB) {

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idTask = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);


            do {
                if (cursor.getString(titleColIndex).equals(taskTitle)) {
                    int taskPosition = cursor.getInt(idTask);
                    Log.d(LOG_TAG, "Task position 22 " + taskPosition);
                    cursor.close();
                    return taskPosition;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return 1;
    }

    public static boolean[] queryGetOnDateTimetable(String date, SQLiteDatabase taskDB, Context context) {

        String sqlQuery = "select * from timetable where date = \"" + date + "\"";
        boolean[] tasksSolve = new boolean[15];

        Log.d(LOG_TAG, "Date = " + date);
        Log.d(LOG_TAG, "In queryTaskDBHelper!!! " );

        Cursor cursor = taskDB.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    for (String columnNames : cursor.getColumnNames()) {
                        if (columnNames.matches("idTimetable") || columnNames.matches("date")) {
                            continue;
                        } else {
                            Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + cursor.getString(cursor.getColumnIndex(columnNames)) + "\"", null);
                            if (cursor1 != null) {
                                if (cursor1.moveToFirst()) {
                                    int titleColIndex = cursor1.getColumnIndex(COLUMN_TASK_TITLE);
                                    int priorityColIndex = cursor1.getColumnIndex(COLUMN_TASK_PRIORITY);
                                    int quantityHColIndex = cursor1.getColumnIndex(COLUMN_TASK_QUANTITY_HOURS);
                                    int isSolvedColIndex = cursor1.getColumnIndex(COLUMN_TASK_IS_SOLVED);
                                    Task resTask = new Task();
                                    resTask.setTitle(cursor1.getString(titleColIndex));
                                    resTask.setPriority(cursor1.getString(priorityColIndex));
                                    resTask.setNumberOfHoursToSolve(cursor1.getInt(quantityHColIndex));
                                    resTask.setIsSolved((cursor1.getInt(isSolvedColIndex) != 0));
                                    DayTimetable.get(context).addTask(resTask);
                                }
                            }
                            cursor1.close();
                        }
                    }
                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        Cursor cursor2 = taskDB.rawQuery("select * from timetableSolve where date = \"" + date + "\"", null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    for (String columnNames : cursor2.getColumnNames()) {
                        if (columnNames.matches("idTimetableSolve") || columnNames.matches("date")) {
                            continue;
                        } else {
                            int isSolvedColIndex = cursor2.getColumnIndex(columnNames);
                            boolean isSolved = (cursor2.getInt(isSolvedColIndex) != 0);
                            if (positionInTaskSolve < tasksSolve.length) {
                                if (isSolved) {
                                    tasksSolve[positionInTaskSolve++] = true;
                                } else {
                                    tasksSolve[positionInTaskSolve++] = false;
                                }
                            }
                        }
                    }
                } while (cursor2.moveToNext());
            }
        }
        cursor2.close();
        positionInTaskSolve = 0;
        return tasksSolve;
    }

    public static void queryEditTask(Task task, String editTaskTitle, SQLiteDatabase taskDB) {
        ContentValues taskCV = new ContentValues();

        int editTaskId = 1;

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (cursor.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = cursor.getInt(idColIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        taskCV.put(COLUMN_TASK_TITLE, task.getTitle());
        taskCV.put(COLUMN_TASK_PRIORITY, task.getPriority());
        taskCV.put(COLUMN_TASK_QUANTITY_HOURS, task.getNumberOfHoursToSolve());
        taskCV.put(COLUMN_TASK_IS_SOLVED, (task.isSolved() ? 1 : 0));

        Log.d(LOG_TAG, "queryEditTask " + task.getTitle());

        taskDB.update(TABLE_TASK, taskCV, "idTask = ?", new String[]{Integer.toString(editTaskId)});

    }

    public static void queryEditSolveTask(String dateTimetable, String editTaskTitle, int taskPositionInTimetable, SQLiteDatabase taskDB) {

        ContentValues taskCV = new ContentValues();
        ContentValues timetableCV = new ContentValues();

        int editTaskId = 1;
        int spentOnSolution = 0;

        Cursor cursor = taskDB.query(TABLE_TASK, null, null, null, null, null, null);

        Log.d(LOG_TAG, "queryEditSolveTask ");

        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
            int titleColIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);

            do {
                String edit = (cursor.getString(titleColIndex));
                if (editTaskTitle.equals(edit)) {
                    editTaskId = cursor.getInt(idColIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor1 = taskDB.rawQuery("select * from tasks where idTask = \"" + editTaskId + "\"", null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                int toSolveHours = cursor1.getColumnIndex(COLUMN_TASK_SPENT_ON_SOLUTION);
                spentOnSolution = cursor1.getInt(toSolveHours) + 1;
            }
        }
        cursor1.close();

        taskCV.put(COLUMN_TASK_SPENT_ON_SOLUTION, spentOnSolution);

        taskDB.update(TABLE_TASK, taskCV, "idTask = ?", new String[]{Integer.toString(editTaskId)});

        timetableCV.put("taskId" + taskPositionInTimetable, 1);

        taskDB.update(TABLE_TIMETABLESOLVE, timetableCV, "date = ?", new String[]{dateTimetable});

    }

    public static void queryUpdateTask(String dateTimetable, int taskId, int taskPositionInTimetable, SQLiteDatabase taskDB) {
        ContentValues timetableCV = new ContentValues();

        Log.d(LOG_TAG, "queryEditSolveTask ");

        timetableCV.put("taskId" + taskPositionInTimetable, taskId);

        taskDB.update(TABLE_TIMETABLE, timetableCV, "date = ?", new String[]{dateTimetable});
    }
}
