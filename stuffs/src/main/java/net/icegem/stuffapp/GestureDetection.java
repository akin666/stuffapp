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
    private Pan panDetection = new Pan();
    private Pinch pinchDetection = new Pinch();
    private Rotate rotateDetection = new Rotate();

    private PointF start = new PointF(0,0);
    private PointF middle = new PointF(0,0);
    private PointF rawMiddle = new PointF(0,0);

    private int count = 0;

    private View view;

    public PointF getPoint() {
        return start;
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

    public GestureDetection(View view) {
        this.view = view;
    }

    public boolean onTouchEvent(MotionEvent event){
        count = event.getPointerCount();

        final int action = event.getActionMasked();
        final int index = event.getActionIndex();
        final int id = event.getPointerId(index);

        Log.w(Constants.AppName, "Count: " + count + " Action: " + action + " index: " + index + " id: " + id );

        rawMiddle = getMiddle(event);
        middle = transformToScreen(rawMiddle);
        switch (action) {
            case MotionEvent.ACTION_POINTER_UP: {
                --count;
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                start.set(middle);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                count = 0;
                break;
            }
            default:
                break;
        }

        switch( action ) {
            case MotionEvent.ACTION_MOVE: {
                panDetection.move(this, event);
                pinchDetection.move(this, event);
                rotateDetection.move(this, event);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                panDetection.begin(this, event);
                pinchDetection.begin(this, event);
                rotateDetection.begin(this, event);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                panDetection.end(this, event);
                pinchDetection.end(this, event);
                rotateDetection.end(this, event);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                panDetection.cancel(this, event);
                pinchDetection.cancel(this, event);
                rotateDetection.cancel(this, event);
                break;
            }
            default:
                break;
        }

        return true;
    }

    private PointF getMiddle(MotionEvent event) {
        PointF point = getRawMiddle(event);
        return transformToScreen(point);
    }

    private PointF getRawMiddle(MotionEvent event) {
        final int count = event.getPointerCount();
        final int action = event.getActionMasked();
        final int index = event.getActionIndex();

        PointF point = new PointF(0,0);

        int skip = -1;
        if( action == MotionEvent.ACTION_POINTER_UP ) {
            skip = index;
        }

        int counter = 0;
        for (int i = 0; i < count; ++i) {
            if( i == skip ) {
                continue;
            }

            point.x += event.getX(i);
            point.y += event.getY(i);
            ++counter;
        }

        if( counter != 0 ) {
            point.x /= counter;
            point.y /= counter;
        }

        return point;
    }

    private PointF transformToScreen(PointF point) {
        final int[] location = { 0, 0 };
        view.getLocationOnScreen(location);

        // Transform:
        double angle = Math.toDegrees(Math.atan2(point.y, point.x));
        angle += view.getRotation();

        final float length = PointF.length(point.x, point.y);

        return new PointF((float) (length * Math.cos(Math.toRadians(angle))) + location[0] , (float) (length * Math.sin(Math.toRadians(angle))) + location[1]);
    }

    private float getAngle(float x , float y) {
        float angle = (float) Math.toDegrees(Math.atan2(y - rawMiddle.y , x - rawMiddle.x));
        return Helpers.clampAngle(angle);
    }

    public static class Rotate {
        private Listener listener;
        private int[] trackID = new int[10];
        private float[] trackAngle = new float[10];
        private float delta = 0.0f;
        private int iterator = 0;

        private void remove( int id ) {
            int index = findIDIndex( id );
            if( index < 0 ) {
                return;
            }
            --iterator;
            if( index != iterator ) {
                trackID[index] = trackID[iterator];
                trackAngle[index] = trackAngle[iterator];
            }
        }

        private void add( int id , float angle ) {
            if( iterator >= trackID.length ) {
                return;
            }
            trackID[iterator] = id;
            trackAngle[iterator] = angle;
            ++iterator;
        }

        private void clear() {
            iterator = 0;
            delta = 0.0f;
        }

        private int findIDIndex(int id) {
            for( int i = 0 ; i < iterator ; ++i ) {
                if( trackID[i] == id ) {
                    return i;
                }
            }
            return -1;
        }

        public void cancel( GestureDetection detection , MotionEvent event ) {
            delta = 0.0f;
            if( listener != null ) {
                listener.onCancel(this);
            }
        }

        public void begin( GestureDetection detection , MotionEvent event ) {
            if( detection.count < 2 ) {
                return;
            }
            else if( detection.count == 2 ) {
                delta = 0.0f;
                if( listener != null ) {
                    listener.onBegin(this);
                }

                // setup all points
                for( int i = 0 ; i < detection.count ; ++i ) {
                    add( event.getPointerId(i) , Helpers.clampAngle(detection.getAngle(event.getX(i), event.getY(i)) - delta) );
                }
                return;
            }

            int index = event.getActionIndex();
            add( event.getPointerId(index) , Helpers.clampAngle(detection.getAngle(event.getX(index), event.getY(index)) - delta) );
        }

        public void move( GestureDetection detection , MotionEvent event ) {
            float deltaCollector = 0.0f;

            // update all existing angles
            int counter = 0;
            int count = event.getPointerCount();
            for( int i = 0 ; i < count ; ++i ) {
                int idx = findIDIndex(event.getPointerId(i));
                if( idx < 0 )
                {
                    continue;
                }
                deltaCollector += Helpers.shortestDistanceAngle( trackAngle[idx] , detection.getAngle(event.getX(i), event.getY(i)) );
                ++counter;
            }

            if( counter > 0 ) {
                delta = deltaCollector / counter;
            } else {
                delta = 0.0f;
            }

            if( listener != null ) {
                listener.onMove(this);
            }
        }

        public void end( GestureDetection detection , MotionEvent event ) {
            if( detection.count < 1 ) {
                return;
            }
            else if( detection.count == 1 ) {
                if( listener != null ) {
                    listener.onEnd(this);
                }
                clear();
                return;
            }

            // remove the removed ID
            int index = event.getActionIndex();
            remove(event.getPointerId(index));

            // update all existing angles
            int count = event.getPointerCount();
            for( int i = 0 ; i < count ; ++i ) {
                if( i == index ) {
                    continue;
                }

                int idx = findIDIndex( event.getPointerId(i) );
                if( idx > 0 ) {
                    trackAngle[idx] = Helpers.clampAngle(detection.getAngle(event.getX(i), event.getY(i)) - delta);
                }
            }
        }

        public float getDelta() {
            return delta;
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

    /**
     * At least 2 points, is needed for pinch
     */
    public static class Pinch {
        private Listener listener;
        private float scale = 1.0f;

        private float distance = 0.0f;

        private float getDistance(MotionEvent event) {
            final int count = event.getPointerCount();
            final int action = event.getActionMasked();
            final int index = event.getActionIndex();

            if( count < 2 ) {
                return 0.0f;
            }

            float length = 0.0f;
            PointF first = new PointF(0,0);
            PointF current = new PointF(0,0);
            PointF point = new PointF(0,0);

            int iter = 0;
            if( action == MotionEvent.ACTION_POINTER_UP ) {
                // skip one point..
                if( count < 3 ) {
                    return 0.0f;
                }

                if( iter != index ) {
                    point.x = event.getX(iter);
                    point.y = event.getY(iter);
                } else {
                    ++iter;
                    point.x = event.getX(iter);
                    point.y = event.getY(iter);
                }
                ++iter;

                first.set(point);

                for (; iter < count; ++iter) {
                    if( iter == index ) {
                        continue;
                    }

                    current.x = event.getX(iter);
                    current.y = event.getY(iter);

                    length += Helpers.distance(current, point);

                    point.set(current);
                }
            }
            else {
                point.x = event.getX(iter);
                point.y = event.getY(iter);
                ++iter;

                first.set(point);
                for (; iter < count; ++iter) {
                    current.x = event.getX(iter);
                    current.y = event.getY(iter);

                    length += Helpers.distance(current, point);

                    point.set(current);
                }
            }

            length += Helpers.distance(first, point);

            return length;
        }

        public void cancel( GestureDetection detection , MotionEvent event ) {
            scale = 1.0f;
            if( listener != null ) {
                listener.onCancel(this);
            }
        }

        public void begin( GestureDetection detection , MotionEvent event ) {
            if( detection.count < 2 ) {
                return;
            }
            else if( detection.count == 2 ) {
                scale = 1.0f;

                if( listener != null ) {
                    listener.onBegin(this);
                }
            }

            if( scale < (-Float.MAX_VALUE) ) {
                distance = Float.MAX_VALUE;
                return;
            }
            distance = getDistance(event) * (1.0f / scale);
        }

        public void move( GestureDetection detection , MotionEvent event ) {
            if( detection.count < 2 ) {
                return;
            }

            scale = getDistance(event) / distance;
            if( listener != null ) {
                listener.onMove(this);
            }
        }

        public void end( GestureDetection detection , MotionEvent event ) {
            if( detection.count == 1 ) {
                if( listener != null ) {
                    listener.onEnd(this);
                }
                return;
            }

            if( scale < (-Float.MAX_VALUE) ) {
                distance = Float.MAX_VALUE;
                return;
            }
            distance = getDistance(event) * (1.0f / scale);
        }

        public float getDelta() {
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

        public void cancel( GestureDetection detection , MotionEvent event ) {
            if( listener != null ) {
                listener.onCancel(this);
            }
        }

        public void begin( GestureDetection detection, MotionEvent event ) {
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

        public void move( GestureDetection detection , MotionEvent event ) {
            delta.set( detection.middle.x - origin.x , detection.middle.y - origin.y  );
            if( listener != null ) {
                listener.onMove(this);
            }
        }

        public void end( GestureDetection detection , MotionEvent event ) {
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