package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Autonomous", group="Linear Opmode")
public class Autonomous extends LinearOpMode {
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
    private DcMotor uptake = null;
    private DcMotor flywheel = null;

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

    // Wobble Arm Variables //
    private final double wobbleArmPowerPercent = 1.0;

    // Shooter Variables //
    private FlywheelMode flywheelMode = FlywheelMode.OFF;
    private double flywheelPower = 0.0;
    private double flywheelPosition = 0.0;
    private double flywheelOldPosition = 0.0;
    private double flywheelVelocity = 0.0;
    private double flywheelTargetVelocity = 0.0;
    private double flywheelFullVelocity = 2100;
    private double flywheelPowershotVelocity = 1800;
    private double flywheelOffVelocity = 0.0;
    private double time = 0.0;
    private double oldTime = 0.0;
    private double deltaTime = 0.0;
    private Object OpenCvCamera;

    // Enumerations //
    private enum FlywheelMode {FULL, POWERSHOT, OFF}


    @Override
    public void runOpMode() {

        
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
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
        uptake = hardwareMap.get(DcMotor.class, "uptake");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        // Set Auxiliary Motor Directions //
        wobbleArm.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        uptake.setDirection(DcMotor.Direction.FORWARD);
        flywheel.setDirection(DcMotor.Direction.REVERSE);

        // Initialize Servos //
        wobbleGrabber = hardwareMap.get(Servo.class, "wobbleGrabber");
        donutFlicker = hardwareMap.get(Servo.class, "donutFlicker");

        //Wait for start
        waitForStart();
        runtime.reset();
    }
}
