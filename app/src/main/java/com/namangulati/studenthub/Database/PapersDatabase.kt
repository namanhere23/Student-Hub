package com.namangulati.studenthub.Database;

import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase;
import android.content.Context
import com.namangulati.studenthub.Dao.PapersDao;
import com.namangulati.studenthub.modelsRoom.PapersModelRoom;
import kotlin.jvm.Volatile;

@Database(entities = [PapersModelRoom::class], version = 1, exportSchema = false)
abstract class PapersDatabase : RoomDatabase() {
    abstract val dao: PapersDao
    companion object {
        @Volatile
        private var INSTANCE: PapersDatabase? = null
        fun getInstance(context: Context):PapersDatabase{
            synchronized(this){
                return INSTANCE ?:Room.databaseBuilder(
                    context.applicationContext,
                    PapersDatabase::class.java,
                    "papers_db"
                ).build().also{
                    INSTANCE=it
                }
            }
        }
    }

}



