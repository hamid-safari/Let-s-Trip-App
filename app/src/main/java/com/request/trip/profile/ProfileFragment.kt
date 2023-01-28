package com.request.trip.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.request.trip.R
import com.request.trip.databinding.FragmentProfileBinding
import com.request.trip.language.ChangeLanguageFragment
import com.request.trip.language.Language
import com.request.trip.language.OnNewLanguageSetListener
import com.request.trip.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment(R.layout.fragment_profile), View.OnClickListener, OnNewLanguageSetListener,
    OnLogoutClickListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val userUID = PrefManager.getUID()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)


        // user login? if not redirect to login page
        if (auth.currentUser == null) {
            getMainActivity().navigateToLogin()
            return
        }

        binding.btnOpenEditName.setOnClickListener(this)
        binding.btnLanguageProfile.setOnClickListener(this)
        binding.btnEditAvatar.setOnClickListener(this)

        binding.txtNameProfile.tag = binding.txtNameProfile.keyListener
        binding.txtNameProfile.keyListener = null

        binding.txtLanguage.text = getLanguageStr()

        db.collection(USERS_COLLECTION).document(userUID!!)
            .get()
            .addOnSuccessListener { document ->
                val thisUser = document.toObject(User::class.java)
                getMainActivity().setCurrentUser(thisUser)
                Glide.with(this).load(thisUser?.avatar)
                    .circleCrop() // circle image
                    .into(binding.imgAvatar)
                binding.txtNameProfile.setText(thisUser?.name)
            }

        viewModel.isSuccessfully.observe(viewLifecycleOwner) {
            if (it) {
                showToast(getString(R.string.name_changed))
            }
        }

        binding.btnLogout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogout ->
                LogoutDialogFragment(this).show(childFragmentManager, "LOGOUT_DIALOG")

            R.id.btnOpenEditName -> {
                if (binding.txtNameProfile.keyListener == null) {
                    binding.txtNameProfile.toggleEditable(requireContext(), true)
                } else {
                    binding.txtNameProfile.toggleEditable(requireContext(), false)
                    val newName = binding.txtNameProfile.getValue()
                    val userUID = getMainActivity().getCurrentUser()?.uid
                    changeUserName(userUID, newName)
                }
            }

            R.id.btnEditAvatar -> {
                if (hasStoragePermission())
                    selectImageIntent()
                else
                    getStoragePermission()
            }

            R.id.btnLanguageProfile -> {
                val language = if (PrefManager.getLocale() == LOCALE_ENGLISH)
                    Language(LANGUAGE_ENGLISH, LOCALE_ENGLISH)
                else
                    Language(LANGUAGE_GERMAN, LOCALE_GERMAN)

                ChangeLanguageFragment(language, this).show(childFragmentManager, "")
            }
        }
    }

    private fun changeUserName(userUID: String?, newName: String) {
        viewModel.changeName(userUID, newName)
    }


    private fun getStoragePermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 156)
    }

    private fun selectImageIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 149)
    }

    private fun hasStoragePermission() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            149 -> {
                val imageUri = data?.data ?: return
                val avatarRef = storageRef.child("images/avatar").child(userUID ?: "")
                avatarRef.putFile(imageUri).addOnSuccessListener {
                    avatarRef.downloadUrl.addOnSuccessListener {
                        db.collection(USERS_COLLECTION).document(userUID ?: "").update("avatar", it.toString())
                        Glide.with(this).load(imageUri).circleCrop().into(binding.imgAvatar)
                    }
                }.addOnFailureListener {
                    showToast(it.message)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
        when (requestCode) {
            156 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    selectImageIntent()
                else {
                    showToast(getString(R.string.confirm_permission_text))
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    override fun onNewLanguageSet(language: Language) {
        if (PrefManager.isLocaleChanges(language.locale)) {
            requireActivity().setLocale(language.locale)
            requireActivity().restartApp()
        }
    }

    override fun onLogoutClick() {
        auth.signOut()
        getMainActivity().navigateToLogin()
    }
}