package hemant3370.com.qrreader.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.regex.Pattern

/**
 * Created by hemantsingh on 22/03/18.
 */

class Utils {
    companion object {
        fun openIfUrl(text: String?, context: Context){
            if (text != null && checkForURL(text)) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(text)
                context.startActivity(i)
            }
        }
        fun checkForURL(text: String?) : Boolean {
            val URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
            val p = Pattern.compile(URL_REGEX)
            val m = p.matcher(text)//replace with string to compare
            return m.find()
        }
    }
}