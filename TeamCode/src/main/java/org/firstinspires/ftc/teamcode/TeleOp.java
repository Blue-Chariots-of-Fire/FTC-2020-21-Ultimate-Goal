package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Linear Opmode")
public class TeleOp extends LinearOpMode
{

    // Software Components //
    private ElapsedTime runtime = new ElapsedTime();

    // DC Motors //
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    // Servos //

    // Sensors //

    // Drive Variables //
    private double frontLeftPower = 0.0;
    private double frontRightPower = 0.0;
    private double backLeftPower = 0.0;
    private double backRightPower = 0.0;

    //Input Variables //
    private double drive = 0.0;
    private double strafe = 0.0;
    private double turn = 0.0;

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
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

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
        }
    }

    private void info ()
    {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.update();
    }

    private void move ()
    {
        // get controller inputs //
        drive = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;

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
}
