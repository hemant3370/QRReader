package hemant3370.com.qrreader.Adapters

import android.net.Uri
import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Storage.QRModel
import android.widget.ImageView
import android.widget.TextView



/**
 * Created by hemantsingh on 22/03/18.
 */

class PastQRcodeAdapter(val qrData: List<QRModel>, val listener: (QRModel) -> Unit) : RecyclerView.Adapter<PastQRcodeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastQRcodeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.saveditem, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PastQRcodeAdapter.ViewHolder, position: Int) {
        holder.bindItems(qrData[position], listener)
    }

    override fun getItemCount(): Int {
        return qrData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(qrData: QRModel, listener: (QRModel) -> Unit) = with(itemView) {

            itemView.findViewById<TextView>(R.id.textViewText).text = qrData.text
            val uri = Uri.parse(qrData.uri)
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(itemView.context.contentResolver, uri)
                if (bitmap != null) { itemView.findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap) }
            }
            itemView.findViewById<TextView>(R.id.textViewCreatedAt).text = qrData.createdAt
            itemView.setOnClickListener { listener(qrData) }
        }
    }
}