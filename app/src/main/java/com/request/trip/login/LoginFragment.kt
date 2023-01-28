package com.request.trip.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.request.trip.R
import com.request.trip.databinding.FragmentLoginBinding
import com.request.trip.utils.GOOGLE_LOGIN_REQUEST_CODE
import com.request.trip.utils.showToast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var callbackManager: CallbackManager
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup facebook login
        FacebookSdk.sdkInitialize(requireContext())
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null)
                        viewModel.loginWithFacebook(result.accessToken.token)
                }

                override fun onCancel() {
                    showToast(getString(R.string.login_canceled))
                }

                override fun onError(error: FacebookException?) {
                    showToast(error?.message)
                }
            })

        //Setup google login
        val options =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build()
        client = GoogleSignIn.getClient(requireContext(), options)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLoginGoogle.setOnClickListener(this)
        binding.btnLoginFacebook.setOnClickListener(this)

        viewModel.isSuccessFull.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(R.id.action_login_to_profile)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLoginGoogle -> {
                client.signOut()
                startActivityForResult(client.signInIntent, GOOGLE_LOGIN_REQUEST_CODE)
            }

            R.id.btnLoginFacebook -> {
                LoginManager.getInstance().logOut()
                LoginManager.getInstance().logInWithReadPermissions(
                    this,
                    listOf("email", "public_profile", "user_friends")
                ) // List of permissions}
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_LOGIN_REQUEST_CODE -> {
                try {
                    val tokenId =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                            .getResult(ApiException::class.java)?.idToken
                    viewModel.loginWithGoogle(tokenId)
                } catch (e: Exception) {
                    showToast(e.message)
                }
            }

            else ->
                callbackManager.onActivityResult(requestCode, resultCode, data) //For facebook login
        }
    }

    //Set null to binding when no need
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}