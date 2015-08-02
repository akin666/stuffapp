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

    private Pan panDetection = new Pan();
    private Pinch pinchDetection = new Pinch();
    private Rotate rotateDetection = new Rotate();

    private PointF point1 = new PointF();
    private PointF point2 = new PointF();

    private int ptrID1, ptrID2;

    public PointF origin = new PointF(0,0);
    public PointF delta = new PointF(0,0);

    public PointF start = new PointF(0,0);
    public PointF middle = new PointF(0,0);

    private int count = 0;
/*
    private PointF[] points = new PointF[10];
    private int[] pointsID = new int[10];
    private int iter = 0;
*/
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

    public Pan pan() {
        return panDetection;
    }

    public Pinch pinch() {
        return pinchDetection;
    }

    public Rotate rotate() {
        return rotateDetection;
    }

    public GestureDetection(OnGestureListener listener, View view) {
        this.angle = 0.0f;
        this.scale = 1.0f;
        delta.x = 0;
        delta.y = 0;

        this.listener = listener;
        this.view = view;
    }

    public void reset() {
        this.angle = 0.0f;
        this.scale = 1.0f;
        delta.x = 0;
        delta.y = 0;
    }

    public boolean onTouchEvent(MotionEvent event){
        count = event.getPointerCount();
        final int action = event.getActionMasked();

        final int index = event.getActionIndex();
        final int id = event.getPointerId(index);

        Log.w(Constants.AppName, "Count: " + count + " Action: " + action + " index: " + index + " id: " + id );

        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_POINTER_DOWN: {
                middle = getMiddle(event);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                middle = getMiddle(event, index);
                --count;
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                getRawPoint(event, id, middle);
                start.set(middle);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                // the pointer ended, do not sniff anymore around..
                //getRawPoint(event, id, middle);
                count = 0;
                break;
            }
            default:
                break;
        }

        switch( action ) {
            case MotionEvent.ACTION_MOVE: {
                panDetection.move(this);
                pinchDetection.move(this);
                rotateDetection.move(this);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                panDetection.begin(this);
                pinchDetection.begin(this);
                rotateDetection.begin(this);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                panDetection.end(this);
                pinchDetection.end(this);
                rotateDetection.end(this);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                panDetection.cancel(this);
                pinchDetection.cancel(this);
                rotateDetection.cancel(this);
                break;
            }
            default:
                break;
        }

        /*
        final PointF beta;
        if( action == MotionEvent.ACTION_POINTER_UP) {
            beta = getMiddle(event , event.getActionIndex());
        } else {
            beta = getMiddle(event);
        }

        // Move logic
        switch(action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                origin.set(beta.x - delta.x, beta.y - delta.y);
                break;
            case MotionEvent.ACTION_MOVE:
                delta.set(beta.x - origin.x, beta.y - origin.y);
                break;
            case MotionEvent.ACTION_DOWN:
                origin.set(beta);
                delta.set(0 , 0);
                if (listener != null) {
                    listener.beginMove(this);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                delta.set(0 , 0);
                if (listener != null) {
                    listener.endMove(this);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.endMove(this);
                }
                delta.set(0 , 0);
                break;
            default:
                break;
        }

        boolean assigned = false;
        // Scale rotate logic
        switch (action) {
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_DOWN:
                ptrID1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if( ptrID1 == INVALID_POINTER_ID ) {
                    assigned = true;
                    ptrID1 = event.getPointerId(event.getActionIndex());
                }
                else if( ptrID2 == INVALID_POINTER_ID ) {
                    assigned = true;
                    ptrID2 = event.getPointerId(event.getActionIndex());
                }

                if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID && assigned) {
                    getRawPoint(event, ptrID1, point1);
                    getRawPoint(event, ptrID2, point2);

                    scaleDistance = 1.0f / distanceBetweenPoints(point1, point2);

                    if (listener != null) {
                        listener.beginGesture(this);
                    }
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
                ptrID2 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if( event.getActionIndex() == ptrID1 ) {
                    assigned = true;
                    ptrID1 = INVALID_POINTER_ID;
                }
                if( event.getActionIndex() == ptrID2 ) {
                    assigned = true;
                    ptrID2 = INVALID_POINTER_ID;
                }
                if ((ptrID1 == INVALID_POINTER_ID || ptrID2 == INVALID_POINTER_ID) && assigned) {
                    if (listener != null) {
                        listener.endGesture(this);
                    }
                    scale = 1.0f;
                    angle = 0.0f;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                ptrID1 = INVALID_POINTER_ID;
                ptrID2 = INVALID_POINTER_ID;

                scale = 1.0f;
                angle = 0.0f;
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
        */

        return true;
    }

    private PointF getMiddle( PointF p , PointF p2 ) {
        return new PointF(
                p.x + (PointF.length( p.x , p2.x ) / 2.0f) ,
                p.y + (PointF.length( p.y , p2.y ) / 2.0f )
        );
    }

    private PointF getMiddle( MotionEvent ev ) {
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

    private PointF getMiddle(MotionEvent ev , int skipIndex) {
        PointF point = new PointF(0,0);
        int count = ev.getPointerCount();
        final int[] location = { 0, 0 };
        view.getLocationOnScreen(location);

        if( count <= 1 ) {
            return point;
        }

        for (int i = 0; i < count; ++i) {
            if( i == skipIndex ) {
                continue;
            }

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

        point.x /= (count - 1);
        point.y /= (count - 1);

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

    public static class Rotate {
        private Listener listener;
        private float angle;

        public void cancel( GestureDetection detection ) {
        }

        public void begin( GestureDetection detection ) {
        }

        public void move( GestureDetection detection ) {
        }

        public void end( GestureDetection detection ) {
        }

        public float getAngle() {
            return angle;
        }

        public void set( Listener listener ) {
            this.listener = listener;
        }

        public interface Listener {
            void onCancel( Rotate rotate );
            void onBegin( Rotate rotate );
            void onMove( Rotate rotate );
            void onEnd( Rotate rotate );
        }
    }

    public static class Pinch {
        private Listener listener;
        private float scale;

        public void cancel( GestureDetection detection ) {
            if( listener != null ) {
                listener.onCancel(this);
            }
        }

        public void begin( GestureDetection detection ) {
        }

        public void move( GestureDetection detection ) {
        }

        public void end( GestureDetection detection ) {
            if( detection.count == 1 ) {
                if( listener != null ) {
                    listener.onEnd(this);
                }
                return;
            }
            //origin.set( detection.middle.x - delta.x , detection.middle.y - delta.y );
        }

        public float getScale() {
            return scale;
        }

        public void set( Listener listener ) {
            this.listener = listener;
        }

        public interface Listener {
            void onCancel( Pinch pinch );
            void onBegin( Pinch pinch );
            void onMove( Pinch pinch );
            void onEnd( Pinch pinch );
        }
    }

    public static class Pan {
        private Listener listener;
        private PointF delta = new PointF(0,0);
        private PointF origin = new PointF(0,0);

        public void cancel( GestureDetection detection ) {
            if( listener != null ) {
                listener.onCancel(this);
            }
        }

        public void begin( GestureDetection detection ) {
            if( detection.count == 1 ) {
                delta.set(0,0);
                origin.set(detection.middle);
                if( listener != null ) {
                    listener.onBegin(this);
                }
                return;
            }
            origin.set( detection.middle.x - delta.x , detection.middle.y - delta.y );
        }

        public void move( GestureDetection detection ) {
            delta.set( detection.middle.x - origin.x , detection.middle.y - origin.y  );
            if( listener != null ) {
                listener.onMove(this);
            }
        }

        public void end( GestureDetection detection ) {
            if( detection.count == 0 ) {
                if( listener != null ) {
                    listener.onEnd(this);
                }
                return;
            }
            origin.set( detection.middle.x - delta.x , detection.middle.y - delta.y );
        }

        public PointF getDelta() {
            return delta;
        }

        public void set( Listener listener ) {
            this.listener = listener;
        }

        public interface Listener {
            void onCancel( Pan pan );
            void onBegin( Pan pan );
            void onMove( Pan pan );
            void onEnd( Pan pan );
        }
    }
}