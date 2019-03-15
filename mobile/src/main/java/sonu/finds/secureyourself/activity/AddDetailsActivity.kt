package sonu.finds.secureyourself.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.telecom.TelecomManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_add_details.*
import sonu.finds.secureyourself.R
import sonu.finds.secureyourself.storage.SharedPrefManager
import sonu.finds.secureyourself.utills.Constant
import sonu.finds.secureyourself.utills.Constant.Companion.REQUEST_PERMISSION
import timber.log.Timber

class AddDetailsActivity : AppCompatActivity() {


    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)
        //SharedPrefManager.getInstance(this).SetNextCallTurn(0)
        val intvalue =  intent.getIntExtra("updateIntent",0)
        if (intvalue == 0 ){
            Log.e("AddettailsActivity", "no intent")
//
//
            for (k in 0..2){
                Log.e("AddettailsActivity", "inside for loop")

                SharedPrefManager.getInstance(this)
                    .SetArraYFalseValue(k)
            }
        }


//        record_imagevice.setOnClickListener {
//
//            //StartRecording()
//
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chekAllPermissions()
        }

        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            val alertDialog =  AlertDialog.Builder(this);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");

            alertDialog.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                // Change the app background color
            }


            // Display a negative button on alert dialog
            alertDialog.setNegativeButton("No"){dialog,which ->
                Toast.makeText(applicationContext,"Please Turn On Gps Location Manually",Toast.LENGTH_SHORT).show()
         dialog.cancel()
            }

        alertDialog.show()
        }


            handler = Handler(Handler.Callback { msg ->
            val stuff = msg.data
            logthis(stuff.getString("logthis"))
            true
        })


        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)


    }



//    fun StartRecording() {
//        Toast.makeText(this@AddDetailsActivity, "recording Started", Toast.LENGTH_SHORT).show()
//
//        mRecoder.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mRecoder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        val root: File = android.os.Environment.getExternalStorageDirectory()
//        val file: File = File(root.absolutePath + "/ScureYourSelf/Audios")
//        if (!file.exists()) {
//            file.mkdirs()
//        }
//        fileName = root.getAbsolutePath() + "/ScureYourSelf/Audios/" +
//                "recoded_file" + ".mp3"
//        mRecoder.setOutputFile(fileName);
//        mRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        var timer: CountDownTimer = object : CountDownTimer(10000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//
//            }
//
//            override fun onFinish() {
//                Toast.makeText(this@AddDetailsActivity, "recording saved successfully", Toast.LENGTH_SHORT).show()
//                try {
//                    mRecoder.stop();
//                    mRecoder.release();
//                } catch (e: Exception) {
//                    e.printStackTrace();
//                }
//
//            }
//
//
//        }.start()
//
//
//
//
//        try {
//
//            mRecoder.prepare();
//            mRecoder.start();
//        } catch (e: IOException) {
//            e.printStackTrace();
//            Toast.makeText(this, "error" + e.message, Toast.LENGTH_SHORT).show()
//        }
//
//
//    }

    fun logthis(newinfo: String?) {
        if (newinfo!!.compareTo("") != 0) {
            Toast.makeText(this, newinfo, Toast.LENGTH_SHORT).show()
        }
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("message")
            // Display message in UI
            Timber.d("OnBroadcastReceiver: " + message)

            logthis(message)

        }
    }

    fun chekAllPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION

                ), Constant.PERMISSION_REQUEST_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constant.PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 5 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "You must give permissions to use this app. .", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        offerReplacingDefaultDialer()
        if (SharedPrefManager.getInstance(this).GetEmergencyContactNumbers() != null &&
            SharedPrefManager.getInstance(this).GetSelfContactNumber() != null
        ) {
            contacts_layout.visibility = View.INVISIBLE
            welcomeImage.visibility = View.VISIBLE

        }



        save_phone_number.setOnClickListener {
            if (mobile_number.text.trim().length == 0) {
                Toast.makeText(this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (mobile_number.text.trim().length < 10) {
                Toast.makeText(this, "Please Enter A Valid  Mobile Number", Toast.LENGTH_SHORT).show()


            } else {
                SharedPrefManager.getInstance(this).SaveSelfContactNumber(mobile_number.text.toString())
                self_contact_details.setText("Mobile Number Saved Successfully")
                // Toast.makeText(this, "Mobile Number Saved Successfully", Toast.LENGTH_SHORT).show()
                mobile_number.setText("")
                save_phone_number.isEnabled = false;

            }
        }

        save_security_btn.setOnClickListener {

            if (contact_1.text.trim().length == 0 ||
                contact_2.text.trim().length == 0 ||
                contact_3.text.trim().length == 0
            ) {

                Toast.makeText(this, "Three Mobile Numbers Are Required For Emergency Calling", Toast.LENGTH_SHORT)
                    .show()


            } else if (contact_1.text.trim().length < 10 ||
                contact_2.text.trim().length < 10 ||
                contact_3.text.trim().length < 10
            ) {

                Toast.makeText(this, "Please Enter A Valid  Mobile Numbers", Toast.LENGTH_SHORT).show()


            } else {

                SharedPrefManager.getInstance(this).SaveEmergencyContactNumbers(
                    contact_1.text.toString(),
                    contact_2.text.toString(), contact_3.text.toString()
                )
                emer_contact_details.setText("Contact  Numbers are Saved Successfully")
                //Toast.makeText(this, "Contact  Numbers are Saved Successfully", Toast.LENGTH_SHORT).show()
                contact_1.setText("")
                contact_2.setText("")
                contact_3.setText("")
                save_security_btn.isEnabled = false;


            }
        }


    }

    @SuppressLint("ObsoleteSdkInt")
    private fun offerReplacingDefaultDialer() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
        if (isAlreadyDefaultDialer) return


        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        startActivityForResult(intent, REQUEST_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PERMISSION -> checkSetDefaultDialerResult(resultCode)
        }

    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        when (resultCode) {
            RESULT_OK -> {

                if (SharedPrefManager.getInstance(this).GetEmergencyContactNumbers() != null &&
                    SharedPrefManager.getInstance(this).GetSelfContactNumber() != null
                ) {
                    contacts_layout.visibility = View.INVISIBLE
                    welcomeImage.visibility = View.VISIBLE

                }
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

                //permission granted for default app calling


            }
            RESULT_CANCELED -> {
                Toast.makeText(this, "Default Dialer Permission is Required", Toast.LENGTH_SHORT).show()

            }
            else -> {
                Toast.makeText(this, "Unexpected result code $resultCode", Toast.LENGTH_SHORT).show()


            }
        }

    }

    override fun onResume() {
        super.onResume()
        for (j in 0..2){
            val b =  SharedPrefManager.getInstance(this).CAllPickUpOrNot(j)
            Log.e("AddDetOnStart", b.toString())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.update_menu_item, menu)
        return true;//for opening the menu pop up


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.update_item) {
            val intent = Intent(this, UpdateContactDetails::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}
