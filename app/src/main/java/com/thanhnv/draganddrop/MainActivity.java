package com.thanhnv.draganddrop;

import android.app.FragmentManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements OnTouchListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TO_LEFT = 1, TO_RIGHT = 2, STAY_TO_LEFT = 3, STAY_TO_RIGHT = 4;
    private static final int DISTANT_TO_CHANGE_PAGER = 130; // pixel
    private static final int NUMBER_OF_PAGER = 3;
    private static final int NUMBER_OF_DATA_PAGER = 3;
    private static final int ID_VIEW = 1000;

    private FrameLayout[] arrPager;
    private FrameLayout virureLayout;
    private RelativeLayout main;
    private RelativeLayout.LayoutParams mainParams; // -- change

    private int curentPosition = 0;
    private int curentPositionArrDataPager = 0;
    private int widthScreen;
    private FragmentManager fragmentManager = getFragmentManager();

    // about of touching....
    private int startX;
    private int startParamsLeft;
    private boolean isChangingPager = false;

    // content of pager ..
    private PagerFragment[] arrPagerFragment;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        main.setOnTouchListener(this);

    }

    private void setContentOfPager(int orient) {
        if (orient == TO_RIGHT){
            int preCurentPosition = curentPosition - 1;
            int theLeftPosionInLinkList = curentPosition - 2;
            if (preCurentPosition < 0){
                preCurentPosition = NUMBER_OF_PAGER - 1;
            }

            if (theLeftPosionInLinkList < 0){
                theLeftPosionInLinkList += NUMBER_OF_PAGER;
            }

            int theRightPositionInLinkList = theLeftPosionInLinkList - 1;
            if (theRightPositionInLinkList < 0){
                theRightPositionInLinkList += NUMBER_OF_PAGER;
            }



            RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) arrPager[preCurentPosition].getLayoutParams();
            leftParams.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

            RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) arrPager[theLeftPosionInLinkList].getLayoutParams();
            rightParams.addRule(RelativeLayout.RIGHT_OF, arrPager[theRightPositionInLinkList].getId());

            int preCurentPositionArrData = curentPositionArrDataPager - 2;
            if (preCurentPositionArrData < 0){
                preCurentPositionArrData += arrPagerFragment.length;
            }
            int nexCurentPositionArrData = curentPositionArrDataPager + 1;
            if (nexCurentPositionArrData >= arrPagerFragment.length){
                nexCurentPositionArrData = 0;
            }

            Log.d(TAG,  "cur : " + curentPositionArrDataPager + "\n"
                    +   "pre : " + preCurentPositionArrData + "\n"
                    +   "nex : " + nexCurentPositionArrData + "\n"
            );

            fragmentManager.beginTransaction().remove(arrPagerFragment[preCurentPositionArrData]).commit();

            fragmentManager.beginTransaction()
                    .replace(arrPager[theLeftPosionInLinkList].getId(), arrPagerFragment[nexCurentPositionArrData])
                    .commit();
        } else {
            int theRightPositionInLinkList = curentPosition - 1;
            if (theRightPositionInLinkList < 0){
                theRightPositionInLinkList = NUMBER_OF_PAGER - 1;
            }

            RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) arrPager[theRightPositionInLinkList].getLayoutParams();
            leftParams.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

            RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) arrPager[curentPosition].getLayoutParams();
            rightParams.addRule(RelativeLayout.RIGHT_OF, arrPager[theRightPositionInLinkList].getId());

            int preCurentPositionArrData = curentPositionArrDataPager - 1;
            if (preCurentPositionArrData < 0){
                preCurentPositionArrData += arrPagerFragment.length;
            }
            int nexCurentPositionArrData = curentPositionArrDataPager + 2;
            if (nexCurentPositionArrData >= arrPagerFragment.length){
                nexCurentPositionArrData = 0;
            }

            Log.d(TAG,  "cur : " + curentPositionArrDataPager + "\n"
                    +   "pre : " + preCurentPositionArrData + "\n"
                    +   "nex : " + nexCurentPositionArrData + "\n"
            );

            fragmentManager.beginTransaction().remove(arrPagerFragment[nexCurentPositionArrData]).commit();

            fragmentManager.beginTransaction()
                    .replace(arrPager[theRightPositionInLinkList].getId(), arrPagerFragment[preCurentPositionArrData])
                    .commit();
        }
    }

    private void initData() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        widthScreen = size.x;

        // ---------------------- content of pager ---------------------

        arrPagerFragment = new PagerFragment[NUMBER_OF_DATA_PAGER];
        for (int i = 0; i < arrPagerFragment.length; i++){
            arrPagerFragment[i] = new PagerFragment();
        }

        // ---------------------- content of pager ---------------------
        arrPager = new FrameLayout[NUMBER_OF_PAGER];


    }

    private void initView() {
        main = (RelativeLayout) findViewById(R.id.frame);

        if(!(main.getParent() instanceof FrameLayout)){
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }

        virureLayout = new FrameLayout(this);
        virureLayout.setId(ID_VIEW -1);

        FrameLayout.LayoutParams virtureParams = new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        main.addView(virureLayout, virtureParams);

        for (int i = 0; i < NUMBER_OF_PAGER; i++){
            arrPager[i] = new FrameLayout(this);
            arrPager[i].setId(ID_VIEW + i);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(widthScreen, ViewGroup.LayoutParams.MATCH_PARENT);
            main.addView(arrPager[i], frameParams);
        }

        RelativeLayout.LayoutParams theBeginPosition = (RelativeLayout.LayoutParams) arrPager[NUMBER_OF_PAGER - 1].getLayoutParams();
        theBeginPosition.addRule(RelativeLayout.RIGHT_OF, virureLayout.getId());

        RelativeLayout.LayoutParams theCurentPosition = (RelativeLayout.LayoutParams) arrPager[0].getLayoutParams();
        theCurentPosition.addRule(RelativeLayout.RIGHT_OF, arrPager[NUMBER_OF_PAGER-1].getId());

        for (int i = 1; i < NUMBER_OF_PAGER - 1; i++){
            RelativeLayout.LayoutParams relativePagerParams = (RelativeLayout.LayoutParams) arrPager[i].getLayoutParams();
            relativePagerParams.addRule(RelativeLayout.RIGHT_OF, arrPager[i-1].getId());
        }

        mainParams = (RelativeLayout.LayoutParams) main.getLayoutParams();
        Log.d(TAG, mainParams.toString());
        mainParams.width = widthScreen * NUMBER_OF_PAGER;
        mainParams.leftMargin = -widthScreen;
//        mainParams.rightMargin = widthScreen * (NUMBER_OF_PAGER+1);

        Log.d(TAG, widthScreen + "\n"
                + mainParams.leftMargin + "\n"
        );

        main.requestLayout();

        // ----- set data for pager ---------

        initDataForPage(curentPositionArrDataPager);


        // ----- set data for pager ---------
    }

    private void initDataForPage(int curentPositionArrDataPager) {
        int preCurentPosition, preCurentPositionArrData;
        int nexCurentPosition, nexCurentPositionArrData;
        preCurentPosition = curentPosition - 1;
        preCurentPositionArrData = curentPositionArrDataPager - 1;

        if (preCurentPosition < 0){
            preCurentPosition += NUMBER_OF_PAGER;
        }
        if (preCurentPositionArrData < 0){
            preCurentPositionArrData += arrPagerFragment.length;
        }
        nexCurentPosition = curentPosition + 1;
        nexCurentPositionArrData = curentPositionArrDataPager + 1;
        if (nexCurentPosition >= NUMBER_OF_PAGER){
            nexCurentPosition = 0;
        }
        if (nexCurentPositionArrData >= arrPagerFragment.length ){
            nexCurentPositionArrData = 0;
        }


        fragmentManager.beginTransaction()
                .replace(arrPager[curentPosition].getId(), arrPagerFragment[curentPositionArrDataPager])
                .replace(arrPager[preCurentPosition].getId(), arrPagerFragment[preCurentPositionArrData])
                .replace(arrPager[nexCurentPosition].getId(), arrPagerFragment[nexCurentPositionArrData])
                .commit();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (isChangingPager){
            return false;
        }
        if (view.getId() != R.id.frame) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startParamsLeft = mainParams.leftMargin;
                Log.d(TAG, "lef : " + startParamsLeft + " " + event.getY());
//                Log.d(TAG, "down : " + startParamsLeft);
                break;

            case MotionEvent.ACTION_MOVE:
                int distanceMovePager = (int) event.getX() - startX;
                mainParams.leftMargin += distanceMovePager;
                mainParams.rightMargin -= distanceMovePager;
//                Log.d(TAG, "para : " + mainParams.leftMargin);
                view.setLayoutParams(mainParams);
                break;

            case MotionEvent.ACTION_UP:
                int movingDirectionFlag;
                int curentParamsLeft = mainParams.leftMargin;
                int distanceMovedMouse = curentParamsLeft - startParamsLeft;

//                Log.d(TAG, "up : " + curentParamsLeft);

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
                        if (curentPosition >= NUMBER_OF_PAGER){
                            curentPosition = 0;
                        }
                        if (curentPositionArrDataPager >= arrPagerFragment.length){
                            curentPositionArrDataPager = 0;
                        }
                    } else {
                        // to left
                        movingDirectionFlag = TO_LEFT;
                        curentPosition --;
                        curentPositionArrDataPager --;
                        if (curentPosition < 0){
                            curentPosition = NUMBER_OF_PAGER - 1;
                        }
                        if (curentPositionArrDataPager < 0){
                            curentPositionArrDataPager = arrPagerFragment.length - 1;
                        }
                    }
                }

                MyAsyntask myAsyntask = new MyAsyntask();
                myAsyntask.execute(mainParams.leftMargin, mainParams.rightMargin, movingDirectionFlag);
                break;
            default:
                break;
        }

        return true;
    }

    private class MyAsyntask extends AsyncTask<Integer, Integer, Boolean>{
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

                        if (paramsLeft >= -widthScreen){
                            flagStop = true;
                        }

                        break;
                    case STAY_TO_RIGHT:
                        paramsLeft -= 10;
                        paramsRight += 10;

                        if (paramsLeft <= -widthScreen){
                            flagStop = true;
                        }
                        break;
                    case TO_RIGHT:
                        paramsLeft -= 10;
                        paramsRight += 10;

                        if (paramsLeft <= -widthScreen*2){
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
            mainParams.leftMargin = values[0];
            mainParams.rightMargin = values[1];
            main.requestLayout();
        }

        @Override
        protected void onPostExecute(Boolean change) {
            if (change){
                setContentOfPager(orient);
            }
            isChangingPager = false;

            mainParams.leftMargin = -widthScreen;
//            mainParams.rightMargin = widthScreen * (NUMBER_OF_PAGER+1);
            main.requestLayout();
        }
    }
}