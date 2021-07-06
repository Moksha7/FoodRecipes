package com.example.mvvmfoodreceipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mvvmfoodreceipes.models.FoodRecipe
import com.example.mvvmfoodreceipes.utils.Constants.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}