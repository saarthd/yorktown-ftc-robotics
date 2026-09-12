package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (
        name = "Swerve TeleOp",
        group = "One"
)
public class SwerveTeleOp extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    private static final double DRIVE_SPEED_SCALE = 0.6;
    private static final double PRECISION_OFFSET = -0.3;
    private static final double FAST_OFFSET = 0.2;

    @Override
    public void runOpMode() {
        /* motor init */
        frontLeft = hardwareMap.get(DcMotor.class, "motor0");
        backLeft = hardwareMap.get(DcMotor.class, "motor1");
        frontRight = hardwareMap.get(DcMotor.class, "motor2");
        backRight = hardwareMap.get(DcMotor.class, "motor3");

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /* left side reversed so positive power drives forward on both sides -- matches StraightAuto */
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("MECANUM TELEOP READY");
        telemetry.addLine("");
        telemetry.addLine("LEFT STICK = drive/strafe");
        telemetry.addLine("RIGHT STICK X = rotate");
        telemetry.addLine("LEFT BUMPER = precision mode (slower)");
        telemetry.addLine("RIGHT BUMPER = fast mode (faster)");
        telemetry.addLine("");
        telemetry.update();

        /* START BLOCK */
        waitForStart();

        if (isStopRequested()) {
            return;
        }

        /* continuous loop after START */
        while (opModeIsActive()) {

            double y = -gamepad1.left_stick_y; /* stick is inverted on Y, positive y = forward */
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x;

            /* simple deadzone */
            if (Math.abs(x) < 0.05) x = 0;
            if (Math.abs(y) < 0.05) y = 0;
            if (Math.abs(rotation) < 0.05) rotation = 0;

            /* speed mode -- left bumper slows down for fine positioning, right bumper speeds up.
               if both are held, precision wins (safer default). */
            boolean precisionHeld = gamepad1.left_bumper;
            boolean fastHeld = gamepad1.right_bumper && !precisionHeld;

            double speedScale = DRIVE_SPEED_SCALE;
            String modeLabel = "Normal";
            if (precisionHeld) {
                speedScale = DRIVE_SPEED_SCALE + PRECISION_OFFSET;
                modeLabel = "Precision";
            } else if (fastHeld) {
                speedScale = DRIVE_SPEED_SCALE + FAST_OFFSET;
                modeLabel = "Fast";
            }

            /* standard mecanum kinematics */
            double frontLeftPower = y + x + rotation;
            double backLeftPower = y - x + rotation;
            double frontRightPower = y - x - rotation;
            double backRightPower = y + x - rotation;

            /* normalize so no value exceeds 1.0 in magnitude, preserving ratios between wheels */
            double max = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            ));

            frontLeftPower = (frontLeftPower / max) * speedScale;
            backLeftPower = (backLeftPower / max) * speedScale;
            frontRightPower = (frontRightPower / max) * speedScale;
            backRightPower = (backRightPower / max) * speedScale;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            /*
             * Display everything useful on the Driver Hub.
             */
            telemetry.addLine("=== SWERVE TELEOP ===");
            telemetry.addData("Mode", modeLabel);
            telemetry.addData("Speed Scale", "%.2f", speedScale);
            telemetry.addData("x", "%.2f", x);
            telemetry.addData("y", "%.2f", y);
            telemetry.addData("rotation", "%.2f", rotation);

            telemetry.addData(
                    "frontLeft [motor0]",
                    "power %.2f | encoder %d",
                    frontLeftPower,
                    frontLeft.getCurrentPosition()
            );

            telemetry.addData(
                    "backLeft [motor1]",
                    "power %.2f | encoder %d",
                    backLeftPower,
                    backLeft.getCurrentPosition()
            );

            telemetry.addData(
                    "frontRight [motor2]",
                    "power %.2f | encoder %d",
                    frontRightPower,
                    frontRight.getCurrentPosition()
            );

            telemetry.addData(
                    "backRight [motor3]",
                    "power %.2f | encoder %d",
                    backRightPower,
                    backRight.getCurrentPosition()
            );

            telemetry.addLine("");
            telemetry.update();

            sleep(20);
        }

        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }
}
