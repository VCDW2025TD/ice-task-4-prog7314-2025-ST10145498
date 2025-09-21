package student.projects.memestream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GiphyAdapter
    private lateinit var repository: MemeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MemeRepository(requireContext())

        recyclerView = view.findViewById(R.id.recycler_view_feed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = GiphyAdapter { gif ->
            // Handle gif click if needed
        }
        recyclerView.adapter = adapter

        loadGifs()
        loadLocalMemes()
    }

    private fun loadGifs() {
        lifecycleScope.launch {
            try {
                val response = NetworkModule.giphyService.searchGifs(
                    apiKey = "GlVGYHkr3WSBnllca54iNt0yFbjz7L65",
                    query = "meme",
                    limit = 25
                )
                if (response.isSuccessful) {
                    response.body()?.let { giphyResponse ->
                        adapter.updateGifs(giphyResponse.data)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load trending memes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadLocalMemes() {
        lifecycleScope.launch {
            repository.getAllMemes().collect { memes ->
                // You can combine these with GIPHY results or show separately
                // For now, we'll just show GIPHY results
            }
        }
    }
}