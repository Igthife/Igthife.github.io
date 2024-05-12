package com.example.cs360project3samuelhemond;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cs360project3samuelhemond.DailyWeight;
import com.example.cs360project3samuelhemond.User;
import com.example.cs360project3samuelhemond.GoalWeight;

//database for application using dao for the three databases being used
@Database(entities = {User.class, DailyWeight.class, GoalWeight.class}, version = 2)
@TypeConverters({LocalDateConverter.class})
public abstract class WeightDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract GoalWeightDao goalWeightDao();
    public abstract DailyWeightDao dailyWeightDao();

}