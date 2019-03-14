package sonu.finds.secureyourself.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager

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
import java.util.concurrent.TimeUnit

import com.google.android.gms.location.*


class DialerActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    var countDownTimer: CountDownTimer? = null

    var countDownTimer1: CountDownTimer? = null
    lateinit var audioManager: AudioManager
    lateinit var fusedLocationClient : FusedLocationProviderClient
    var longitude:Double ? = null
    var lattitude:Double ? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(sonu.finds.secureyourself.R.layout.activity_dialer)
        Timber.d("onstart is called")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

                countDownTimer = object : CountDownTimer(15000, 1000) {
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

                Log.e("DialerActivity","Active value of active calling  is"
                        +SharedPrefManager.getInstance(this).intValue)

                val int =  SharedPrefManager.getInstance(this).callTimes
                if (int == 0){
                    //person picks up first time
                    //SharedPrefManager.getInstance(this).setCllTimesValue(1)
                }
                SharedPrefManager.getInstance(this)
                    .setCallingTimes(
                        SharedPrefManager.getInstance(this)
                            .intValue
                    )
                Log.e("DialerActivity ","Active value of active boolean array "
                        +SharedPrefManager.getInstance(this)
                    .getCallingTimes(
                        SharedPrefManager.getInstance(this)
                            .intValue
                    )
                )
//                if (int == 0){
//                    //set value true
//                    SharedPrefManager.getInstance(this).setCallingTimes(0)
//                }
//                else{
//                    SharedPrefManager.getInstance(this).setCallingTimes(int-1)
//
//                }
                val messageToSend = "Emergency,Emergency Immediate Help Required"
                Timber.d("call is active ")
                val selfContat = SharedPrefManager.getInstance(this).GetSelfContactNumber()
                if (Constant.NORMAL_CALLING != "normal") {
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend + "\n" + selfContat, null, null)


                    val task =  fusedLocationClient.lastLocation
                    if (task !=null){
//                        Log.e("tag","task in not null")
//                        task.addOnSuccessListener{
//                            Log.e("tag","on success ")
//
//                            lattitude = it.latitude
//                            lattitude.let {
//
//
//                            }
//                            val doublelong = it.longitude
//                            val doublelat = it.latitude
//
//                            Log.e("tag", doublelat.toString()+""+doublelong.toString())
//                            val LocationToSend = "My Location is\n"
//                            val smsBody =  StringBuffer();
//                            smsBody.append("http://maps.google.com?q=");
//                            smsBody.append(doublelat);
//                            smsBody.append(",");
//                            smsBody.append(doublelong);
//                            SmsManager.getDefault().sendTextMessage(number, null, LocationToSend+smsBody + selfContat, null, null)
//
//
//                        }
                    }
                    else{
                        Log.e("tag","task in  null")

                    }

                }
                countDownTimer1 = object : CountDownTimer(7000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (countDownTimer !=null){
                            countDownTimer!!.cancel()
                        }
                        Timber.d("onTick" + millisUntilFinished)
                        Log.e("DialerActivity", "countdownTimerActive  " + millisUntilFinished)
                        if (millisUntilFinished < 1000) {
                            if (OngoingCall.getCallStatus() != null) {
                                OngoingCall.hangup()
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
        Log.e("DialerActivity","onDestroyCalled")
        Log.e("DialerActivity","onDestroyCalled and value of shared prefrence vall value is" +
                ""+SharedPrefManager.getInstance(this).callTimes)
        Log.e("DialerActivity","onDestroyCalled and value of boolean  vall value is" +
                ""+SharedPrefManager.getInstance(this).getCallingTimes(
            SharedPrefManager.getInstance(this).callTimes
        ))

        if (Constant.NORMAL_CALLING != "normal") {
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            if (countDownTimer1 != null) {
                countDownTimer1!!.cancel()
            }

           if (SharedPrefManager.getInstance(this).callTimes == 0
                &&SharedPrefManager.getInstance(this).getCallingTimes(
                   SharedPrefManager.getInstance(this).callTimes
                ) == false) {
               SharedPrefManager.getInstance(this).intValue = 0

               Log.e("DialerActivity","call times first and call time value is 0 and value is false")
               if (SharedPrefManager.getInstance(this).getCallingTimes(
                       SharedPrefManager.getInstance(this).intValue+1
                   ) == false){
                   SharedPrefManager.getInstance(this).setCllTimesValue(1)


               }
               else if (SharedPrefManager.getInstance(this).getCallingTimes(
                       SharedPrefManager.getInstance(this).intValue+2
                   ) == false){
                   SharedPrefManager.getInstance(this).setCllTimesValue(2)


               }
                val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
                val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[0], null))
               intent.putExtra("updateIntent",111);
               startActivity(intent)
               return


            }
            if (SharedPrefManager.getInstance(this).callTimes == 1
                &&SharedPrefManager.getInstance(this).getCallingTimes(
                    SharedPrefManager.getInstance(this).callTimes
                ) == false)  {
                SharedPrefManager.getInstance(this).intValue = 1
                Log.e("DialerActivity","call times second and call time value is 1 and value is false")

                if (SharedPrefManager.getInstance(this).getCallingTimes(
                        SharedPrefManager.getInstance(this).intValue+1
                    ) == false){
                    SharedPrefManager.getInstance(this).setCllTimesValue(2)

                }
                else if (SharedPrefManager.getInstance(this).getCallingTimes(
                        0
                    ) == false){
                    SharedPrefManager.getInstance(this).setCllTimesValue(0)


                }
                val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
                val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[1], null))
                intent.putExtra("updateIntent",111);

                startActivity(intent)
                return



            }
            if (SharedPrefManager.getInstance(this).callTimes == 2
                &&SharedPrefManager.getInstance(this).getCallingTimes(
                    SharedPrefManager.getInstance(this).callTimes
                ) == false)  {
                Log.e("DialerActivity","call times third and call time value is 2 and value is false")
                SharedPrefManager.getInstance(this).intValue = 2
                if (SharedPrefManager.getInstance(this).getCallingTimes(
                        0
                    ) == false){
                    SharedPrefManager.getInstance(this).setCllTimesValue(0)

                }
                else if (SharedPrefManager.getInstance(this).getCallingTimes(
                        SharedPrefManager.getInstance(this).intValue -1
                    ) == false){
                    SharedPrefManager.getInstance(this).setCllTimesValue(1)


                }

                val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
                val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[2], null))
                intent.putExtra("updateIntent",111);
                startActivity(intent)

                return


            }
//            else {
//                Log.e("DialerActivity","else part")
////                SharedPrefManager.getInstance(this).setCllTimesValue(0)
////               for (i in 0..2){
////                   SharedPrefManager.getInstance(this).callingTimes[i] = false
////                   Log.e("DialerActivity","else part on Destroy After successfull deliver message")
////
////
////               }
//
//            }
        }
    }


}
