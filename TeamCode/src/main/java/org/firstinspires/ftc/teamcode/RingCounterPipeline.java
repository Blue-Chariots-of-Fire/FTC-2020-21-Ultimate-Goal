package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RingCounterPipeline extends OpenCvPipeline {
    // Matrices //
    private Mat zoomedMat = null; //zoomed into the donuts
    private Mat displayMat = new Mat(); //displayed on the screen

    // Variables to test HSV values //
    private int avgHue, avgSaturation, avgValue;

    // thresholds of Hue values for the rings //
    private final int UPPER_THRESHOLD = 23;
    private final int LOWER_THRESHOLD = 18;

    // number of rings detected to give to TeleOp //
    private RingNumber ringNumber = null;

    // enumeration of ring number options //
    public enum RingNumber {FOUR, ONE, ZERO}

    public Mat processFrame(Mat input) //input is 1280x960
    {
        // get the image to the display matrix //
        input.copyTo(displayMat);

        // draw rectangle on the display matrix to show the drivers where to aim //
        Imgproc.rectangle(displayMat, new Point(1620, 727), new Point(1840, 854), new Scalar(255, 0, 0), 2);

        // take the submatrix within the rectangle //
        zoomedMat = input.submat(727, 854, 1620, 1840);

        // convert to HSV colorspace //
        Imgproc.cvtColor(zoomedMat, zoomedMat, Imgproc.COLOR_RGB2HSV);

        // store average HSV values of the zoomed matrix into variables //
        avgHue = (int) Core.mean(zoomedMat).val[0];
        avgSaturation = (int) Core.mean(zoomedMat).val[1];
        avgValue = (int) Core.mean(zoomedMat).val[2];

        // calculate number of rings //
        if (avgHue >= UPPER_THRESHOLD) {
            ringNumber = RingNumber.ZERO;
        } else if (avgHue >= LOWER_THRESHOLD) {
            ringNumber = RingNumber.ONE;
        } else {
            ringNumber = RingNumber.FOUR;
        }

        // return the display matrix to be displayed //
        return displayMat;
    }

    public RingNumber getRingNumber() {
        return ringNumber;
    }

    public int getAvgHue() {
        return avgHue;
    }

    public int getAvgSaturation() {
        return avgSaturation;
    }

    public int getAvgValue() {
        return avgValue;
    }





}

