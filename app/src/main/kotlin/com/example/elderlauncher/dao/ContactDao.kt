package com.example.elderlauncher.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.elderlauncher.model.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY id DESC LIMIT 6")
 suspend fun getTopContacts(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact): Long

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)
}
