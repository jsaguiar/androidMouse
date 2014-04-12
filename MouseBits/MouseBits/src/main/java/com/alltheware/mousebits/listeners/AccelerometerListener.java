package com.alltheware.mousebits.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import com.alltheware.mousebits.app.MouseActivity;
import com.alltheware.mousebits.objects.MoveRequest;
import com.alltheware.mousebits.signal.AccelerometerProcessor;

import java.util.ArrayList;

/**
 * Created by pedrocarmona on 10/04/14.
 */
public class AccelerometerListener implements SensorEventListener
{
    /**
     *
     * |--------------------Fetch Period-------------------------------|
     * 											|--Sample Collected----|
     *
     * Requirement:
     * 		Sample < Fetch Period
     *
     */


    /** Period for accelerometer sampling **/
    public static final long FETCH_PERIOD = 5;
    /** Length of each accelerometer sample **/
    public static final long SAMPLE_DURATION = 4;

    //ArrayList for store accelerometer sample data
    private ArrayList<Float> acXXValues = new ArrayList<Float>();
    private ArrayList<Float> acYYValues = new ArrayList<Float>();
    private ArrayList<Float> acZZValues = new ArrayList<Float>();

    //Flag to know if all collected samples were processed
    private boolean samplesProcessed = true;

    //Last registered activity from the last sample record
    private final int INVALID_SAMPLE=-1;
    private final int SAMPLES = 3;
    private int iLAST_ACTIVITY_INDEX = 0;
    private int[] LAST_ACTIVITY_INDEX = new int[SAMPLES];


    private MouseActivity mouseActivity;
    //Frequency Analyzer

    private float accelerometerValues[];

    public AccelerometerListener(MouseActivity mouseActivity)
    {
        this.mouseActivity = mouseActivity;
        this.accelerometerValues = new float[3];

        //only used for debug
        //createTimers();

        for(int i=0;i<SAMPLES;i++)
            LAST_ACTIVITY_INDEX[i]=INVALID_SAMPLE;
    }

    /**
     * This method is used only for debug and shows the accelerometer values
     */
	/*private void createTimers()
	{
		TimerTask timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				mapRouteActivity.runOnUiThread(new Runnable()
				{
					public void run()
					{
						String strResponse = "X: " + accelerometerValues[0] + " Y: " + accelerometerValues[1] + " Z: " + accelerometerValues[2];
						Geral.mostraToast(mapRouteActivity.getApplicationContext(), strResponse, Toast.LENGTH_SHORT);
					}
				});

			}
		};

		Timer timer = new Timer();
		timer.schedule(timerTask, 0, 4000);
	}*/

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub

    }

    public void onSensorChanged(SensorEvent event)
    {
        float acxx = accelerometerValues[0] = event.values[0];
        float acyy = accelerometerValues[1] = event.values[1];
        float aczz = accelerometerValues[2] = event.values[2];

        long timestamp = System.currentTimeMillis();

        int fftResponseXX= (int)(acxx* 10);
        int fftResponseYY=(int) (acyy * 10);
        int fftResponseZZ=(int) (aczz * 10);

            Log.i("Listener", "xx=" + fftResponseXX +  "     yy=" + fftResponseYY );
        mouseActivity.sendMove(new MoveRequest(fftResponseXX, fftResponseYY,fftResponseZZ ));



        if(true){
            return;
        }
        //Inside Sample recording Time Window
        if((timestamp%FETCH_PERIOD) > (FETCH_PERIOD-SAMPLE_DURATION))
        {
            //Double module = Math.sqrt(acxx*acxx+acyy*acyy+aczz*aczz);

            acXXValues.add(acxx);
            acYYValues.add(acyy);
            acZZValues.add(aczz);

            samplesProcessed = false;
        }

        //else, ignore incoming sample and process it if hasnt been processed yet
        else if(!samplesProcessed)
        {
            //LAST_ACTIVITY_INDEX[getAndUpdateActArrayIndex()] = classifySamples(acValues);

            //Log.i("Accel Val", "" + classifySamples(acValues));

            //samplesProcessed=true;

            //acValues = new ArrayList<Double>();


            processValues();
            samplesProcessed=true;

            acXXValues = new ArrayList<Float>();
            acYYValues = new ArrayList<Float>();
            acZZValues = new ArrayList<Float>();

        }
    }

    final int UP = 1000;
    final int DOWN = 1001;
    final int LEFT = 1002;
    final int RIGHT = 1003;
    private int processValues(){
        float xx = 0;
        float yy = 0;
        float zz = 0;
        for(int i =0; i<acXXValues.size(); i++){
            xx += acXXValues.get(i);
            yy += acYYValues.get(i);
            zz += acZZValues.get(i);
        }

        Double moduleX = Math.sqrt(xx*xx);
        Double moduleY = Math.sqrt(yy*yy);

        AccelerometerProcessor acp = new AccelerometerProcessor(acXXValues, acYYValues, acZZValues);

        acp.processData(mouseActivity);
        /*
        if(moduleX>moduleY){
            Log.d("meter", "xx");
        }else if(moduleY>moduleX) {
            Log.d("meter", "yy");

        }else{
            Log.d("meter", "equals");

        }
        */
        return UP;

    }
    /**
     * TODO assig relation between FFT coef sum and a suited value for LAST_ACTIVITY_INDEX
     * [1-5]
     *
     * @param acValues
     * @return
     */
    private int classifySamples(ArrayList<Double> acValues)
    {
        //double coefsSum = activityClassifier.classifyZ1AccStream(listToArray(acValues));
        //return (int)coefsSum;
        return 0;
    }

    /**
     * Utility function, ArrayList -> double[]
     *
     * @param list
     * @return
     */
    private double[] listToArray(ArrayList<Double> list)
    {
        double[] array = new double[list.size()];
        int i=0;

        for (Double f:list)
            array[i++]=(f != null ? f : 0);

        return array;
    }

    private int getAndUpdateActArrayIndex()
    {
        iLAST_ACTIVITY_INDEX++;
        iLAST_ACTIVITY_INDEX = iLAST_ACTIVITY_INDEX % SAMPLES;

        return iLAST_ACTIVITY_INDEX;
    }

    public int getLAST_ACTIVITY_INDEX()
    {
        int sum=0;
        int validSamples=0;

        for(int i=0;i<SAMPLES;i++)
        {
            if (LAST_ACTIVITY_INDEX[i] != INVALID_SAMPLE)
            {
                sum+=LAST_ACTIVITY_INDEX[i];
                validSamples++;
            }
        }

        int result = 0;

        if(validSamples > 0)
        {
            result = Math.round(sum/validSamples);
        }

        Log.i("Accel Result", ""+ result);


        return result;
    }
}
