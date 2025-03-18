package com.example.appcomics.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu
    private static final String DATABASE_NAME = "manga_app.db";

    // Phiên bản cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;

    // Cấu trúc bảng download
    public static final String TABLE_DOWNLOADS = "downloads";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MANGAID = "mangaid";
    public static final String COLUMN_CHAPTERID = "chapterid";
    public static final String COLUMN_CHAPTER_TITLE = "chapter_title";
    public static final String COLUMN_CONTENT = "content";

    // SQL query để tạo bảng
    private static final String CREATE_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_DOWNLOADS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MANGAID + " INTEGER,"
            + COLUMN_CHAPTERID + " INTEGER,"
            + COLUMN_CHAPTER_TITLE + " TEXT,"
            + COLUMN_CONTENT + " TEXT)";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng khi cơ sở dữ liệu lần đầu được tạo
        db.execSQL(CREATE_DOWNLOADS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu có thay đổi phiên bản, bạn có thể thực hiện việc nâng cấp cơ sở dữ liệu ở đây
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);
        onCreate(db);
    }

    // Phương thức để thêm một chapter vào bảng downloads
    public void addDownload(int mangaid, int chapterid, String chapterTitle, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MANGAID, mangaid);
        values.put(COLUMN_CHAPTERID, chapterid);
        values.put(COLUMN_CHAPTER_TITLE, chapterTitle);
        values.put(COLUMN_CONTENT, content);

        // Chèn dòng mới vào bảng
        db.insert(TABLE_DOWNLOADS, null, values);
        db.close();
    }
}
