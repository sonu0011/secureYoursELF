package sonu.finds.secureyourself.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.telecom.Call
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_dialer.*
import sonu.finds.secureyourself.R
import sonu.finds.secureyourself.storage.SharedPrefManager
import sonu.finds.secureyourself.utills.Constant
import sonu.finds.secureyourself.utills.OngoingCall
import sonu.finds.secureyourself.utills.asString
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit


class DialerActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()

    private lateinit var number: String
    var countDownTimer: CountDownTimer? = null
    var countDownTimer1: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(sonu.finds.secureyourself.R.layout.activity_dialer)
        Timber.d("onstart is called")
        number = intent.data!!.schemeSpecificPart


    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, DialerActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }


    override fun onStart() {
        super.onStart()
        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }

        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe { finish() }
            .addTo(disposables)
    }


    @SuppressLint("MissingPermission", "HardwareIds", "SetTextI18n")
    private fun updateUi(state: Int) {
        Timber.d("call state" + state)
        Log.e("DialerActivity", "" + state)

        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"

        if (Constant.NORMAL_CALLING != "normal") {
            if (state.asString().equals("DIALING")) {

                countDownTimer = object : CountDownTimer(18000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        Timber.d("onTick" + millisUntilFinished)
                        Log.e("DialerActivity", "countdownTimer  " + millisUntilFinished)
                        if (millisUntilFinished < 1000) {
                            OngoingCall.hangup()

                        }


                    }

                    override fun onFinish() {
                        if (Constant.HANGUP_REQUEST == 0) {
                            Log.e("DialerActivity", "countdownTimerFinish")

//                            OngoingCall.hangup()

                        }


                    }
                }.start()


            }

            if (state.asString().equals("ACTIVE")) {
                val messageToSend = "Please Help Me,My Mobile Number is"
                Timber.d("call is active ")
                val selfContat = SharedPrefManager.getInstance(this).GetSelfContactNumber()
                // val number = "8727888113"
                //val songuri = Uri.fromFile(File("//assets/htc.mp3"))
                if (Constant.NORMAL_CALLING != "normal") {
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend + "\n" + selfContat, null, null)
                }
                countDownTimer1 = object : CountDownTimer(6000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        Timber.d("onTick" + millisUntilFinished)
                        Log.e("DialerActivity", "countdownTimer1  " + millisUntilFinished)
                        if (millisUntilFinished < 1000) {
                            OngoingCall.hangup()

                        }
                        Constant.HANGUP_REQUEST = 1

                    }

                    override fun onFinish() {
                        Log.e("DialerActivity", "countdownTimer1Finish")

                       // OngoingCall.hangup()


                    }
                }.start()
            }




            answer.isVisible = state == Call.STATE_RINGING
            hangup.isVisible = state in listOf(
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE
            )

        }
        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "onDestroy" + SharedPrefManager.getInstance(this).callTimes, Toast.LENGTH_SHORT).show()
        if (Constant.NORMAL_CALLING != "normal") {
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            if (countDownTimer1 != null) {
                countDownTimer1!!.cancel()
            }

            if (SharedPrefManager.getInstance(this).callTimes == 0) {

                val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
                val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[1], null))
                startActivity(intent)
                SharedPrefManager.getInstance(this).setCllTimesValue(1)
                return

            }
            if (SharedPrefManager.getInstance(this).callTimes == 1) {
                SharedPrefManager.getInstance(this).setCllTimesValue(5)
                val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
                val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[2], null))
                startActivity(intent)
                return


            } else {
                SharedPrefManager.getInstance(this).setCllTimesValue(0)

            }
        }
    }

}
