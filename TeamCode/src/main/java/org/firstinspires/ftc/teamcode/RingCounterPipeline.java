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

    private int avgR = 0;
    private int avgG = 0;
    private int avgB = 0;

    private RingNumber ringNumber = null;

    public enum RingNumber {FOUR, ONE, ZERO}

    public Mat processFrame(Mat input) //input is 1280x960
    {
        input.copyTo(displayMat);

        Imgproc.rectangle(displayMat, new Point(820, 527), new Point(940, 754), new Scalar(255, 0, 0), 2);

        zoomedMat = input.submat(527, 754, 820, 940);

        avgR = (int) Core.mean(zoomedMat).val[0];
        avgG = (int) Core.mean(zoomedMat).val[1];
        avgB = (int) Core.mean(zoomedMat).val[2];



        if (avgR < 85) {
            ringNumber = RingNumber.ZERO;
        } else if (avgR < 105) {
            ringNumber = RingNumber.ONE;
        } else if (avgR < 135) {
            ringNumber = RingNumber.FOUR;
        }

        return zoomedMat;
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

}

