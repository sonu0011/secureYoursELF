package sonu.finds.secureyourself.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import sonu.finds.secureyourself.storage.SharedPrefManager;
import sonu.finds.secureyourself.utills.Constant;
import timber.log.Timber;
public class ListenerService extends WearableListenerService implements  DataClient.OnDataChangedListener {
    String TAG = "mobile Listener";
    String datapath = "/data_path";
    String[] contactdetails  = new String[3];


    @SuppressLint("BinaryOperationInTimber")
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/message_path")) {
            final String message = new String(messageEvent.getData());
            Timber.tag(TAG).v("Message path received on phone is: " + messageEvent.getPath());
            Timber.tag(TAG).v("Message received on phone is: " + message);

            // Broadcast message to MainActivity for display
            //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
//            Intent messageIntent = new Intent();
            Constant.Companion.setNORMAL_CALLING(message);
//            messageIntent.setAction(Intent.ACTION_SEND);
//            messageIntent.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

         contactdetails =    SharedPrefManager.getInstance(getApplicationContext()).GetEmergencyContactNumbers();

             Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", contactdetails[0], null));
            intent.putExtra("updateIntent",111);

            startActivity(intent);
            SharedPrefManager.getInstance(getApplicationContext()).SetNextCallTurn(1);
            SharedPrefManager.getInstance(this).IamGoing(1);



        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Timber.d("onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    Timber.tag(TAG).v("Wear activity received message: " + message);
                    // Display message in U-I

                } else {
                    Timber.e("Unrecognized path: " + path);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Timber.tag(TAG).v("Data deleted : " + event.getDataItem().toString());
            } else {
                Timber.e("Unknown data event Type = " + event.getType());
            }
        }
    }
}
