package student.projects.memestream

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateMemeFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var editCaption: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnUpload: Button
    private lateinit var repository: MemeRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedImageUri: Uri? = null
    private var currentLocation: Location? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)
            btnUpload.isEnabled = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_meme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MemeRepository(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        imageView = view.findViewById(R.id.image_selected)
        editCaption = view.findViewById(R.id.edit_caption)
        btnSelectImage = view.findViewById(R.id.btn_select_image)
        btnUpload = view.findViewById(R.id.btn_upload)

        btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnUpload.setOnClickListener {
            uploadMeme()
        }

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }

    private fun uploadMeme() {
        val caption = editCaption.text.toString().trim()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (caption.isEmpty() || selectedImageUri == null || currentUser == null) {
            Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val lat = currentLocation?.latitude ?: 0.0
        val lng = currentLocation?.longitude ?: 0.0

        val memePost = MemePost(
            userId = currentUser.uid,
            imageUrl = selectedImageUri.toString(), // In a real app, upload to Firebase Storage first
            caption = caption,
            lat = lat,
            lng = lng,
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        lifecycleScope.launch {
            val success = repository.insertMeme(memePost)
            if (success) {
                Toast.makeText(context, "Meme uploaded successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Meme saved offline - will sync when online", Toast.LENGTH_SHORT).show()
            }

            // Reset form
            editCaption.text.clear()
            imageView.setImageResource(android.R.color.transparent)
            selectedImageUri = null
            btnUpload.isEnabled = false
        }
    }
}
