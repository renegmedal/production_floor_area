package application.beacon.industrial;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import application.beacon.industrial.logger.Log;
import application.beacon.industrial.logger.LogWrapper;
import timber.log.Timber;

/**
 *
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    /** Set up targets to receive log data */
    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        Timber.i(">>>>> initializeLogging() Ready....");
    }
}
