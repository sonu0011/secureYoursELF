package sonu.finds.secureyourself.services

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class ListenerService : WearableListenerService() {
    internal var TAG = "wear listener"
    override fun onMessageReceived(messageEvent: MessageEvent?) {

        if (messageEvent!!.path == "/message_path") {
            val message = String(messageEvent.data)
            Log.v(TAG, "Message path received is: " + messageEvent.path)
            Log.v(TAG, "Message received is: $message")

            // Broadcast message to wearable activity for display
            val messageIntent = Intent()
            messageIntent.action = Intent.ACTION_SEND
            messageIntent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }

}