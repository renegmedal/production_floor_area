package application.beacon.industrial;


import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewAnimator;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import application.beacon.industrial.logger.Log;
import application.beacon.industrial.logger.LogFragment;
import application.beacon.industrial.logger.LogWrapper;
import application.beacon.industrial.logger.MessageOnlyLogFilter;
import application.beacon.industrial.service.WorkOrder;
import application.beacon.industrial.service.WorkOrderServiceApi;
import application.beacon.industrial.util.RxBus;
import application.beacon.industrial.view.Area;
import application.beacon.industrial.view.CompanyLayoutView;
import application.beacon.industrial.view.LayoutArea;
import application.beacon.industrial.workorders.WorkOrderDialogFragment;
import application.beacon.industrial.workorders.WorkOrderListEvent;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class CompanyHomeActivity extends BaseActivity implements
        BeaconConsumer, CompanyLayoutView.AreaDoubleTapListener {

    private static final String TAG = CompanyHomeActivity.class.getSimpleName();

    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    private static final int BEACON_MAXIMUM_DISTANCE = 4;
    private static final int NUMBER_OF_BEACONS = 2;
    private static final String LOCATION_MACHINING = "machining";
    private static final String LOCATION_RECEIVING = "receiving";
    private static final int LOCATION_MACHINING_INDEX = 0;
    private static final int LOCATION_RECEIVING_INDEX = 1;
    private static final String MACHINING_BEACON_ID = "00:07:80:15:74:79";
    private static final String RECEIVING_BEACON_ID = "00:07:80:15:8A:AA";

    private static final int MACHINING_INDICATOR_X_COORDINATE = 750;
    private static final int MACHINING_INDICATOR_Y_COORDINATE = 950;

    private static final int RECEIVING_INDICATOR_X_COORDINATE = 750;
    private static final int RECEIVING_INDICATOR_Y_COORDINATE = 200;

    private String mLastLocation = "NONE";
    CompanyLayoutView mCompanyLayoutView;

    private LayoutArea[] mAreas;


    private RxBus mRxBus;
    private String mUrl;
    private WorkOrderServiceApi mWorkOrderServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getResources().getString(R.string.service_api_url);

        setContentView(R.layout.activity_company_home);

        mCompanyLayoutView = (CompanyLayoutView) findViewById(R.id.layout_view);

        mAreas = new Area[NUMBER_OF_BEACONS];
        mAreas[LOCATION_MACHINING_INDEX] = new Area(LOCATION_MACHINING, MACHINING_BEACON_ID,
                MACHINING_INDICATOR_X_COORDINATE, MACHINING_INDICATOR_Y_COORDINATE);

        mAreas[LOCATION_RECEIVING_INDEX] = new Area(LOCATION_RECEIVING, RECEIVING_BEACON_ID,
                RECEIVING_INDICATOR_X_COORDINATE, RECEIVING_INDICATOR_Y_COORDINATE);

        mCompanyLayoutView.setLayoutAreas(mAreas);

        // make this activity listener to double tap touch event
        mCompanyLayoutView.setAreaDoubleTapListener(this);

        mBeaconManager.bind(this);

        mRxBus = new RxBus();


        RestAdapter restAdapter = new RestAdapter
                .Builder()
                .setEndpoint(mUrl)
                .build();

        mWorkOrderServiceApi = restAdapter.create(WorkOrderServiceApi.class);

    }

//    public RxBus getRxBus() {
//        if (mRxBus == null) {
//            mRxBus = new RxBus();
//        }
//
//        return mRxBus;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(false);
    }


    // --- implement method ----


    @Override
    public void doubleTappedArea(LayoutArea layoutArea) {
        Log.i(TAG, ">>>> doubleTappedArea(LayoutArea layoutArea.getName(): " + layoutArea.getName());

        final List<WorkOrder> workOrderList = new ArrayList<WorkOrder>();

        if (layoutArea.getName() != null) {
            String beaconId = "none";
            switch (layoutArea.getName()) {
                case LOCATION_MACHINING:
                    beaconId = MACHINING_BEACON_ID;
                    break;
                case LOCATION_RECEIVING:
                    beaconId = RECEIVING_BEACON_ID;
                    break;
            }

            Log.i(TAG, ">>>> doubleTappedArea() Beacon ID to use: " + beaconId);

            Subscription mSubscription = mWorkOrderServiceApi.getWorkOrdersByUuid(beaconId)
                    .flatMap(new Func1<Collection<WorkOrder>, Observable<WorkOrder>>() {
                        @Override
                        public Observable<WorkOrder> call(Collection<WorkOrder> workOrders) {
                            return Observable.from(workOrders);
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WorkOrder>() {

                        @Override
                        public void onCompleted() {
                            WorkOrderDialogFragment.newInstance(MACHINING_BEACON_ID, workOrderList).show(getSupportFragmentManager(), "workorder");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(WorkOrder workOrder) {
                            workOrderList.add(workOrder);
                        }
                    });
        }

    }


    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {   // this would receive notification if beacons are in range
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                //Timber.i(">>>>> didRangeBeaconsInRegion() beacon region: " + region.getUniqueId());

                boolean display = false;
                Beacon inRangeBeacon = null;
                for (Beacon beacon : beacons) {
                    //Timber.i("    beacon: " + beacon.getBluetoothAddress() + " distance: " + beacon.getDistance());
                    if (beacon.getDistance() < BEACON_MAXIMUM_DISTANCE) {
                        //                   Timber.i("    beacon: " + beacon.getBluetoothAddress() + " distance: " + beacon.getDistance());
                        inRangeBeacon = beacon;
                        display = true;
                        break;
                    }
                }

                if (inRangeBeacon != null && display) {

//                    Timber.i(">>>>>  didRangeBeaconsInRegion() beacon to display: " + inRangeBeacon.getBluetoothAddress());

                    if (inRangeBeacon.getBluetoothAddress().equals(MACHINING_BEACON_ID) &&
                            !mLastLocation.equals(MACHINING_BEACON_ID)) {
                        // bedroom
                        mLastLocation = MACHINING_BEACON_ID;
                        //LocationDialog dialogFrag = LocationDialog.newInstance("You are now in the bedroom.");
                        //dialogFrag.show(getFragmentManager(), "bedroom");

                        Log.i(TAG, "Machining Section beacon found...distance: " + inRangeBeacon.getDistance());
                        //mHouseLayoutView.blinkOff();
                        mCompanyLayoutView.blinkOn(mAreas[LOCATION_MACHINING_INDEX]);

                    } else if (inRangeBeacon.getBluetoothAddress().equals(RECEIVING_BEACON_ID) &&
                            !mLastLocation.equals(RECEIVING_BEACON_ID)) {

                        mLastLocation = RECEIVING_BEACON_ID;
                        Log.i(TAG, "Receiving Section beacon found...distance: " + inRangeBeacon.getDistance());

                        mCompanyLayoutView.blinkOn(mAreas[LOCATION_RECEIVING_INDEX]);
                    }

                } else {  // no beacon signal
                    mLastLocation = "NONE";
                    mCompanyLayoutView.blinkOff();
                    Log.i(TAG, "No beacon within " + BEACON_MAXIMUM_DISTANCE + " meters.");
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region(getPackageName(), null, null, null));

        } catch (RemoteException e) {
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_company_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.animator_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.hide_log : R.string.show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.animator_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.


        LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Timber.i(">>>>>>  initializeLogging() .... Ready.... ");
    }
}
