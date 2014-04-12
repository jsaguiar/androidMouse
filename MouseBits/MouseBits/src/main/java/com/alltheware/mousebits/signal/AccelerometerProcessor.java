package com.alltheware.mousebits.signal;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.alltheware.mousebits.app.MouseActivity;
import com.alltheware.mousebits.objects.MoveRequest;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by pedrocarmona on 10/04/14.
 */
public class AccelerometerProcessor {



    private ArrayList<Float> xx;
    private ArrayList<Float> yy;
    private ArrayList<Float> zz;
    Context context;

    public AccelerometerProcessor( ArrayList<Float> xx, ArrayList<Float> yy, ArrayList<Float> zz){
        this.context = context;
        this.xx = xx;
        this.yy = yy;
        this.zz = zz;






    }



    public int nextPowerOfTwoPlusOne(ArrayList<Float> input){

        Log.d("Sensor Data", "acMergedValues.size = " + input.size());

        int sizeActual =  input.size();

        int baseLimite = (int)(Math.log10(sizeActual)/Math.log10(2));

        Log.d("Sensor Data", "base="+Math.log10(sizeActual)/Math.log10(2));
        Log.d("Sensor Data", "baseLimite="+baseLimite);

        int limiar = (int)Math.pow(2,baseLimite+1);
        Log.d("Sensor Data", "limiar="+limiar);

        return limiar;


    }
    public  ArrayList<Float> grantArraySizeMultipleTwo(ArrayList<Float> input){

        ArrayList<Float> listToProcess = new ArrayList<Float>();

        int limiar = nextPowerOfTwoPlusOne(input);

        int i;

        for ( i =0 ; i< input.size(); i++){
            listToProcess.add(input.get(i));
        }
        for (  ; i< limiar; i++){
            listToProcess.add(new Float(0));
        }

        return listToProcess;
    }

    public double processData(MouseActivity mouse){
        ArrayList<Float> preparedXX = grantArraySizeMultipleTwo(this.xx);
        ArrayList<Float> preparedYY = grantArraySizeMultipleTwo(this.yy);
        ArrayList<Float> preparedZZ = grantArraySizeMultipleTwo(this.zz);


        int fftResponseXX= (int)(classifySamples(preparedXX) * 10);
        int fftResponseYY=(int) (classifySamples(preparedYY) * 10);
        int fftResponseZZ=(int) (classifySamples(preparedZZ) * 10);

        int directionXX= (int)(classifyDirection(preparedXX,0));
        int directionYY=(int) (classifyDirection(preparedYY,0));
        int directionZZ=(int) (classifyDirection(preparedZZ,0));

        //Muda aqui!

        Log.i("FFT Response", "xx=" + fftResponseXX +  "     yy=" + fftResponseYY +  "      zz=" + fftResponseZZ);
        mouse.sendMove(new MoveRequest(fftResponseXX, fftResponseYY, fftResponseZZ,directionXX,directionYY,directionZZ ));

        return 0;
    }

    public int classifyDirection(ArrayList<Float> input, int threshold){
        int direction =0;
        for (Float value : input){
            if(value>threshold){
                direction += 1;
            } else if (value<threshold){
                direction += -1;
            }
        }
        return direction;
    }


    /**
     * TODO assig relation between FFT coef sum and a suited value for LAST_ACTIVITY_INDEX
     * [1-5]
     *
     * @param acValues
     * @return
     */
    private double classifySamples(ArrayList<Float> acValues)
    {
        Log.d("Sensor Data", "arraylist size="+acValues.size());

        double[] array = listToArray(acValues);
        Log.d("Sensor Data", "array length="+array.length);


        double[] dataStream_fft = calculateFFT(array);

        double sumFFTcoff=0;

        for(double d:dataStream_fft)
            sumFFTcoff+=Math.abs(d);

        return sumFFTcoff;

    }

    /**
     * Utility function, ArrayList -> double[]
     *
     * @param list
     * @return
     */
    private double[] listToArray(ArrayList<Float> list)
    {
        double[] array = new double[list.size()];
        int i=0;

        for (Float f:list)
            array[i++]=(f != null ? f : 0);

        return array;
    }

    /**
     * This fft function only accepts values that are power of 2 +1
     *  I know that signals coming from the mote are of size 5
     *  for each dimension, so there shouldn't be any problems
     *
     * @param dataStream
     * @return
     */
    public  double[] calculateFFT(double [] dataStream) {
        //TODO: Maybe use the Goertzel's algorithm instead of FFT?



        FastFourierTransformer transf = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] dataStream_fft = transf.transform(dataStream, TransformType.FORWARD);

        double [] out = new double [dataStream.length];


        for (int i = 0; i<dataStream_fft.length;i++)
        {
            dataStream_fft[i] = dataStream_fft[i].divide(dataStream.length);

            out[i] =  dataStream_fft[i].getReal();

        }

        return out;
    }
}

