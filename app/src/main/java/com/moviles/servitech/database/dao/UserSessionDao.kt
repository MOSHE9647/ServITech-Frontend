package com.moviles.servitech.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.servitech.common.Constants.SESSION_TABLE
import com.moviles.servitech.database.entities.user.UserSessionEntity

@Dao
interface UserSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(userSession: UserSessionEntity)

    @Query("SELECT * FROM $SESSION_TABLE LIMIT 1")
    suspend fun getSession(): UserSessionEntity?

    @Query("DELETE FROM $SESSION_TABLE")
    suspend fun clearSession()

}