package sonu.finds.secureyourself.storage;

import android.content.Context;
import android.content.SharedPreferences;

import static sonu.finds.secureyourself.utills.Constant.SHARED_PREF_NAME;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;

    private Context mCtx;
    public boolean[] callingTimes = new boolean[3];

    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    // checking which has call has picked up

    public void CAllPickUp(int position) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        callingTimes[position] = true;

        editor.putBoolean("CAllPickUpOrNot" + position, callingTimes[position]);
        editor.apply();


    }

    public boolean CAllPickUpOrNot(int position) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean("CAllPickUpOrNot"+ position, callingTimes[position]);
    }
//    end of checking which call has picked up




    //checking which is currently goes for calling

    public int WhichHasGone() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getInt("putIntValue", -1);    }

    public void IamGoing(int posotion){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("putIntValue",posotion);
        editor.apply();
    }

    //end of which is currently goes for calling


    public void SetArraYFalseValue(int position) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        callingTimes[position] = false;

        editor.putBoolean("CAllPickUpOrNot" + position, callingTimes[position]);
        editor.apply();


    }

    // checking next call turn

    public void SetNextCallTurn(int value) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("putCallTimesValue", value);
        editor.apply();
    }

    public int GetNextCallTurn() {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getInt("putCallTimesValue", -1);

    }

    // end of checking next call turn





    public void SaveSelfContactNumber(String SelfContactNumber) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("SelfContactNumber", SelfContactNumber);
        editor.apply();

    }

    public void SaveEmergencyContactNumbers(String... EmergencyContactNumbers) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("EmergencyContactNumber1", EmergencyContactNumbers[0]);
        editor.putString("EmergencyContactNumber2", EmergencyContactNumbers[1]);
        editor.putString("EmergencyContactNumber3", EmergencyContactNumbers[2]);
        editor.apply();

    }

    public String GetSelfContactNumber() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString("SelfContactNumber", null);

    }

    public String[] GetEmergencyContactNumbers() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String[] emergencyContactDetails = new String[3];

        emergencyContactDetails[0] = sharedPreferences.getString("EmergencyContactNumber1", null);
        emergencyContactDetails[1] = sharedPreferences.getString("EmergencyContactNumber2", null);
        emergencyContactDetails[2] = sharedPreferences.getString("EmergencyContactNumber3", null);

        return emergencyContactDetails;

    }


}
