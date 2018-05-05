package com.vodafone.frt.v2.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by SM-002 on 01-02-2018.
 */
@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(LocationEntity locationEntity);

    @Query("SELECT * FROM locationentity WHERE mobileTime > :time ORDER BY mobileTime ASC LIMIT 100")
    List<LocationEntity> fetchData(long time);
}
