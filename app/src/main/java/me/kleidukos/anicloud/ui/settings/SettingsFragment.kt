package me.kleidukos.anicloud.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.room.user.User
import java.lang.Exception
import kotlin.streams.toList

class SettingsFragment : Fragment() {

    private var user: User? = null

    private lateinit var loginContent: LinearLayout
    private lateinit var loggedContent: LinearLayout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var login: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        loginContent = root.findViewById(R.id.login_content)
        loggedContent = root.findViewById(R.id.logged_content)
        loginEmail = root.findViewById(R.id.login_email)
        loginPassword = root.findViewById(R.id.login_password)
        login = root.findViewById(R.id.login)

        if (!MainActivity.database().userDao().isExists()) {
            login.setOnClickListener {
                val email = loginEmail.text
                val password = loginPassword.text

                if (email.isNotBlank() && password.isNotBlank()) {
                    Fuel.post(
                        "https://anicloud.io/login",
                        listOf("email" to email, "password" to password)
                    ).responseString { request, response, result ->
                        when (result) {
                            is Result.Failure -> {
                                login.text = "Login Ungültig"
                            }
                            is Result.Success -> {
                                try {
                                    val cookieContent =
                                        response.headers.get("set-cookie").stream().toList()

                                    for (cockie in cookieContent) {
                                        if (cockie.contains("rememberLogin")) {

                                            val regex: Regex = "rememberLogin=(.*?);".toRegex()

                                            val sessionId: String = regex.find(cockie)?.groups?.get(1)?.value!!

                                            if (sessionId != null) {
                                                val user = User(0, sessionId)
                                                MainActivity.database().userDao().insertUser(user)
                                            }

                                            loginContent.visibility = View.GONE
                                            loggedContent.visibility = View.VISIBLE
                                            break
                                        }
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        }
                    }
                }
            }
        }else{
            loginContent.visibility = View.GONE
            loggedContent.visibility = View.VISIBLE
        }

        return root
    }
}