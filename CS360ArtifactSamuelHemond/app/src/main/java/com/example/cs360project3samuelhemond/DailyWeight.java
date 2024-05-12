package com.example.cs360project3samuelhemond;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

import java.time.LocalDate;

//Class for storing daily recorded weights
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id",
        childColumns = "user_id", onDelete = CASCADE))
public class DailyWeight {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long mId;

    @ColumnInfo(name = "weight")
    private int mWeight;

    @ColumnInfo(name = "date")
    private LocalDate mDate;

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

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        this.mDate = date;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long user_Id) {
        this.mUserId = user_Id;
    }
}
