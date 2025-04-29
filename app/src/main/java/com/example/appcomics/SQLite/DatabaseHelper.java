package com.example.appcomics.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.appcomics.Model.ChapContent;

import java.util.ArrayList;
import java.util.List;

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
    public static final String COLUMN_TAC_GIA = "tacgia";
    public static final String COLUMN_VIEWS = "views";
    public static final String COLUMN_IMAGES = "image";
    public static final String COLUMN_AUDIO_FILE = "audiofile";

    // SQL query để tạo bảng
    private static final String CREATE_DOWNLOADS_TABLE = "CREATE TABLE " + TABLE_DOWNLOADS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MANGAID + " INTEGER,"
            + COLUMN_CHAPTERID + " INTEGER,"
            + COLUMN_CHAPTER_TITLE + " TEXT,"
            + COLUMN_TAC_GIA + " TEXT,"
            + COLUMN_VIEWS + " INTEGER,"
            + COLUMN_IMAGES + " TEXT,"
            + COLUMN_AUDIO_FILE + " BLOB,"
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

        // Kiểm tra xem chương đã tồn tại chưa
        String checkQuery = "SELECT * FROM " + TABLE_DOWNLOADS + " WHERE " + COLUMN_MANGAID + " = ? AND " + COLUMN_CHAPTERID + " = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(mangaid), String.valueOf(chapterid)});

        if (cursor.getCount() == 0) {
            // Nếu chưa tồn tại, thêm chương mới
            ContentValues values = new ContentValues();
            values.put(COLUMN_MANGAID, mangaid);
            values.put(COLUMN_CHAPTERID, chapterid);
            values.put(COLUMN_CHAPTER_TITLE, chapterTitle);
            values.put(COLUMN_CONTENT, content);
            db.insert(TABLE_DOWNLOADS, null, values);
        }
        cursor.close();
        db.close();
    }

    public List<ChapContent> getDownloadedChapters(int mangaid) {
        List<ChapContent> chapters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOWNLOADS,
                new String[]{COLUMN_CHAPTERID, COLUMN_CHAPTER_TITLE, COLUMN_CONTENT},
                COLUMN_MANGAID + " = ?",
                new String[]{String.valueOf(mangaid)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int chapterId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAPTERID));
                String chapterTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAPTER_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                chapters.add(new ChapContent(chapterId, chapterTitle, content));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapters;
    }
}
