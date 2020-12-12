package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

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

    // Enumerations //
    private enum FlywheelMode {FULL, POWERSHOT, OFF}

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

        // Wait for start
        waitForStart();
        runtime.reset();

        // Run loop
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
        telemetry.addData("flywheel velocity", flywheelVelocity);
        telemetry.addData("flywheel mode", flywheelMode);
        telemetry.addData("slow mode", slowMode);
        telemetry.addData("reverse mode", reverseMode);
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

        telemetry.addData("reverse mode", reverseMode);
        telemetry.addData("slow mode", slowMode);

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
        armIn = gamepad2.right_stick_y;
        wobbleArmPower = armIn * wobbleArmPowerPercent;
        wobbleArm.setPower(wobbleArmPower);

        if(gamepad2.a) {wobbleGrabberOpen = true;}
        else if (gamepad2.b) {wobbleGrabberOpen = false;}

        if (wobbleGrabberOpen)
        {
            wobbleGrabber.setPosition(0.5);
        }
        else
        {
            wobbleGrabber.setPosition(1.0);
        }
    }

    private void intake ()
    {
        intakeIn = gamepad1.right_trigger - gamepad1.left_trigger;
        intakePower = intakeIn;
        uptakePower = -intakeIn;

        intake.setPower(intakePower);
        uptake.setPower(uptakePower);
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
            donutFlicker.setPosition(0.5);
        }
        else
        {
            donutFlicker.setPosition(0.79);
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

        oldTime = time;
        time = getRuntime();
        deltaTime = time - oldTime;

        flywheelOldPosition = flywheelPosition;
        flywheelPosition = flywheel.getCurrentPosition();
        flywheelVelocity = (flywheelPosition - flywheelOldPosition)/deltaTime;

        if (flywheelTargetVelocity == flywheelOffVelocity)
        {
            flywheel.setPower(0.0);
        }
        else if (flywheelVelocity < flywheelTargetVelocity)
        {
            flywheel.setPower(1.0);
        }
        else if (flywheelTargetVelocity == flywheelFullVelocity)
        {
            flywheel.setPower(0.87);
        }
        else if (flywheelTargetVelocity == flywheelPowershotVelocity)
        {
            flywheel.setPower(0.8);
        }
    }
}
