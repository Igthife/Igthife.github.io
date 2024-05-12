package com.example.cs360project3samuelhemond;

import androidx.room.*;
import com.example.cs360project3samuelhemond.GoalWeight;
import java.util.List;

//Data access object for working with goalWeight objects within ROOM
@Dao
public interface GoalWeightDao {
    @Query("SELECT * FROM GoalWeight WHERE id = :id")
    GoalWeight getGoalWeightById(long id);

    @Query("SELECT * FROM GoalWeight WHERE user_id = :user_id")
    GoalWeight getGoalWeightByUserId(long user_id);

    @Query("SELECT * FROM GoalWeight ORDER BY user_id COLLATE NOCASE")
    List<GoalWeight> getAllGoalWeights();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addGoalWeight(GoalWeight goalWeight);

    @Update
    void updateGoalWeight(GoalWeight goalWeight);

    @Delete
    void deleteGoalWeight(GoalWeight goalWeight);
}
