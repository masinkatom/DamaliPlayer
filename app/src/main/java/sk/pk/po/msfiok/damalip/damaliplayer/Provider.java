package sk.pk.po.msfiok.damalip.damaliplayer;

import android.provider.BaseColumns;

public interface Provider {
    interface Music extends BaseColumns{
        public static final String TABLE_NAME = "song_fav";
        public static final String SONG_NAME = "song_name";
        public static final String SONG_LENGTH = "song_length";
        public static final String SONG_USAGE = "song_usage";
        public static final String SONG_DATE = "song_date";
    }

}
