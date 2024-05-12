package com.example.cs360project3samuelhemond;

import androidx.room.TypeConverter;

import java.time.LocalDate;

//localDate converter class for use within android persistence room
public class LocalDateConverter {
    @TypeConverter
    public static LocalDate toDate(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            return LocalDate.parse(dateString);
        }
    }

    @TypeConverter
    public static String toDateString(LocalDate date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }
}
