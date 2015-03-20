package application.beacon.industrial.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 *
 */
public class CompanyLayoutView extends View implements DoubleTapHandler {

    //private static final String TAG = CompanyLayoutView.class.getSimpleName();

    private static final int LEFT_MARGIN_OFFSET = 25;
    private static final int TOP_MARGIN_OFFSET = 25;

    private static final int LOCATION_MACHINING_INDEX = 0;
    private static final int LOCATION_RECEIVING_INDEX = 1;
    private static final int BLINK_DURATION = 350;

    private Context mContext;
    private Path mPath;
    private Paint mPaint;

    private GestureDetector mGestureDetector;

    private LayoutArea[] mLayoutAreas;
    private AreaDoubleTapListener mAreaDoubleTapListener;

    // blinking of beacon indicator variables
    private boolean mBlinkOn = false;
    private boolean mShow = true;
    private long mLastUpdateTime = 0;
    private long mBlinkStart = 0;
    private int mBlinkX, mBlinkY;
    private LayoutArea mCurrentBlinkingLocation;

    public interface AreaDoubleTapListener {
        void doubleTappedArea(LayoutArea layoutArea);
    }

    public CompanyLayoutView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CompanyLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        Timber.i(">>>>> CompanyLayoutView(Context context, AttributeSet attrs) ....");
        init();
    }


    private void init() {
        Timber.i(">>>>> init() enter....");
        mGestureDetector = new GestureDetector(mContext, new GestureListener(this));
//        mGestureDetector.setOnDoubleTapListener(new GestureListener(this));
//        mDetector = new GestureDetectorCompat(getContext(), this);
//        mDetector.setOnDoubleTapListener(this);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK); //Color.argb(0xff, 0x99, 0x00, 0x00));
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setShadowLayer(7, 0, 0, Color.RED);
        mPath = new Path();
        //Timber.i(">>>>> init() exit....");
    }

    public void setLayoutAreas(LayoutArea[] layoutAreas) {
        mLayoutAreas = layoutAreas;
    }


    /*
         *  When an area is touch, the pass the event to
         *  gesture detector.
         */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG, ">>>>> onTouchEvent().....");
        Timber.i(">>>>>>> onTouchEvent().....");
        return mGestureDetector.onTouchEvent(event);
        //this.mDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);
    }

    /*
     *  Sets a callback for double tap listener
     */
    public void setAreaDoubleTapListener(AreaDoubleTapListener areaDoubleTapListener) {
        mAreaDoubleTapListener = areaDoubleTapListener;
    }

    public void doubleTapHandler(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        Timber.i(">>>>> onDoubleTapEvent() eventX: " + eventX + "  eventY: " + eventY);

        if (eventX >= 575f && eventX <= 975f
                && eventY >= 125f && eventY <= 425f) {  // Receiving rectangle area

            Timber.i(">>>>> this is the Receiving.");
            if (mAreaDoubleTapListener != null &&
                    mCurrentBlinkingLocation != null &&
                    mCurrentBlinkingLocation.getName().equals(mLayoutAreas[LOCATION_RECEIVING_INDEX].getName())) {

                mAreaDoubleTapListener.doubleTappedArea(mLayoutAreas[LOCATION_RECEIVING_INDEX]);
            }

        } else if (eventX >= LEFT_MARGIN_OFFSET + 525 && eventX <= LEFT_MARGIN_OFFSET + 975f
                && eventY >= 825f && eventY <= 1395f) { // Assembly rectangle area

            Timber.i(">>>>> this is the Machining.");
            if (mAreaDoubleTapListener != null &&
                    mCurrentBlinkingLocation != null &&
                    mCurrentBlinkingLocation.getName().equals(mLayoutAreas[LOCATION_MACHINING_INDEX].getName())) {

                mAreaDoubleTapListener.doubleTappedArea(mLayoutAreas[LOCATION_MACHINING_INDEX]);
            }
        } else {
            Timber.i(">>>>> nothing to process.");
            //Log.i(TAG, ">>>>> no tap to process.");
        }

        //invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        //Timber.i(">>>>> onDraw(Canvas canvas) enter....");
        super.onDraw(canvas);
        canvas.translate(LEFT_MARGIN_OFFSET, TOP_MARGIN_OFFSET);
        //canvas.save();
        drawFloorLayout(canvas);
        updateBlinking();
        //canvas.restore();
        invalidate();
        //Timber.i(">>>>> onDraw(Canvas canvas) exit....");
    }

    /*
     * This setups the blinking and unblinking dot
     *
     */
    @Override
    public void dispatchDraw(Canvas canvas) {
        //Timber.i(">>>>>>>>>>>>> dispatchDraw() >>>>>>>>>>>>>>>>>>");
        Paint paint = new Paint();

        canvas.save();
        if (mShow && mBlinkOn) {  // display blinking dot
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            canvas.setDensity(5);
            canvas.drawCircle(mBlinkX, mBlinkY, 20f, paint);
        } else {                  // hide blinking dot
            canvas.setDensity(0);
        }
        canvas.restore();
        invalidate();
    }

    private void drawFloorLayout(Canvas canvas) {
        canvas.save();

        Path mPath = new Path(); // ????????????????
        boundary(mPath);
        divider(mPath);
        canvas.drawPath(mPath, mPaint);

        // Receiving
        Rect rect = new Rect(580, 125, 975, 425);
        Paint paintRect = new Paint();
        paintRect.setColor(Color.rgb(255, 229, 204));
        canvas.drawRect(rect, paintRect);

        // Packing/Shipping
        rect = new Rect(125, 125, 525, 575);
        paintRect.setColor(Color.rgb(255, 229, 204));
        canvas.drawRect(rect, paintRect);

        // Stockroom
        rect = new Rect(700, 475, 995, 775);
        paintRect.setColor(Color.rgb(255, 229, 204));
        canvas.drawRect(rect, paintRect);


        // Machining
        rect = new Rect(525, 825, 975, 1395);
        paintRect.setColor(Color.rgb(255, 229, 204));
        canvas.drawRect(rect, paintRect);

        // Assembly
        rect = new Rect(125, 625, 475, 1375);
        paintRect.setColor(Color.rgb(255, 229, 204));
        canvas.drawRect(rect, paintRect);


        canvas.drawPath(mPath, mPaint);

        canvas.restore();


        canvas.save();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(4);
        mPaint.setTextSize(48);

        canvas.drawText("Packing/Shipping", 135, 300, mPaint);
        canvas.drawText("Receiving", 700, 300, mPaint);
        canvas.drawText("Stockroom", 720, 600, mPaint);
        canvas.drawText("Machining", 650, 1100, mPaint);
        canvas.drawText("Assembly", 200, 1000, mPaint);

        canvas.restore();

    }

    /*
        *  This would draw the are layout(boundaries, dividers, text, etc) for viewing
        *
        */
    private void boundary(Path path) {
        path.moveTo(300, 100);
        path.lineTo(100, 100);
        path.lineTo(100, 1400);
        path.lineTo(1000, 1400);
        path.lineTo(1000, 100);
        path.lineTo(500, 100);
    }

    private void divider(Path path) {
        // Divider A
        //path.moveTo(350, 625);
        path.moveTo(350, 600);
        path.lineTo(100, 600);

        // Divider B
        path.moveTo(650, 800);
        path.lineTo(1000, 800);
        path.moveTo(675, 800);
        path.lineTo(675, 700);

        // Divider C
        path.moveTo(550, 350);
        path.lineTo(550, 600);
        path.lineTo(675, 600);

        // Divider D
        path.moveTo(550, 450);
        path.lineTo(1000, 450);
        path.moveTo(675, 450);
        path.lineTo(675, 475);
        path.moveTo(675, 575);
        path.lineTo(675, 625);

    }


    // ---- blinking of beacon indicator -----

    public void blinkOn(LayoutArea location) {
        mBlinkX = location.getX();
        mBlinkY = location.getY();
        mCurrentBlinkingLocation = location;
        mBlinkOn = true;
    }

//    public void blinkOn(int x, int y) {
//        mBlinkX = x;
//        mBlinkY = y;
//        mBlinkOn = true;
//    }

    public void blinkOff() {
        mCurrentBlinkingLocation = null;
        mBlinkOn = false;
    }

    /*
     *  Calculate and update the state of showing of the red dot
     *
     */
    public void updateBlinking() {

//        Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//        animation.setDuration(300); // duration - half a second
//        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
//        animation.setRepeatMode(Animation.REVERSE);

        if (System.currentTimeMillis() - mLastUpdateTime >= BLINK_DURATION
                && !mShow) {
            mShow = true;
            mBlinkStart = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - mBlinkStart >= BLINK_DURATION && mShow) {
            mShow = false;
            mLastUpdateTime = System.currentTimeMillis();
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener {

        private DoubleTapHandler mDoubleTapHandler;
//        private CompanyLayoutView mCompanyLayoutView;

        public GestureListener(DoubleTapHandler doubleTapHandler) {
            mDoubleTapHandler = doubleTapHandler;
        }

//        public GestureListener(CompanyLayoutView view) {
//            mCompanyLayoutView = view;
//        }

//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Timber.i("------------------- onSingleTapConfirmed(MotionEvent e)....");
//            return false;
//        }


        // This need to be present for onDoubleTap(MotionEvent e) to work
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Timber.i("????????????????????????????????????? onDoubleTap(MotionEvent e)....");
            if (mDoubleTapHandler != null) {
                mDoubleTapHandler.doubleTapHandler(e);
                return true;
            } else {
                return false;
            }

        }

//        // event when double tap occurs
//        @Override
//        public boolean onDoubleTapEvent(MotionEvent e) { //onDoubleTap(MotionEvent e) {
//            // let the listener handle the double tap event
////            Timber.i("+++++++++++++++++ onDoubleTapEvent(MotionEvent e)....");
//
//
//            return false;
//        }
    }
}
