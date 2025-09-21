package student.projects.memestream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MemeAdapter(private val onShareClick: (Meme) -> Unit) : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    private var memes = listOf<Meme>()

    fun updateMemes(newMemes: List<Meme>) {
        memes = newMemes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meme, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        holder.bind(memes[position])
    }

    override fun getItemCount() = memes.size

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_meme)
        private val captionText: TextView = itemView.findViewById(R.id.text_caption)
        private val timestampText: TextView = itemView.findViewById(R.id.text_timestamp)
        private val shareButton: Button = itemView.findViewById(R.id.btn_share)
        private val syncStatus: TextView = itemView.findViewById(R.id.text_sync_status)

        fun bind(meme: Meme) {
            captionText.text = meme.caption
            timestampText.text = meme.timestamp
            syncStatus.text = if (meme.synced) "Synced" else "Pending sync"

            Glide.with(itemView.context)
                .load(meme.imageUrl)
                .into(imageView)

            shareButton.setOnClickListener { onShareClick(meme) }
        }
    }
}