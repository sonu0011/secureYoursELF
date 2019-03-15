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

                //setting boolean value of active call
                SharedPrefManager.getInstance(this)
                    .CAllPickUp(
                        SharedPrefManager.getInstance(this)
                            .WhichHasGone()
                    )

                val messageToSend = "Emergency,Emergency Immediate Help Required"
                Timber.d("call is active ")
                val selfContat = SharedPrefManager.getInstance(this).GetSelfContactNumber()
                if (Constant.NORMAL_CALLING != "normal") {
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend + "\n" + selfContat, null, null)
                    val task =  fusedLocationClient.lastLocation
                    if (task !=null){
                        Log.e("tag","task in not null")
                        task.addOnSuccessListener{
                        val doublelong = it.longitude
                        val doublelat = it.latitude
                        if(doublelat != null && doublelong !=null){

                            Log.e("tag", doublelat.toString()+""+doublelong.toString())
                            val LocationToSend = "My Location is\n"
                            val smsBody =  StringBuffer();
                            smsBody.append("http://maps.google.com?q=");
                            smsBody.append(doublelat);
                            smsBody.append(",");
                            smsBody.append(doublelong);
                            SmsManager.getDefault().sendTextMessage(number, null, LocationToSend+smsBody + selfContat, null, null)

                        }
                            else{
                            Toast.makeText(this,"Unable To Fetch Location",Toast.LENGTH_SHORT).show()
                        }



                            }


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
        if (Constant.NORMAL_CALLING != "normal") {
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            if (countDownTimer1 != null) {
                countDownTimer1!!.cancel()
            }

        }
        //loging the values
        Log.e("DialerActivityOnDestroy","getNextCllTurn"+
                SharedPrefManager.getInstance(this).GetNextCallTurn())
        Log.e("DialerActivityOnDestroy","callPickUpOrNot"+
                SharedPrefManager.getInstance(this)
                    .CAllPickUpOrNot(SharedPrefManager.getInstance(this).GetNextCallTurn()))


        //call turns settings

        if (SharedPrefManager.getInstance(this).GetNextCallTurn() == 1 &&
            !SharedPrefManager.getInstance(this).CAllPickUpOrNot(1))
        {
            Log.e("DialerActivity","call NextCallturn 1 and not picked false")
            //setting up that i am going to make a call
            SharedPrefManager.getInstance(this).IamGoing(1)
            //setting up next call turn
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(2)){
                Log.e("DialerActivity","call not pickUp By call 2")
                SharedPrefManager.getInstance(this).SetNextCallTurn(2)
            }
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(0)){
                Log.e("DialerActivity","call not pickUp By call 0")

                SharedPrefManager.getInstance(this).SetNextCallTurn(0)
            }


            // second call and call not pickUp
            val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
            val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[1], null))
            intent.putExtra("updateIntent",111);
            startActivity(intent)
            return

        }


        if (SharedPrefManager.getInstance(this).GetNextCallTurn() == 2 &&
            !SharedPrefManager.getInstance(this).CAllPickUpOrNot(2))
        {
            Log.e("DialerActivity","call NextCallturn 2 and not picked false")

            //setting up that i am going to make a call
            SharedPrefManager.getInstance(this).IamGoing(2)
            //setting up next call turn
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(0)){
                Log.e("DialerActivity","call not pickUp By call 0")

                SharedPrefManager.getInstance(this).SetNextCallTurn(0)
            }
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(1)){
                Log.e("DialerActivity","call not pickUp By call 1")

                SharedPrefManager.getInstance(this).SetNextCallTurn(1)
            }


            // second call and call not pickUp
            val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
            val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[2], null))
            intent.putExtra("updateIntent",111);
            startActivity(intent)
            return

        }


        if (SharedPrefManager.getInstance(this).GetNextCallTurn() == 0 &&
            !SharedPrefManager.getInstance(this).CAllPickUpOrNot(0))
        {
            Log.e("DialerActivity","call NextCallturn 0 and not picked false")

            //setting up that i am going to make a call
            SharedPrefManager.getInstance(this).IamGoing(0)
            //setting up next call turn
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(1)){
                Log.e("DialerActivity","call not pickUp By call 1")

                SharedPrefManager.getInstance(this).SetNextCallTurn(1)
            }
            if (!SharedPrefManager.getInstance(this).CAllPickUpOrNot(2)){
                Log.e("DialerActivity","call not pickUp By call 2")

                SharedPrefManager.getInstance(this).SetNextCallTurn(2)
            }


            // second call and call not pickUp
            val nos: Array<String> = SharedPrefManager.getInstance(this).GetEmergencyContactNumbers()
            val intent = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", nos[0], null))
            intent.putExtra("updateIntent",111);
            startActivity(intent)
            return

        }

        //setting value  false after pickingUp all the calls
        if (SharedPrefManager.getInstance(this).CAllPickUpOrNot(0)
            &&SharedPrefManager.getInstance(this).CAllPickUpOrNot(1)
            &&SharedPrefManager.getInstance(this).CAllPickUpOrNot(2)){
            Log.e("DialerActivity","all call pickUp")


            SharedPrefManager.getInstance(this).SetArraYFalseValue(0)
            SharedPrefManager.getInstance(this).SetArraYFalseValue(1)
            SharedPrefManager.getInstance(this).SetArraYFalseValue(2)
        }




    }


}
