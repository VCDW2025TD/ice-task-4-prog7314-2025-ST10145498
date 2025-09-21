package student.projects.memestream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GiphyAdapter(private val onItemClick: (GiphyGif) -> Unit) : RecyclerView.Adapter<GiphyAdapter.GifViewHolder>() {

    private var gifs = listOf<GiphyGif>()

    fun updateGifs(newGifs: List<GiphyGif>) {
        gifs = newGifs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return GifViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(gifs[position])
    }

    override fun getItemCount() = gifs.size

    inner class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_gif)
        private val titleText: TextView = itemView.findViewById(R.id.text_title)

        fun bind(gif: GiphyGif) {
            titleText.text = gif.title
            Glide.with(itemView.context)
                .load(gif.images.fixedHeight.url)
                .into(imageView)

            itemView.setOnClickListener { onItemClick(gif) }
        }
    }
}