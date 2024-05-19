package com.example.cs360project3samuelhemond;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.Update;

import com.example.cs360project3samuelhemond.DailyWeight;
import com.example.cs360project3samuelhemond.User;
import com.example.cs360project3samuelhemond.GoalWeight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//repository for the database
public class WeightRepository {
    private static WeightRepository mWeightRepository;
    private final UserDao mUserDao;
    private final GoalWeightDao mGoalWeightDao;
    private  final DailyWeightDao mDailyWeightDao;
    private static final String TAG = "WeightRepo";

    //singleton pattern with getInstance public and constructor private
    public static WeightRepository getInstance(Context context) {
        if (mWeightRepository == null) {
            mWeightRepository = new WeightRepository(context); //FIXME context.getApplicationContext()
        }
        return mWeightRepository;
    }

    //private constructor
    private WeightRepository(Context context) {
        WeightDatabase database = Room.databaseBuilder(context, WeightDatabase.class, "weight6.db")
                .allowMainThreadQueries()
                .build();

        //setup Dao's
        mUserDao = database.userDao();
        mGoalWeightDao = database.goalWeightDao();
        mDailyWeightDao = database.dailyWeightDao();

        if (mUserDao.getAllUsers().isEmpty()) {
            Log.i(TAG, "WeightRepository Empty");
        }
    }

    //Methods to use Dao methods from the repository
    public User getUserById(long id){   //user methods
        return mUserDao.getUserById(id);
    }

    public User getUserByName(String userName){
        return mUserDao.getUserByName(userName);
    }

    public User getUser(String userName, String password){
        return mUserDao.getUser(userName, password);
    }

    public List<User> getAllUsers(){
        return mUserDao.getAllUsers();
    }

    public long addUser(User user){
        Log.i(TAG, "addUser: ");
        return mUserDao.addUser(user);
    }

    public void updateUser(User user){
        mUserDao.updateUser(user);
    }

    public void deleteUser(User user){
        mUserDao.deleteUser(user);
    }

    public GoalWeight getGoalWeightById(long id){   //goal weight methods
        return mGoalWeightDao.getGoalWeightById(id);
    }

    public GoalWeight getGoalWeightByUserId(long id){
        return mGoalWeightDao.getGoalWeightByUserId(id);
    }

    public List<GoalWeight> getAllGoalWeights(){
        return mGoalWeightDao.getAllGoalWeights();
    }

    public long addGoalWeight(GoalWeight goalWeight){
        Log.i(TAG, "add weight: ");
        return mGoalWeightDao.addGoalWeight(goalWeight);
    }

    public void updateGoalWeight(GoalWeight goalWeight){
        mGoalWeightDao.updateGoalWeight(goalWeight);
    }

    public void deleteGoalWeight(GoalWeight goalWeight){
        mGoalWeightDao.deleteGoalWeight(goalWeight);
    }

    public DailyWeight getDailyWeightById(long id){ //daily weight methods
        return mDailyWeightDao.getDailyWeightById(id);
    }

    public List<DailyWeight> getDailyWeightByUserId(long user_id){
        return mDailyWeightDao.getDailyWeightByUserId( user_id);
    }

    DailyWeight getDailyWeightByUserIDAndDate(long user_id, LocalDate localDate){
        return mDailyWeightDao.getDailyWeightByUserIDAndDate(user_id, localDate);
    }

    public List<DailyWeight> getAllDailyWeights(){
        return mDailyWeightDao.getAllDailyWeights();
    }

    public long addDailyWeight(DailyWeight dailyWeight){
        return mDailyWeightDao.addDailyWeight(dailyWeight);
    }

    public void updateDailyWeight(DailyWeight dailyWeight){
        mDailyWeightDao.updateDailyWeight(dailyWeight);
    }


    public void deleteDailyWeight(DailyWeight dailyWeight){
        mDailyWeightDao.deleteDailyWeight(dailyWeight);
    }
}
