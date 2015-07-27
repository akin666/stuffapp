package net.icegem.stuffapp;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mikael.korpela on 27.7.2015.
 *
 * based on
 * http://stackoverflow.com/questions/10682019/android-two-finger-rotation
 */
public class GestureDetection {
    private static final int INVALID_POINTER_ID = -1;
    private PointF point1 = new PointF();
    private PointF point2 = new PointF();
    private int ptrID1, ptrID2;

    public PointF origin = new PointF(0,0);
    public PointF delta = new PointF(0,0);

    private float scaleDistance;

    private float angle;
    private float scale;

    private View view;

    private OnGestureListener listener;

    public float getAngle() {
        return angle;
    }

    public float getScale() {
        return scale;
    }

    public PointF getPoint() {
        return origin;
    }

    public PointF getMoveDelta() {
        return delta;
    }

    public GestureDetection(OnGestureListener listener, View view) {
        this.angle = 0.0f;
        this.scale = 1.0f;
        delta.x = 0;
        delta.y = 0;

        this.listener = listener;
        this.view = view;
        this.ptrID1 = INVALID_POINTER_ID;
        this.ptrID2 = INVALID_POINTER_ID;
    }

    public void reset() {
        this.angle = 0.0f;
        this.scale = 1.0f;
        delta.x = 0;
        delta.y = 0;
    }

    public boolean onTouchEvent(MotionEvent event){
        PointF middle = getMiddle(event);
        int action = event.getActionMasked();

        Log.w(Constants.AppName, "---------------------------------");
        // Move logic
        switch(action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                origin.set(middle.x - delta.x , middle.y - delta.y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                origin.set(middle.x - delta.x , middle.y - delta.y);
                break;
            case MotionEvent.ACTION_MOVE:
                delta.set( middle.x - origin.x, middle.y - origin.y );
                break;
            case MotionEvent.ACTION_DOWN:
                origin.set(middle);
                delta.set(0 , 0);
                if (listener != null) {
                    listener.beginMove(this);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.endMove(this);
                }
                delta.set(0 , 0);
                break;
            default:
                break;
        }

        // Scale rotate logic
        switch (action) {
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_DOWN:
                ptrID1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                ptrID2 = event.getPointerId(event.getActionIndex());

                getRawPoint(event, ptrID1, point1);
                getRawPoint(event, ptrID2, point2);

                scaleDistance = 1.0f / distanceBetweenPoints( point1 , point2 );

                if (listener != null) {
                    listener.beginGesture(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID){
                    PointF tpoint1 = new PointF();
                    PointF tpoint2 = new PointF();

                    getRawPoint(event, ptrID1, tpoint1);
                    getRawPoint(event, ptrID2, tpoint2);

                    angle = angleBetweenLines(point1, point2, tpoint1, tpoint2);
                    scale = scaleDistance * distanceBetweenPoints(tpoint1, tpoint2);
                }
                break;
            case MotionEvent.ACTION_UP:
                ptrID1 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                ptrID2 = INVALID_POINTER_ID;
                if (listener != null) {
                    listener.endGesture(this);
                }
                scale = 1.0f;
                angle = 0.0f;
                break;
            case MotionEvent.ACTION_CANCEL:
                ptrID1 = INVALID_POINTER_ID;
                ptrID2 = INVALID_POINTER_ID;

                if (listener != null) {
                    listener.endGesture(this);
                }
                break;
            default:
                break;
        }

        if( action == MotionEvent.ACTION_MOVE ) {
            if (listener != null) {
                listener.onGesture(this);
            }
        }

        return true;
    }

    private PointF getMiddle( PointF p , PointF p2 ) {
        return new PointF(
                p.x + (PointF.length( p.x , p2.x ) / 2.0f) ,
                p.y + (PointF.length( p.y , p2.y ) / 2.0f )
        );
    }

    private PointF getMiddle(MotionEvent ev) {
        PointF point = new PointF(0,0);
        final int[] location = { 0, 0 };
        view.getLocationOnScreen(location);

        int count = ev.getPointerCount();
        for (int i = 0; i < count; ++i) {
            float x = ev.getX(i);
            float y = ev.getY(i);

            double angle = Math.toDegrees(Math.atan2(y, x));
            angle += view.getRotation();

            final float length = PointF.length(x, y);

            x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
            y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

            point.x += x;
            point.y += y;
        }

        point.x /= count;
        point.y /= count;

        return point;
    }

    void getRawPoint(MotionEvent ev, int index, PointF point){
        final int[] location = { 0, 0 };
        view.getLocationOnScreen(location);

        float x = ev.getX(index);
        float y = ev.getY(index);

        double angle = Math.toDegrees(Math.atan2(y, x));
        angle += view.getRotation();

        final float length = PointF.length(x, y);

        x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
        y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

        point.set(x, y);
    }

    private float angleBetweenLines(PointF fPoint, PointF sPoint, PointF nFpoint, PointF nSpoint)
    {
        float angle1 = (float) Math.atan2((fPoint.y - sPoint.y), (fPoint.x - sPoint.x));
        float angle2 = (float) Math.atan2((nFpoint.y - nSpoint.y), (nFpoint.x - nSpoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    private float distanceBetweenPoints(PointF point1, PointF point2) {
        return PointF.length(point1.x - point2.x , point1.y - point2.y);
    }

    public interface OnGestureListener {
        void beginMove(GestureDetection detection );
        void beginGesture(GestureDetection detection );
        void onGesture(GestureDetection detection );
        void endGesture(GestureDetection detection );
        void endMove(GestureDetection detection );
    }
}