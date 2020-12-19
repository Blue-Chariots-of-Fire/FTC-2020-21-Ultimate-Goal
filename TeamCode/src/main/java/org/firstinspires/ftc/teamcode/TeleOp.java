package org.firstinspires.ftc.teamcode;

import android.graphics.Point;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.camera.WebcamExample;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera2;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Linear Opmode")
public class TeleOp extends LinearOpMode
{

    // Software Components //
    private ElapsedTime runtime = new ElapsedTime();

    // Drive Motors //
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    //Auxiliary Motors //
    private DcMotor wobbleArm = null;
    private DcMotor intake = null;
   // private DcMotor uptake = null;
    private DcMotorEx flywheel = null;

    // Servos //
    private Servo wobbleGrabber = null;
    private Servo donutFlicker = null;

    // Sensors //

    // Drive Power Variables //
    private double frontLeftPower = 0.0;
    private double frontRightPower = 0.0;
    private double backLeftPower = 0.0;
    private double backRightPower = 0.0;

    // Auxiliary Power Variables //
    private double wobbleArmPower = 0.0;
    private boolean wobbleGrabberOpen = false;
    private double intakePower = 0.0;
    private double uptakePower = 0.0;
    private boolean donutFlickerFlicked = false;
    private final double flickerFlicked = 0.5;
    private final double flickerReady = 0.79;

    //Drive Input Variables //
    private double drive = 0.0;
    private double strafe = 0.0;
    private double turn = 0.0;

    // Auxiliary Input Variables //
    private double armIn = 0.0;
    private double intakeIn = 0.0;

    // Driver Assist Variables //
    private boolean slowMode = true;
    private double slowModePowerPercent = 0.5;
    private boolean reverseMode = false;

    // Wobble Variables //
    private final double wobbleArmPowerPercent = 0.43;
    private final double wobbleOpen = 0.5;
    private final double wobbleClosed = 1.0;

    // Shooter Variables //
    private FlywheelMode flywheelMode = FlywheelMode.OFF;
    private double flywheelPosition = 0.0;
    private double flywheelOldPosition = 0.0;
    private double flywheelVelocity = 0.0;
    private double flywheelTargetVelocity = 0.0;
    private double flywheelFullVelocity = 2150.0;
    private double flywheelPowershotVelocity = 1950.0;
    private double flywheelOffVelocity = 0.0;
    private double time = 0.0;
    private double oldTime = 0.0;
    private double deltaTime = 0.0;
    private final double flywheelMaxPower = 1.0;

    // Enumerations //
    private enum FlywheelMode {FULL, POWERSHOT, OFF}

    // OpenCV Stuff //
    OpenCvInternalCamera2 camera = null;
    RingCounterPipeline ringCounterPipeline = null;
    private final int webcamWidth = 1920;
    private final int webcamHeight = 1080;

    @Override
    public void runOpMode()
    {
        // Initialize Telemetry //
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize Drive Motors //
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        // Set Drive Motor Directions //
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Initialize Auxiliary Motors //
        wobbleArm = hardwareMap.get(DcMotor.class, "wobbleArm");
        intake = hardwareMap.get(DcMotor.class, "intake");
        //uptake = hardwareMap.get(DcMotor.class, "uptake");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        // Set Auxiliary Motor Directions //
        wobbleArm.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        //uptake.setDirection(DcMotor.Direction.FORWARD);
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);

        // Initialize Servos //
        wobbleGrabber = hardwareMap.get(Servo.class, "wobbleGrabber");
        donutFlicker = hardwareMap.get(Servo.class, "donutFlicker");

        // Open Camera Monitor //
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // Open Camera //
        //camera = OpenCvCameraFactory.getInstance().createInternalCamera2(OpenCvInternalCamera2.CameraDirection.BACK, cameraMonitorViewId);
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam");
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);


        // Start Stream and Enable GPU Acceleration for the Viewport //
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
                camera.startStreaming(webcamWidth, webcamHeight, OpenCvCameraRotation.UPRIGHT);
            }
        });

        // Create Pipeline //
        ringCounterPipeline = new RingCounterPipeline();

        // Set Pipeline //
        camera.setPipeline(ringCounterPipeline);

        // Wait for start
        waitForStart();
        runtime.reset();

        //camera.stopStreaming();

        // Run loop //
        while (opModeIsActive())
        {
            // send information to driver station //
            info();

            // run the movement methods //
            move();

            // run the auxiliary systems methods //
            wobble();
            intake();
            shooter();
        }
    }

    private void info ()
    {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("flywheel velocity", flywheel.getVelocity());
        telemetry.addData("flywheel mode", flywheelMode);
        telemetry.addData("slow mode", slowMode);
        telemetry.addData("reverse mode", reverseMode);
        telemetry.addData("number of rings", ringCounterPipeline.getRingNumber());
        telemetry.addData("avgR:", ringCounterPipeline.getAvgR());
        telemetry.addData("avgG:", ringCounterPipeline.getAvgG());
        telemetry.addData("avgB:", ringCounterPipeline.getAvgB());
        telemetry.update();
    }

    private void move ()
    {
        // get controller inputs //
        drive = gamepad1.left_stick_y;
        strafe = -gamepad1.left_stick_x;
        turn = -gamepad1.right_stick_x;

        // get slow mode input //
        if (gamepad1.dpad_left && gamepad1.left_bumper) {slowMode = false;}
        else if (gamepad1.dpad_right) {slowMode = true;}

        //get reverse mode input //
        if (gamepad1.dpad_up) {reverseMode = false;}
        else if (gamepad1.dpad_down) {reverseMode = true;}

        // calculate slow mode powers //
        if (slowMode)
        {
            drive *= slowModePowerPercent;
            strafe *= slowModePowerPercent;
            turn *= slowModePowerPercent;
        }

        // calculate reverse mode powers //
        if(reverseMode)
        {
            drive *= -1;
            strafe *= -1;
        }

        // compute motor powers //
        frontLeftPower = drive + strafe + turn;
        frontRightPower = drive - strafe - turn;
        backLeftPower = drive - strafe + turn;
        backRightPower = drive + strafe - turn;

        // set motor powers //
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    private void wobble ()
    {
        // get inputs from controller //
        armIn = gamepad2.right_stick_y;
        if(gamepad2.a) {wobbleGrabberOpen = true;}
        else if (gamepad2.b) {wobbleGrabberOpen = false;}

        // calculate wobble arm motor power //
        wobbleArmPower = armIn * wobbleArmPowerPercent;
        wobbleArm.setPower(wobbleArmPower);

        // set wobble grabber servo positions //
        if (wobbleGrabberOpen)
        {
            wobbleGrabber.setPosition(wobbleOpen);
        }
        else
        {
            wobbleGrabber.setPosition(wobbleClosed);
        }
    }

    private void intake ()
    {
        intakeIn = gamepad1.right_trigger - gamepad1.left_trigger;
        intakePower = intakeIn;
        uptakePower = -intakeIn;

        intake.setPower(intakePower);
        //uptake.setPower(uptakePower);
    }

    private void shooter ()
    {
        // get flicker input //
        donutFlickerFlicked = gamepad1.a;

        // get flywheel mode input //
        if (gamepad1.x) {flywheelMode = FlywheelMode.FULL;}
        else if (gamepad1.b) {flywheelMode = FlywheelMode.POWERSHOT;}
        else if (gamepad1.y) {flywheelMode = FlywheelMode.OFF;}

        if (donutFlickerFlicked)
        {
            donutFlicker.setPosition(flickerFlicked);
        }
        else
        {
            donutFlicker.setPosition(flickerReady);
        }

        switch (flywheelMode)
        {
            case OFF:
                flywheelTargetVelocity = flywheelOffVelocity; break;
            case FULL:
                flywheelTargetVelocity = flywheelFullVelocity; break;
            case POWERSHOT:
                flywheelTargetVelocity = flywheelPowershotVelocity; break;
        }

        /*
        oldTime = time;
        time = getRuntime();
        deltaTime = time - oldTime;

        flywheelOldPosition = flywheelPosition;
        flywheelPosition = flywheel.getCurrentPosition();
        flywheelVelocity = (flywheelPosition - flywheelOldPosition)/deltaTime;
         */

        if (gamepad1.right_bumper)
        {
            flywheel.setPower(flywheelMaxPower);
        }
        else
        {
            flywheel.setVelocity(flywheelTargetVelocity);
        }
    }
}
