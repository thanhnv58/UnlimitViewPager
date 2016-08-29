package com.thanhnv.draganddrop;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by thanh on 8/10/2016.
 */
public class InfiniteViewPager extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = InfiniteViewPager.class.getSimpleName();
    private static final int WIDTH_VIEW_DEFAULT = 500, HEIGHT_VIEW_DEFAULT = 300;
    private static final int TO_LEFT = 1, TO_RIGHT = 2, STAY_TO_LEFT = 3, STAY_TO_RIGHT = 4;
    private static final int LINEAR_LAYOUT = 1, FRAME_LAYOUT = 2, RELATIVE_LAYOUT = 3, OTHER_LAYOUT = 4;
    private static final int DISTANT_TO_CHANGE_PAGER = 130; // pixel
    private static final int DEFAULT_NUMBER = 3;
    private static final int ID_VIEW = 1000;

    private boolean isChangingPager = false;
    private boolean isStopping = false;
    private int widthView, heightView;
    private int startX, startParamsLeft;
    private int numberOfPagerData;
    private int curentPosition = 0;
    private int numberOfLoadedPager = DEFAULT_NUMBER;
    private int curentPositionArrDataPager = 0;
    private static int keyIdView = 0;

    private ViewGroup.LayoutParams mainParams;
    private int keyParentLayout;
    private FrameLayout[] arrPager;
    private FrameLayout virureLayout;

    private Context mContext;
    private FragmentManager fragmentManager;

    private InfinitePagerAdapter myAdapter;

    public void setInfiniteAdapter(InfinitePagerAdapter adapter){
        myAdapter = adapter;
        numberOfPagerData = myAdapter.getLength();
        if (numberOfLoadedPager > numberOfPagerData){
            numberOfLoadedPager = numberOfPagerData;
        }
    }

    public void setNumberOfLoadedPager(int numberOfLoadedPager){
        this.numberOfLoadedPager = numberOfLoadedPager;
    }

    public InfiniteViewPager(Context context) {
        super(context);
        mContext = context;
        fragmentManager = ((Activity)mContext).getFragmentManager();
        setOnTouchListener(this);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        fragmentManager = ((Activity)mContext).getFragmentManager();
        setOnTouchListener(this);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        fragmentManager = ((Activity)mContext).getFragmentManager();
        setOnTouchListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (isStopping){
            return;
        }
        widthView = getMeasuredWidth();
        heightView = getMeasuredHeight();
        if (widthView == 0){
            widthView = WIDTH_VIEW_DEFAULT;
        }
        if (heightView == 0){
            heightView = HEIGHT_VIEW_DEFAULT;
        }
        initData();
        initViews();
        isStopping = true;
    }

    private void initData(){
        arrPager = new FrameLayout[numberOfLoadedPager];
    }

    private void initViews(){
        // set parent params
        if (getParent() instanceof LinearLayout){
            keyParentLayout = LINEAR_LAYOUT;
            mainParams = (LinearLayout.LayoutParams)getLayoutParams();
            ((LinearLayout.LayoutParams)mainParams).width = widthView * numberOfLoadedPager;
            ((LinearLayout.LayoutParams)mainParams).leftMargin = -widthView;
            ((LinearLayout.LayoutParams)mainParams).rightMargin = widthView * (numberOfLoadedPager -1);
            requestLayout();
        } else if (getParent() instanceof RelativeLayout){
            keyParentLayout = RELATIVE_LAYOUT;
            mainParams = (RelativeLayout.LayoutParams)getLayoutParams();
            ((RelativeLayout.LayoutParams)mainParams).width = widthView * numberOfLoadedPager;
            ((RelativeLayout.LayoutParams)mainParams).leftMargin = -widthView;
            if (numberOfLoadedPager < 4) {
                ((RelativeLayout.LayoutParams) mainParams).rightMargin = widthView * 3;
            } else {
                ((RelativeLayout.LayoutParams) mainParams).rightMargin = widthView * (numberOfLoadedPager - 1);
            }
            requestLayout();
        } else if (getParent() instanceof FrameLayout){
            keyParentLayout = FRAME_LAYOUT;
            mainParams = (FrameLayout.LayoutParams)getLayoutParams();
            ((FrameLayout.LayoutParams)mainParams).width = widthView * numberOfLoadedPager;
            ((FrameLayout.LayoutParams)mainParams).leftMargin = -widthView;
            ((FrameLayout.LayoutParams)mainParams).rightMargin = widthView * (numberOfLoadedPager -1);
            requestLayout();
        } else {
            keyParentLayout = OTHER_LAYOUT;
            Log.e(TAG, "Not correct layout container !!!");
            return;
        }
        // set parent params

        // set content layout
        virureLayout = new FrameLayout(mContext);
        virureLayout.setId((keyIdView * ID_VIEW) -1);

        FrameLayout.LayoutParams virtureParams = new FrameLayout.LayoutParams(0, heightView);
        addView(virureLayout, virtureParams);

        keyIdView += 1;
        Log.d(TAG, "key id view " + keyIdView);
        for (int i = 0; i < numberOfLoadedPager; i++){
            arrPager[i] = new FrameLayout(mContext);
            arrPager[i].setId((keyIdView * ID_VIEW) + i);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(widthView, heightView);
            addView(arrPager[i], frameParams);
        }

        RelativeLayout.LayoutParams theBeginPosition = (RelativeLayout.LayoutParams) arrPager[numberOfLoadedPager - 1].getLayoutParams();
        theBeginPosition.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

        RelativeLayout.LayoutParams theCurentPosition = (RelativeLayout.LayoutParams) arrPager[0].getLayoutParams();
        theCurentPosition.addRule(RelativeLayout.RIGHT_OF, arrPager[numberOfLoadedPager -1].getId());

        for (int i = 1; i < numberOfLoadedPager - 1; i++){
            RelativeLayout.LayoutParams relativePagerParams = (RelativeLayout.LayoutParams) arrPager[i].getLayoutParams();
            relativePagerParams.addRule(RelativeLayout.RIGHT_OF, arrPager[i-1].getId());
        }
        // set content layout

        // set Data for pager ...
        initDataForPage();
        // set Data for pager ...
    }

    private void initDataForPage() {
        int preCurentPosition, preCurentPositionArrData;
        preCurentPosition = numberOfLoadedPager - 1;
        preCurentPositionArrData = numberOfPagerData - 1;

        fragmentManager.beginTransaction()
                .replace(arrPager[0].getId(), myAdapter.getFragmentPager(0))
                .replace(arrPager[preCurentPosition].getId(), myAdapter.getFragmentPager(preCurentPositionArrData))
                .commit();


        for (int i = 1, j = 1; i < numberOfLoadedPager - 1; i++, j++){
            fragmentManager.beginTransaction()
                    .replace(arrPager[i].getId(), myAdapter.getFragmentPager(j))
                    .commit();
        }
    }



    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (isChangingPager){
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                switch (keyParentLayout){
                    case LINEAR_LAYOUT:
                        startParamsLeft = ((LinearLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    case FRAME_LAYOUT:
                        startParamsLeft = ((FrameLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    case RELATIVE_LAYOUT:
                        startParamsLeft = ((RelativeLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    default:
                        break;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                int distanceMovePager = (int) event.getX() - startX;
                switch (keyParentLayout){
                    case LINEAR_LAYOUT:
                        ((LinearLayout.LayoutParams)mainParams).leftMargin += distanceMovePager;
                        ((LinearLayout.LayoutParams)mainParams).rightMargin -= distanceMovePager;
                        view.setLayoutParams((LinearLayout.LayoutParams)mainParams);
                        break;
                    case FRAME_LAYOUT:
                        ((FrameLayout.LayoutParams)mainParams).leftMargin += distanceMovePager;
                        ((FrameLayout.LayoutParams)mainParams).rightMargin -= distanceMovePager;
                        view.setLayoutParams((FrameLayout.LayoutParams)mainParams);
                        break;
                    case RELATIVE_LAYOUT:
                        ((RelativeLayout.LayoutParams)mainParams).leftMargin += distanceMovePager;
                        ((RelativeLayout.LayoutParams)mainParams).rightMargin -= distanceMovePager;
                        view.setLayoutParams((RelativeLayout.LayoutParams)mainParams);
                        break;
                    default:
                        Log.e(TAG, "other layout action move");
                        break;
                }
                break;

            case MotionEvent.ACTION_UP:
                int movingDirectionFlag;
                int curentParamsLeft = 0;

                switch (keyParentLayout){
                    case LINEAR_LAYOUT:
                        curentParamsLeft = ((LinearLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    case FRAME_LAYOUT:
                        curentParamsLeft = ((FrameLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    case RELATIVE_LAYOUT:
                        curentParamsLeft = ((RelativeLayout.LayoutParams)mainParams).leftMargin;
                        break;
                    default:
                        Log.e(TAG, "other layout action up");
                        break;
                }

                int distanceMovedMouse = curentParamsLeft - startParamsLeft;

                if (Math.abs(distanceMovedMouse) < DISTANT_TO_CHANGE_PAGER){
                    // stay ...
                    if (distanceMovedMouse <= 0){
                        movingDirectionFlag = STAY_TO_LEFT;
                    } else {
                        movingDirectionFlag = STAY_TO_RIGHT;
                    }

                } else {
                    if (distanceMovedMouse <= -DISTANT_TO_CHANGE_PAGER){
                        // to right
                        movingDirectionFlag = TO_RIGHT;
                        curentPosition ++;
                        curentPositionArrDataPager ++;

                        if (curentPosition >= numberOfLoadedPager){
                            curentPosition = 0;
                        }

                        if (curentPositionArrDataPager >= numberOfPagerData){
                            curentPositionArrDataPager = 0;
                        }
                    } else {
                        // to left
                        movingDirectionFlag = TO_LEFT;
                        curentPosition --;
                        curentPositionArrDataPager --;

                        if (curentPosition < 0){
                            curentPosition += numberOfLoadedPager;
                        }
                        if (curentPositionArrDataPager < 0){
                            curentPositionArrDataPager += numberOfPagerData;
                        }
                    }
                }
                int paramsLeft = 0, paramsRight = 0;
                MyAsyntask myAsyntask = new MyAsyntask();

                switch (keyParentLayout){
                    case LINEAR_LAYOUT:
                        paramsLeft = ((LinearLayout.LayoutParams)mainParams).leftMargin;
                        paramsRight = ((LinearLayout.LayoutParams)mainParams).rightMargin;
                        break;
                    case FRAME_LAYOUT:
                        paramsLeft = ((FrameLayout.LayoutParams)mainParams).leftMargin;
                        paramsRight = ((FrameLayout.LayoutParams)mainParams).rightMargin;
                        break;
                    case RELATIVE_LAYOUT:
                        paramsLeft = ((RelativeLayout.LayoutParams)mainParams).leftMargin;
                        paramsRight = ((RelativeLayout.LayoutParams)mainParams).rightMargin;
                        break;
                    default:
                        Log.e(TAG, "other layout action up");
                        break;
                }
                myAsyntask.execute(paramsLeft, paramsRight, movingDirectionFlag);
                break;
            default:
                break;
        }

        return true;
    }

    private class MyAsyntask extends AsyncTask<Integer, Integer, Boolean> {
        private int orient;

        @Override
        protected void onPreExecute() {
            isChangingPager = true;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            int paramsLeft = params[0];
            int paramsRight = params[1];

            boolean flagStop = false;

            while (!flagStop){
                publishProgress(paramsLeft, paramsRight);
                switch (params[2]){
                    case STAY_TO_LEFT:
                        paramsLeft += 10;
                        paramsRight -= 10;

                        if (paramsLeft >= -widthView){
                            flagStop = true;
                        }

                        break;
                    case STAY_TO_RIGHT:
                        paramsLeft -= 10;
                        paramsRight += 10;

                        if (paramsLeft <= -widthView){
                            flagStop = true;
                        }
                        break;
                    case TO_RIGHT:
                        paramsLeft -= 10;
                        paramsRight += 10;
                        if (paramsLeft <= -(widthView*2)){
                            flagStop = true;
                        }

                        orient = TO_RIGHT;
                        break;
                    case TO_LEFT:
                        paramsLeft += 10;
                        paramsRight -= 10;

                        if (paramsLeft >= 0){
                            flagStop = true;
                        }

                        orient = TO_LEFT;
                        break;
                    default:
                        break;
                }
                // delay ...
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return (params[2] == TO_LEFT || params[2] == TO_RIGHT);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // update view ....
            switch (keyParentLayout){
                case LINEAR_LAYOUT:
                    ((LinearLayout.LayoutParams)mainParams).leftMargin = values[0];
                    ((LinearLayout.LayoutParams)mainParams).rightMargin = values[1];
                    requestLayout();
                    break;
                case FRAME_LAYOUT:
                    ((FrameLayout.LayoutParams)mainParams).leftMargin = values[0];
                    ((FrameLayout.LayoutParams)mainParams).rightMargin = values[1];
                    requestLayout();
                    break;
                case RELATIVE_LAYOUT:
                    ((RelativeLayout.LayoutParams)mainParams).leftMargin = values[0];
                    ((RelativeLayout.LayoutParams)mainParams).rightMargin = values[1];
                    requestLayout();
                    break;
                default:
                    Log.e(TAG, "other layout postexcute");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Boolean change) {
            if (change){
                setContentOfPager(orient);
            }
            isChangingPager = false;

            switch (keyParentLayout){
                case LINEAR_LAYOUT:
                    ((LinearLayout.LayoutParams)mainParams).leftMargin = -widthView;
                    ((LinearLayout.LayoutParams)mainParams).rightMargin = widthView * (numberOfLoadedPager - 1);
                    requestLayout();
                    break;
                case FRAME_LAYOUT:
                    ((FrameLayout.LayoutParams)mainParams).leftMargin = -widthView;
                    ((FrameLayout.LayoutParams)mainParams).rightMargin = widthView * (numberOfLoadedPager - 1);
                    requestLayout();
                    break;
                case RELATIVE_LAYOUT:
                    ((RelativeLayout.LayoutParams)mainParams).leftMargin = -widthView;
                    if (numberOfLoadedPager < 4){
                        ((RelativeLayout.LayoutParams) mainParams).rightMargin = widthView * 3;
                    } else {
                        ((RelativeLayout.LayoutParams) mainParams).rightMargin = widthView * (numberOfLoadedPager - 1);
                    }
                    requestLayout();
                    break;
                default:
                    Log.e(TAG, "other layout postexcute");
                    break;
            }
        }
    }

    private void setContentOfPager(int orient) {
        if (orient == TO_RIGHT){
            int preCurentPosition = curentPosition - 1;
            int theLeftPosionInLinkList = curentPosition - 2;
            if (preCurentPosition < 0){
                preCurentPosition += numberOfLoadedPager;
            }

            if (theLeftPosionInLinkList < 0){
                theLeftPosionInLinkList += numberOfLoadedPager;
            }

            int theRightPositionInLinkList = theLeftPosionInLinkList - 1;
            if (theRightPositionInLinkList < 0){
                theRightPositionInLinkList += numberOfLoadedPager;
            }



            LayoutParams leftParams = (LayoutParams) arrPager[preCurentPosition].getLayoutParams();
            leftParams.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

            LayoutParams rightParams = (LayoutParams) arrPager[theLeftPosionInLinkList].getLayoutParams();
            rightParams.addRule(RelativeLayout.RIGHT_OF, arrPager[theRightPositionInLinkList].getId());




            int removePositionArrData = curentPositionArrDataPager - 2;
            if (removePositionArrData < 0){
                removePositionArrData += numberOfPagerData;
            }

            int theRightCurentPositionArrData = curentPositionArrDataPager + numberOfLoadedPager - 2;
            if (theRightCurentPositionArrData >= numberOfPagerData){
                theRightCurentPositionArrData -= numberOfPagerData;
            }
            if (numberOfLoadedPager == numberOfPagerData){
                return;
            }
            fragmentManager.beginTransaction().remove(myAdapter.getFragmentPager(removePositionArrData)).commit();

            fragmentManager.beginTransaction()
                    .replace(arrPager[theLeftPosionInLinkList].getId(), myAdapter.getFragmentPager(theRightCurentPositionArrData))
                    .commit();

            Log.d(TAG, "data cur : " + curentPositionArrDataPager + "\n"
                    +  "data rem : " + removePositionArrData + "\n"
                    +  "data rig : " + theRightCurentPositionArrData + "\n"
                    );

            Log.d(TAG, "fram cur : " + curentPosition + "\n"
                    +  "fram pre : " + preCurentPosition + "\n"
                    +  "fram rig : " + theRightPositionInLinkList + "\n"
                    +  "fram lef : " + theLeftPosionInLinkList + "\n"
            );

        } else {
            int theRightPositionInLinkList = curentPosition - 1;
            if (theRightPositionInLinkList < 0){
                theRightPositionInLinkList += numberOfLoadedPager;
            }

            LayoutParams leftParams = (LayoutParams) arrPager[theRightPositionInLinkList].getLayoutParams();
            leftParams.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

            LayoutParams rightParams = (LayoutParams) arrPager[curentPosition].getLayoutParams();
            rightParams.addRule(RelativeLayout.RIGHT_OF, arrPager[theRightPositionInLinkList].getId());




            int preCurentPositionArrData = curentPositionArrDataPager - 1;
            if (preCurentPositionArrData < 0){
                preCurentPositionArrData += numberOfPagerData;
            }

            int removePositionArrData = curentPositionArrDataPager + numberOfLoadedPager - 1;
            if (removePositionArrData >= numberOfPagerData){
                removePositionArrData -= numberOfPagerData;
            }
            if (numberOfLoadedPager == numberOfPagerData){
                return;
            }
            fragmentManager.beginTransaction().remove(myAdapter.getFragmentPager(removePositionArrData)).commit();

            fragmentManager.beginTransaction()
                    .replace(arrPager[theRightPositionInLinkList].getId(), myAdapter.getFragmentPager(preCurentPositionArrData))
                    .commit();
        }
    }
}
