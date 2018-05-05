package com.vodafone.frt.v2.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Sara solution on 01-02-2018.
 */

@Database(entities = {LocationEntity.class}, version = 1, exportSchema = false)
public abstract class DbService extends RoomDatabase {
    public static final String DATABASE_NAME = "tracker-db";

    public abstract LocationDao daoAccess();
}
