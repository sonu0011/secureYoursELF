package sonu.finds.secureyourself.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.telecom.TelecomManager
import android.telephony.SmsManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_add_details.*
import sonu.finds.secureyourself.R
import sonu.finds.secureyourself.storage.SharedPrefManager
import sonu.finds.secureyourself.utills.Constant.Companion.REQUEST_PERMISSION
import timber.log.Timber
import java.lang.NullPointerException

class AddDetailsActivity : AppCompatActivity() {


    lateinit var handler: Handler
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    private val TAG = "AddDetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)
            supportActionBar!!.title = "High Secutiry Alert"


        offerReplacingDefaultDialer()


        val intvalue =  intent.getIntExtra("updateIntent",0)
        if (intvalue == 0 ){
            Log.e("AddettailsActivity", "no intent")

            for (k in 0..2){
                Log.e("AddettailsActivity", "inside for loop")

                SharedPrefManager.getInstance(this)
                    .SetArraYFalseValue(k)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions()
        }

        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            val alertDialog =  AlertDialog.Builder(this);
            alertDialog.setCancelable(false)

        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Please Turn On GPS for working of this app ");

            alertDialog.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }


//             Display a negative button on alert dialog
            alertDialog.setNegativeButton("No"){dialog,which ->
                Toast.makeText(applicationContext,"Please Turn On GPS  Location Manually",Toast.LENGTH_SHORT).show()
         dialog.cancel()
            }


        val dialog =  alertDialog.show()
            val button  =  dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                 val params =  LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0,0,50,0);
            button.setLayoutParams(params);
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


    private fun checkAndRequestPermissions(): Boolean {
        val callpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        val accessfinelocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val accesscoarselocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val smspermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        val phonestatepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//        val readphonenumberpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
//        val accessnetworkstatepermisson = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)


        val listPermissionsNeeded = ArrayList<String>()

        if (callpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)
        }
        if (accessfinelocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (accesscoarselocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (smspermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (phonestatepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
//        if (readphonenumberpermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_NUMBERS)
//        }
//        if (accessnetworkstatepermisson != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE)
//        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//


        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions

                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.SEND_SMS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.READ_PHONE_NUMBERS] = PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.ACCESS_NETWORK_STATE] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user

                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]


                    if (perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.SEND_SMS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                        //&& perms[Manifest.permission.READ_PHONE_NUMBERS] == PackageManager.PERMISSION_GRANTED
                        //&& perms[Manifest.permission.ACCESS_NETWORK_STATE] == PackageManager.PERMISSION_GRANTED
                    ) {

                        Log.d(TAG, "sms & location services permission granted")
                        // process the normal flow



                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                           // || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_NUMBERS)
                         //   || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                        ) {
                            showDialogOK("All  Permissions are required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }



    }

    fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(msg)
            .setPositiveButton("Yes") { paramDialogInterface, paramInt ->


                startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse(packageName)))
            }
            .setNegativeButton("Cancel") {
                    paramDialogInterface, paramInt -> finish()
            }
        dialog.show()

    }


    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        checkValueAndPerformAciton()

        }

    private fun checkValueAndPerformAciton() {
        if (SharedPrefManager.getInstance(this).GetEmergencyContactNumbers() != null &&
            SharedPrefManager.getInstance(this).getSelfContactNumber() != null
            && SharedPrefManager.getInstance(this).selfNmae !=null
            && SharedPrefManager.getInstance(this).storeMessage !=null
        ) {
//            var displayMetrics =  DisplayMetrics()
//            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            var height = displayMetrics.heightPixels;
//            var width = displayMetrics.widthPixels;
//             var rmainingheight =  height - 360.00
//           var params1 = textAlert.getLayoutParams();
//params1.height = rmainingheight.toInt()
//textAlert.setLayoutParams(params1)

            contacts_layout.visibility = View.GONE
            welcomeImage.visibility = View.VISIBLE
            textAlert.visibility = View.VISIBLE
            icon_Logo.visibility = View.GONE




        }



    save_details_btn.setOnClickListener {
            if (user_name.text.toString().trim().length == 0){
                Toast.makeText(this,"Please Enter Your Name",Toast.LENGTH_SHORT).show()
            }
            else if (mobile_number.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Your Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (mobile_number.text.toString().length < 10){
                Toast.makeText(this,"Please Enter  A Valid Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_1.text.toString().length == 0){
                Toast.makeText(this,"Please Enter First  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_2.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Second  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_3.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Third  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_1.text.toString().length  < 10){
                Toast.makeText(this,"Invalid First Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_2.text.toString().length  < 10){
                Toast.makeText(this,"Invalid Second Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_3.text.toString().length  < 10){
                Toast.makeText(this,"Invalid Third Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (user_message.text.toString().trim().length  == 0){
                Toast.makeText(this,"Please Enter Your Message",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Details Added Successfully",Toast.LENGTH_SHORT).show()

                SharedPrefManager.getInstance(this).saveSelfContactNumber(
                    mobile_number.text.toString()
                )
                SharedPrefManager.getInstance(this).storeSelfName(
                    user_name.text.toString()
                )
                SharedPrefManager.getInstance(this).SaveEmergencyContactNumbers(
                    contact_1.text.toString(),
                    contact_2.text.toString(),
                    contact_3.text.toString()

                )
                SharedPrefManager.getInstance(this).storeMessage(user_message.text.toString())
                startActivity(Intent(this,AddDetailsActivity::class.java))

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


                checkValueAndPerformAciton()


            }
            RESULT_CANCELED -> {

                val alertDialog =  AlertDialog.Builder(this);


                alertDialog.setTitle("Default Dialer!");
                alertDialog.setCancelable(false)

                alertDialog.setMessage("Default Dialer Permission is Required");

                alertDialog.setPositiveButton("YES"){dialog, which ->
                    // Do something when user press the positive button
                    offerReplacingDefaultDialer()

                    // Change the app background color
                }


                // Display a negative button on alert dialog
                alertDialog.setNegativeButton("No"){
                        dialog,which ->
                    Toast.makeText(applicationContext,"This  Permission is mandatory for running this app",Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                    finishAffinity()
                }

                alertDialog.show()


            }
            else -> {
                Toast.makeText(this, "Unexpected result code $resultCode", Toast.LENGTH_SHORT).show()


            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        for (j in 0..2){
            val b =  SharedPrefManager.getInstance(this).CAllPickUpOrNot(j)
            Log.e("AddDetails", b.toString())
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
