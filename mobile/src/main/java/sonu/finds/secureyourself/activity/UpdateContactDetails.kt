package sonu.finds.secureyourself.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_details.*
import kotlinx.android.synthetic.main.activity_update_contact_details.*
import sonu.finds.secureyourself.R
import sonu.finds.secureyourself.storage.SharedPrefManager

class UpdateContactDetails : AppCompatActivity() {
    lateinit var contactnumbers:Array<String>

    lateinit var sharedPrefManager :SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_contact_details)

        sharedPrefManager =  SharedPrefManager.getInstance(this);
        mobile_number_update.setText( sharedPrefManager.GetSelfContactNumber())
        contactnumbers = sharedPrefManager.GetEmergencyContactNumbers()

        contact_1_update.setText(contactnumbers[0])
        contact_2_update.setText(contactnumbers[1])
        contact_3_update.setText(contactnumbers[2])





    }

    override fun onStart() {
        super.onStart()

        save_phone_number_update.setOnClickListener {
            if (mobile_number_update.text.trim().length == 0) {
                Toast.makeText(this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (mobile_number_update.text.trim().length < 10) {
                Toast.makeText(this, "Please Enter A Valid  Mobile Number", Toast.LENGTH_SHORT).show()


            } else {
                SharedPrefManager.getInstance(this).SaveSelfContactNumber(mobile_number_update.text.toString())
                self_contact_details_update.setText("Mobile Number Updated Successfully")
                // Toast.makeText(this, "Mobile Number Saved Successfully", Toast.LENGTH_SHORT).show()
                mobile_number_update.setText("")
                save_phone_number_update.isEnabled = false;

            }
        }

        save_security_btn_update.setOnClickListener {

            if (contact_1_update.text.trim().length == 0 ||
                contact_2_update.text.trim().length == 0 ||
                contact_3_update.text.trim().length == 0
            ) {
                Toast.makeText(this, "Three Mobile Numbers Are Required For Emergency Calling", Toast.LENGTH_SHORT)
                    .show()


            } else if (contact_1_update.text.trim().length < 10 ||
                contact_2_update.text.trim().length < 10 ||
                contact_3_update.text.trim().length < 10
            ) {
                Toast.makeText(this, "Please Enter A Valid  Mobile Numbers", Toast.LENGTH_SHORT).show()


            } else {

                SharedPrefManager.getInstance(this).SaveEmergencyContactNumbers(
                    contact_1_update.text.toString(),
                    contact_2_update.text.toString(), contact_3_update.text.toString()
                )
                emer_contact_details_update.setText("Contact  Numbers are Updated Successfully")
                //Toast.makeText(this, "Contact  Numbers are Saved Successfully", Toast.LENGTH_SHORT).show()
                contact_1_update.setText("")
                contact_2_update.setText("")
                contact_3_update.setText("")
                save_security_btn_update.isEnabled = false;


            }
        }

    }
}
