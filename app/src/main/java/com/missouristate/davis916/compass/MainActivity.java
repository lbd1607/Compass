package com.missouristate.davis916.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Laura Davis CIS 262-902
 * 28 April 2018
 *
 * This app uses a sensor event and the accelerometer and magnetometer
 * to create a virtual compass.
 */

public class MainActivity extends Activity implements SensorEventListener{

    //Compass image on screen
    private ImageView compassImage;

    //Record compass angle in degrees
    private float currentDegree = 0.0f;

    //Sensor manager and the sensors to be monitored
    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private float[] accelerometer;
    private float[] geomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference the compass image in the layout
        compassImage = (ImageView) findViewById(R.id.imageView);

        //Initialize the sensor capabilities
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }//end onCreate

    @Override
    protected void onResume(){
        super.onResume();
        //SENSOR_DELAY_GAME is the only one that works
        mSensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }//end onResume()

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }//end onPause()

    @Override
    public void onSensorChanged(SensorEvent event){
        //Rotation animation set for 1000 milliseconds
        final int DELAY = 1000;

        //Collect data from an accelerometer-driven event
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometer = event.values;
        //COLLECT DATA FROM A MAGNETOMETER DRIVEN EVENT
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;

        //Check if both sensors caused the event
        if (accelerometer != null && geomagnetic != null){
            float r[] = new float[9];
            float i[] = new float[9];
            boolean foundRotation = SensorManager.getRotationMatrix(r, i, accelerometer, geomagnetic);

            //Rotation has occurred
            if (foundRotation){
                float orientation[] = new float[3];
                SensorManager.getOrientation(r, orientation);

                //Compute the x-axis rotation angle
                float degree = (float) Math.toDegrees(orientation[0]);

                //Create a rotation animation
                RotateAnimation animation = new RotateAnimation(currentDegree, -degree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                //Set the duration of the aninmation
                animation.setDuration(DELAY);

                //Set animation after the end of the transformation
                animation.setFillAfter(true);

                //Begin the animation
                compassImage.startAnimation(animation);
                currentDegree = -degree;
            }
        }
    }//end onSensorChanged()

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the menu
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }//end createOptionsMenu()

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action bar item clicks here. The action bar will
        //automatically handle clicks on the Home/Up button,
        //as long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected()

}//end MainActivity class
