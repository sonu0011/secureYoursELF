package sonu.finds.secureyourself.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
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
import android.provider.Settings

import com.klinker.android.send_message.Transaction
import com.google.android.mms.pdu_alt.PduPersister.getBytes
import com.klinker.android.send_message.Message
import java.io.IOException
import java.io.InputStream


class DialerActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()

    private lateinit var number: String
    var countDownTimer: CountDownTimer? = null
    private lateinit var mp: MediaPlayer

    var countDownTimer1: CountDownTimer? = null
    var hanguprequest: Int = 0
    lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(sonu.finds.secureyourself.R.layout.activity_dialer)
        Timber.d("onstart is called")
        number = intent.data!!.schemeSpecificPart
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL

        speaker_on_of.setOnClickListener {
                    try {


                        if (!audioManager.isSpeakerphoneOn) {
                            speaker_on_of.setImageResource(R.drawable.ic_volume_up_black_24dp)
                            audioManager.setSpeakerphoneOn(true);
                        }
                        else {
                            audioManager.setSpeakerphoneOn(false);
                            speaker_on_of.setImageResource(R.drawable.ic_volume_down_black_24dp)


                        }
                    } catch (excep: InterruptedException) {
                        Toast.makeText(this@DialerActivity, "error " + excep.message, Toast.LENGTH_SHORT).show()
                    }
                }




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
                        Log.e("DialerActivity", "countdownTimerDialing  " + millisUntilFinished)
                        if (millisUntilFinished < 1000) {
//                            if(Constant.HANGUP_REQUEST == 0)
                            if (OngoingCall.getCallStatus() != null) {
                                OngoingCall.hangup()

                            }

                        }


                    }

                    override fun onFinish() {

                        Log.e("DialerActivity", "countdownTimerFinish")

//                            OngoingCall.hangup()


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
                    val root: File = android.os.Environment.getExternalStorageDirectory()
                   val  fileName = root.getAbsolutePath() + "/ScureYourSelf/Audios/" +
                            "recoded_file"+".mp3"
                    //SmsManager.getDefault().sendMultimediaMessage(this,Uri.parse(fileName),null,null,null)
//
//                    val sendSettings = com.klinker.android.send_message.Settings()
//                    val sendTransaction = Transaction(this, sendSettings)
//
//                    val mMessage = Message(messageToSend, number)
//                    var stream =    getContentResolver().openInputStream(Uri.parse(fileName));
//                    var  inputData = getBytes(stream.toString())
//                    mMessage.addAudio(inputData)
//                    sendTransaction.sendNewMessage(mMessage, Transaction.NO_THREAD_ID)
                    try {
                        audioManager.setSpeakerphoneOn(true);
                        mp = MediaPlayer.create(this,Uri.parse(fileName))
                        mp.start()
                    }catch (ex:IOException){
                        Log.e("DialerActivity", "No file found on this device "+ex.message)

                    }


                }
                countDownTimer1 = object : CountDownTimer(11000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (countDownTimer !=null){
                            countDownTimer!!.cancel()
                        }
                        Timber.d("onTick" + millisUntilFinished)
                        Log.e("DialerActivity", "countdownTimerActive  " + millisUntilFinished)
                        if (millisUntilFinished < 1000) {
                            if (OngoingCall.getCallStatus() != null) {
                                OngoingCall.hangup()
                                mp.release()
                            }
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
