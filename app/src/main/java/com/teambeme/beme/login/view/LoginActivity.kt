package com.teambeme.beme.login.view

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.teambeme.beme.R
import com.teambeme.beme.base.BindingActivity
import com.teambeme.beme.data.remote.datasource.LoginDataSourceImpl
import com.teambeme.beme.data.remote.singleton.BeMeAuthPreference
import com.teambeme.beme.data.remote.singleton.RetrofitObjects
import com.teambeme.beme.databinding.ActivityLoginBinding
import com.teambeme.beme.login.repository.LoginRepositoryImpl
import com.teambeme.beme.login.viewmodel.LoginViewModel
import com.teambeme.beme.login.viewmodel.LoginViewModelFactory
import com.teambeme.beme.main.view.MainActivity
import com.teambeme.beme.signup.view.SignUpActivity
import com.teambeme.beme.util.KeyboardVisibilityUtils
import com.teambeme.beme.util.StatusBarUtil

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var visibleDisplayFrameHeight = 0
    private var keyboardHeight = 0
    private val loginViewModelFactory =
        LoginViewModelFactory(LoginRepositoryImpl(LoginDataSourceImpl(RetrofitObjects.getLoginService())))
    private val loginViewModel: LoginViewModel by viewModels { loginViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight, visibleDisplayFrameHeight ->
                this.keyboardHeight = keyboardHeight
                this.visibleDisplayFrameHeight = visibleDisplayFrameHeight
                val animator = setValueChangeAnimator(0, visibleDisplayFrameHeight - keyboardHeight)
                animator.addUpdateListener { updatedAnimation ->
                    binding.constraintLoginView
                        .setPaddingBottomAnimator(updatedAnimation)
                }
            },
            onHideKeyboard = {
                val animator = setValueChangeAnimator(visibleDisplayFrameHeight - keyboardHeight, 0)
                animator.addUpdateListener { updatedAnimation ->
                    binding.constraintLoginView
                        .setPaddingBottomAnimator(updatedAnimation)
                }
            }
        )
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        StatusBarUtil.setStatusBar(this, resources.getColor(R.color.white, null))
        loginViewModel.responseValue.observe(this) { data ->
            if (data != null) {
                if (data.success) {
                    BeMeAuthPreference.userToken = data.data!!.token!!
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Toast.makeText(this, "????????? ??????", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "????????? ?????? ${data.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "???????????? ?????????????????????. ?????? ??????????????????", Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                binding.linearLoginError.visibility = View.VISIBLE
            }
        }
        signUpButtonClickListener()
        setChangeEndIconFromPassword()
        setEndIconModeObserve()
    }

    private fun signUpButtonClickListener() {
        binding.btnLoginSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setValueChangeAnimator(from: Int, to: Int): ValueAnimator {
        return ValueAnimator.ofInt(from, to).apply {
            duration = 400
            start()
        }
    }

    private fun ConstraintLayout.setPaddingBottomAnimator(valueAnimator: ValueAnimator) {
        this.setPadding(
            0,
            0,
            0,
            valueAnimator.animatedValue as Int
        )
    }

    private fun setChangeEndIconFromPassword() {
        binding.txtlayoutLoginPassword.setEndIconOnClickListener {
            loginViewModel.setShowPassword()
        }
    }

    private fun setEndIconModeObserve() {
        loginViewModel.showPassword.observe(this) { showPassword ->
            showPassword?.let {
                if (showPassword) {
                    binding.txtlayoutLoginPassword.setEndIconDrawable(R.drawable.ic_hide_password)
                    binding.editxtLoginPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    binding.txtlayoutLoginPassword.setEndIconDrawable(R.drawable.ic_show_password)
                    binding.editxtLoginPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }
}