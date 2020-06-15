package sk.pk.po.msfiok.damalip.damaliplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static sk.pk.po.msfiok.damalip.damaliplayer.Defaults.DEFAULT_CURSOR_FACTORY;
import static sk.pk.po.msfiok.damalip.damaliplayer.Provider.Music.SONG_DATE;
import static sk.pk.po.msfiok.damalip.damaliplayer.Provider.Music.SONG_LENGTH;
import static sk.pk.po.msfiok.damalip.damaliplayer.Provider.Music.SONG_NAME;
import static sk.pk.po.msfiok.damalip.damaliplayer.Provider.Music.SONG_USAGE;
import static sk.pk.po.msfiok.damalip.damaliplayer.Provider.Music.TABLE_NAME;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "music";
    public static final int DATABASE_VERSION = 1;
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, DEFAULT_CURSOR_FACTORY, DATABASE_VERSION);
    }

   // @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql());
    }

    private void insertSampleEntry(SQLiteDatabase db, String song_name, String usage, int length, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_NAME, song_name);
        contentValues.put(SONG_USAGE, usage);
        contentValues.put(SONG_LENGTH, length);
        contentValues.put(SONG_DATE, date);
        db.insert(Provider.Music.TABLE_NAME, Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    private String createTableSql() {
        String sqlTemplate = "CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "%s TEXT,"
                + "%s TEXT,"
                + "%s INTEGER"
                + ")";
        return String.format(sqlTemplate, TABLE_NAME, _ID, SONG_NAME, SONG_USAGE, SONG_LENGTH);
    }

  //  @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
