package hemant3370.com.qrreader.Storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by hemantsingh on 22/03/18.
 */

@Database(entities = arrayOf(QRModel::class), version = 1)
abstract class QRDatabase : RoomDatabase() {
    abstract fun daoAccess(): DaoAccess

    companion object {
        private var INSTANCE: QRDatabase? = null

        fun getInstance(context: Context): QRDatabase? {
            if (INSTANCE == null) {
                synchronized(QRDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QRDatabase::class.java, "qr.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}