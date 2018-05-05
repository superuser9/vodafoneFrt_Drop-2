package com.vodafone.frt.v2.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Anurag Gupta on 04-01-2018.
 */

public class DateTimeConverter {
    @TypeConverter
    public static Date toDateTime(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
