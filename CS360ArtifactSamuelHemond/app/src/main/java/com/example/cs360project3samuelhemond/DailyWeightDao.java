package com.example.cs360project3samuelhemond;

import androidx.room.*;

import java.time.LocalDate;
import java.util.List;

//Data access object for working with dailyWeights objects within ROOM
@Dao
public interface DailyWeightDao {
    @Query("SELECT * FROM DailyWeight WHERE id = :id")
    DailyWeight getDailyWeightById(long id);

    @Query("SELECT * FROM DailyWeight WHERE user_id = :user_id")
    List<DailyWeight> getDailyWeightByUserId(long user_id);

    @Query("SELECT * FROM DailyWeight WHERE user_id = :user_id AND date = :localDate")
    DailyWeight getDailyWeightByUserIDAndDate(long user_id, LocalDate localDate);

    @Query("SELECT * FROM DailyWeight ORDER BY date COLLATE NOCASE")
    List<DailyWeight> getAllDailyWeights();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addDailyWeight(DailyWeight dailyWeight);

    @Update
    void updateDailyWeight(DailyWeight dailyWeight);

    @Delete
    void deleteDailyWeight(DailyWeight dailyWeight);
}
