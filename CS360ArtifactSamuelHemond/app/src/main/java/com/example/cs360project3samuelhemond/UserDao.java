package com.example.cs360project3samuelhemond;

import androidx.room.*;
import java.util.List;

//Data access object for working with User objects within ROOM
@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE id = :id")
    User getUserById(long id);

    @Query("SELECT * FROM User WHERE userName = :userName")
    User getUserByName(String userName);

    @Query("SELECT * FROM User WHERE userName = :userName AND userPassword = :password")
    User getUser(String userName, String password);

    @Query("SELECT * FROM User ORDER BY userName COLLATE NOCASE")
    List<User> getAllUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
