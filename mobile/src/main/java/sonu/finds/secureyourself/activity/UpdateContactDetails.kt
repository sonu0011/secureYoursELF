package sonu.finds.secureyourself.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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

        sharedPrefManager =  SharedPrefManager.getInstance(this)
        up_user_name.setText(sharedPrefManager.selfNmae)
        up_contact_number.setText( sharedPrefManager.getSelfContactNumber())
        contactnumbers = sharedPrefManager.GetEmergencyContactNumbers()


        contact_1_update.setText(contactnumbers[0])
        contact_2_update.setText(contactnumbers[1])
        contact_3_update.setText(contactnumbers[2])


        up_user_message.setText(sharedPrefManager.storeMessage)



    }

    override fun onStart() {
        super.onStart()

        update_contact_details.setOnClickListener {

            if (up_user_name.text.toString().trim().length == 0){
                Toast.makeText(this,"Please Enter Your Name",Toast.LENGTH_SHORT).show()
            }
            else if (up_contact_number.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Your Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (up_contact_number.text.toString().length < 10){
                Toast.makeText(this,"Please Enter  A Valid Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_1_update.text.toString().length == 0){
                Toast.makeText(this,"Please Enter First  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_2_update.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Second  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_3_update.text.toString().length == 0){
                Toast.makeText(this,"Please Enter Third  Contact Number You Want to Alert",Toast.LENGTH_SHORT).show()
            }
            else if (contact_1_update.text.toString().length  < 10){
                Toast.makeText(this,"Invalid First Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_2_update.text.toString().length  < 10){
                Toast.makeText(this,"Invalid Second Contact Number",Toast.LENGTH_SHORT).show()
            }
            else if (contact_3_update.text.toString().length  < 10){
                Toast.makeText(this,"Invalid Third Contact Number",Toast.LENGTH_SHORT).show()
            }
            else{

                Toast.makeText(this," Details Updated Successfully ",Toast.LENGTH_SHORT).show()

                SharedPrefManager.getInstance(this).saveSelfContactNumber(
                    up_contact_number.text.toString()
                )
                SharedPrefManager.getInstance(this).storeSelfName(
                    up_user_name.text.toString()
                )
                SharedPrefManager.getInstance(this).SaveEmergencyContactNumbers(
                    contact_1_update.text.toString(),
                    contact_2_update.text.toString(),
                    contact_3_update.text.toString()

                )
                startActivity(Intent(this,AddDetailsActivity::class.java))

            }

        }



    }
}
