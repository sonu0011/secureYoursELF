package sonu.finds.secureyourself.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
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

        // Enables Always-on
        setAmbientEnabled()

        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)


        wrbutton.setOnClickListener {
            val message = "$num"

            SendThread(datapath, message).start()
            num++
        }


    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var message = intent.getStringExtra("message")
            text.setText(message)
            message = "$num"

            SendThread(datapath, message).start()
            num++
        }
    }

    internal inner class SendThread//constructor
        (var path: String, var message: String) : Thread() {

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.

        override fun run() {
            //first get all the nodes, ie connected wearable devices.
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

                    } catch (exception: ExecutionException) {

                    } catch (exception: InterruptedException) {
                    }

                }

            } catch (exception: ExecutionException) {

            } catch (exception: InterruptedException) {
            }

        }
    }
}
