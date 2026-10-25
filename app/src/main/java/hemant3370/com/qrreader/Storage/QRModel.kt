package hemant3370.com.qrreader.Storage

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by hemantsingh on 22/03/18.
 */
@Entity(tableName = "QRCodes")
data class QRModel(@PrimaryKey var text: String, var uri: String, var createdAt: String) {
    constructor():this("","","")
}