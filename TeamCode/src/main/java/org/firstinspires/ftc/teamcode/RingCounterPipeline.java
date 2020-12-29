package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RingCounterPipeline extends OpenCvPipeline {
    private Mat zoomedMat = null;
    private Mat displayMat = new Mat();
    private Mat groundMat = null;

    private int avgR = 0;
    private int avgG = 0;
    private int avgB = 0;
    private int avgROther = 0;
    private int avgGOther = 0;
    private int avgBOther = 0;
    private Scalar avgYLow = null;
    private Scalar avgYHigh = null;

    private RingNumber ringNumber = null;

    public enum RingNumber {FOUR, ONE, ZERO}

    public Mat processFrame(Mat input) //input is 1280x960
    {
        input.copyTo(displayMat);

        Imgproc.rectangle(displayMat, new Point(1620, 727), new Point(1840, 854), new Scalar(255, 0, 0), 2);

        zoomedMat = input.submat(727, 854, 1620, 1840);

        Scalar lowYellow = new Scalar(20, 100, 100);
        Scalar highYellow = new Scalar(30, 255, 255);

        Imgproc.rectangle(displayMat, new Point(1540, 727), new Point(1620, 854), new Scalar(255, 0, 0), 2);

        groundMat = input.submat(727, 854, 1540, 1620);



        avgR = (int) Core.mean(zoomedMat).val[0];
        avgG = (int) Core.mean(zoomedMat).val[1];
        avgB = (int) Core.mean(zoomedMat).val[2];


        avgROther = (int) Core.mean(groundMat).val[0];
        avgGOther = (int) Core.mean(groundMat).val[1];
        avgBOther = (int) Core.mean(groundMat).val[2];




        if (avgR < 85) {
            ringNumber = RingNumber.ZERO;
        } else if (avgR < 105) {
            ringNumber = RingNumber.ONE;
        } else if (avgR < 135) {
            ringNumber = RingNumber.FOUR;
        }

        return displayMat;
    }

    public RingNumber getRingNumber() {
        return ringNumber;
    }

    public int getAvgR() {
        return avgR;
    }

    public int getAvgG() {
        return avgG;
    }

    public int getAvgB() {
        return avgB;
    }

    public int getAvgROther() { return avgROther; }

    public int getAvgGOther() { return avgGOther; }

    public int getAvgBOther() { return avgGOther; }




}

