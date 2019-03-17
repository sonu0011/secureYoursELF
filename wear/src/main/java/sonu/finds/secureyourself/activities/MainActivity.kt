package sonu.finds.secureyourself.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_main.*
import sonu.finds.secureyourself.R
import java.util.concurrent.ExecutionException

class MainActivity : WearableActivity() {
     var datapath = "/message_path"
     var num = 1




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setAmbientEnabled()

//        val messageFilter = IntentFilter(Intent.ACTION_SEND)
//        val messageReceiver = MessageReceiver()
//        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)
//

        wrbutton.setOnClickListener {
            val message = "$num"

            SendThread(datapath, message).start()
            num++
        }


    }

//    inner class MessageReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            var message = intent.getStringExtra("message")
//
//            message = "$num"
//
//            SendThread(datapath, message).start()
//            num++
//        }
//    }

    internal inner class SendThread//constructor
        (var path: String, var message: String) : Thread() {


        override fun run() {

            val nodeListTask = Wearable.getNodeClient(applicationContext).connectedNodes
            try {

                // Block on a task and get the result synchronously (because this is on a background
                // thread).

                val nodes = Tasks.await(nodeListTask)

                //Now send the message to each device.

                for (node in nodes) {
                    if (node ==null){
                        Toast.makeText(this@MainActivity,"Connection Lost",Toast.LENGTH_SHORT).show()
                    }
                    val sendMessageTask =
                        Wearable.getMessageClient(this@MainActivity).sendMessage(node.id, path, message.toByteArray())

                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        val result = Tasks.await(sendMessageTask)
                        Log.e("MainActivity",result.toString())

                    } catch (exception: ExecutionException) {
                        Log.e("MainActivity",exception.message)

                    } catch (exception: InterruptedException) {
                        Log.e("MainActivity",exception.message)


                    }

                }

            } catch (exception: ExecutionException) {

            } catch (exception: InterruptedException) {
            }

        }
    }
}
