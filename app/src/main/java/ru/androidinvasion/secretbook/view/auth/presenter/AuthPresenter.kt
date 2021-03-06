package ru.androidinvasion.secretbook.view.auth.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.androidinvasion.secretbook.App
import ru.androidinvasion.secretbook.R
import ru.androidinvasion.secretbook.di.auth.AuthModule
import ru.androidinvasion.secretbook.interactor.auth.IAuthInteractor
import ru.androidinvasion.secretbook.utils.Constants
import ru.androidinvasion.secretbook.view.auth.ui.IAuthView
import javax.inject.Inject

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project SecretBook
 * @date 06.03.18
 */

//TODO save login and password here
@InjectViewState
class AuthPresenter : MvpPresenter<IAuthView>() {
    @Inject
    lateinit var authInteractor: IAuthInteractor
    private val disposable = CompositeDisposable()

    init {
        App.appComponent.plus(AuthModule()).inject(this)
    }

    fun onClickLogin(login: String, password: String) {
        if (!validatePasswordAndLogin(login, password)) {
            return
        }
        viewState.onLoading(true)
        disposable.addAll(authInteractor.login(login, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.onLoading(false)
                }, {
                    viewState.onError("TODO ME")
                }))
    }

    private fun validatePasswordAndLogin(login: String, password: String): Boolean {
        var loginValid = true
        var passwordValid = true
        if (login.isEmpty()) {
            viewState.showLoginError(R.string.auth_activity_login_empty)
            loginValid = false
        }
        if (!Constants.LOGIN_PATTERN.matcher(login).matches()) {
            viewState.showLoginError(R.string.auth_activity_login_matcher)
            loginValid = false
        }
        if (loginValid) {
            viewState.hideLoginError()
        }
        if (password.isEmpty()) {
            viewState.showPasswordError(R.string.auth_activity_password_empty)
            passwordValid = false
        }
        if (password.length < Constants.PASSWORD_MAX_LENGHT) {
            viewState.showPasswordError(R.string.auth_activity_password_matcher)
            passwordValid = false
        }
        if (passwordValid) {
            viewState.hidePasswordError()
        }
        return passwordValid && loginValid
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}