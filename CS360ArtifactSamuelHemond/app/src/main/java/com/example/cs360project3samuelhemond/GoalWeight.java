package com.example.cs360project3samuelhemond;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

//Class for storing users goal weights
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id",
        childColumns = "user_id", onDelete = CASCADE))
public class GoalWeight {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long mId;

    @ColumnInfo(name = "weight")
    private int mWeight;

    @ColumnInfo(name = "user_id")
    private Long mUserId;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        this.mWeight = weight;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long user_Id) {
        this.mUserId = user_Id;
    }
}
