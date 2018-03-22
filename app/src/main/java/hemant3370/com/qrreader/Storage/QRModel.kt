package hemant3370.com.qrreader.Storage

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by hemantsingh on 22/03/18.
 */
@Entity(tableName = "QRCodes")
data class QRModel(@PrimaryKey var text: String, var uri: String, var createdAt: String) {
    constructor():this("","","")
}