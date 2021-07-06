package com.example.mvvmfoodreceipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mvvmfoodreceipes.models.Result
import com.example.mvvmfoodreceipes.utils.Constants.FAVOURITE_RECIPES_TABLE

@Entity(tableName = FAVOURITE_RECIPES_TABLE)
class FavouritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)