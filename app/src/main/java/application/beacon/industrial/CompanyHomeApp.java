package application.beacon.industrial;

import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;


import timber.log.Timber;

/**
 *
 */
public class CompanyHomeApp extends Application implements BootstrapNotifier {

    private BeaconManager mBeaconManager;
    private BackgroundPowerSaver mBackgroundPowerSaver;
    private RegionBootstrap mRegionBootstrap;
    private  Region mAllBeaconsRegion;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }


        mAllBeaconsRegion =  new Region(getPackageName(), null, null, null);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.setBackgroundScanPeriod(5000L);
        mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);
        mBackgroundPowerSaver = new BackgroundPowerSaver(this);
        Timber.i(">>>>> onCreate() existing... ");
    }

    // ----- method implementations ----------

    @Override
    public void didEnterRegion(Region region) {

        Timber.i(">>>>> didEnterRegion(Region region) region: " + region.getUniqueId());

        Intent intentMainActivity = new Intent(this,CompanyHomeActivity.class);
        //Intent intentMainActivity = new Intent(this, BeaconActiveReceiver.class);
        intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMainActivity);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }


    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
