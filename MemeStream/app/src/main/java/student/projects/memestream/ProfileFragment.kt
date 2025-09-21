package student.projects.memestream

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemeAdapter
    private lateinit var repository: MemeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MemeRepository(requireContext())

        recyclerView = view.findViewById(R.id.recycler_view_profile)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MemeAdapter { meme ->
            shareMeme(meme)
        }
        recyclerView.adapter = adapter

        loadUserMemes()
    }

    private fun loadUserMemes() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            lifecycleScope.launch {
                repository.getMemesByUser(user.uid).collect { memes ->
                    adapter.updateMemes(memes)
                }
            }
        }
    }

    private fun shareMeme(meme: Meme) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "${meme.caption}\n${meme.imageUrl}")
            putExtra(Intent.EXTRA_SUBJECT, "Check out this meme from MemeStream!")
        }
        startActivity(Intent.createChooser(shareIntent, "Share meme"))
    }
}