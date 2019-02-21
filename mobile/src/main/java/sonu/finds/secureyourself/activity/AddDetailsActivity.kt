package sonu.finds.secureyourself.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_details.*
import sonu.finds.secureyourself.R
import sonu.finds.secureyourself.storage.SharedPrefManager
import sonu.finds.secureyourself.utills.Constant.Companion.REQUEST_PERMISSION

class AddDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)


    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
        if (SharedPrefManager.getInstance(this).GetEmergencyContactNumbers() !=null &&
            SharedPrefManager.getInstance(this).GetSelfContactNumber() !=null){
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

                //permission granted for default app calling
                contacts_layout.visibility = View.VISIBLE


            }
            RESULT_CANCELED -> {
                Toast.makeText(this, "Default Dialer Permission is Required", Toast.LENGTH_SHORT).show()

            }
            else -> {
                Toast.makeText(this, "Unexpected result code $resultCode", Toast.LENGTH_SHORT).show()


            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.update_menu_item,menu)
        return true;//for opening the menu pop up


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==R.id.update_item){
            val intent = Intent(this, UpdateContactDetails::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}
