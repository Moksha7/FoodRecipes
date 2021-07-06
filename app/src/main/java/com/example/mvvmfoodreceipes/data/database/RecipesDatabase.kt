package com.example.mvvmfoodreceipes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvmfoodreceipes.data.database.entities.FavouritesEntity
import com.example.mvvmfoodreceipes.data.database.entities.FoodJokeEntity
import com.example.mvvmfoodreceipes.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavouritesEntity::class, FoodJokeEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

}