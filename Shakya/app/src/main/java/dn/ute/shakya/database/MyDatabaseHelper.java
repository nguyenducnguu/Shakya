package dn.ute.shakya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Shakya";

    public static final String TABLE_LESSON = "Lesson";
    public static final String COLUMN_LESSON_ID = "id";
    public static final String COLUMN_LESSON_TITLE = "title";

    public static final String TABLE_WORD = "Word";
    public static final String COLUMN_WORD_ID = "id";
    public static final String COLUMN_WORD_LESSON_ID = "lessonId";
    public static final String COLUMN_WORD_CONTENT = "content";

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptCreateTableLesson = "CREATE TABLE " + TABLE_LESSON
                + "(" + COLUMN_LESSON_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LESSON_TITLE + " TEXT" + ")";
        db.execSQL(scriptCreateTableLesson);

        String scriptCreateTableWord = "CREATE TABLE " + TABLE_WORD
                + "(" + COLUMN_WORD_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_WORD_LESSON_ID + " INTEGER,"
                + COLUMN_WORD_CONTENT + " TEXT" + ")";
        db.execSQL(scriptCreateTableWord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);

        // Recreate
        onCreate(db);
    }
}