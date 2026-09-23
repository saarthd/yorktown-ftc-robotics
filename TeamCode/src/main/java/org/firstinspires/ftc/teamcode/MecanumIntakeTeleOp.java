package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * Mecanum drive + intake
 *
 * Left stick - drive/strafe
 * Right stick X - turn
 * LB - precision mode (0.3)
 * RB - fast mode (0.8)
 * R2 - intake at 100% power
 */
@TeleOp(name = "Mecanum + Intake TeleOp", group = "One")
public class MecanumIntakeTeleOp extends LinearOpMode {

    // Names must match the robot configuration exactly
    private static final String FRONT_LEFT_NAME  = "motor0";
    private static final String BACK_LEFT_NAME   = "motor1";
    private static final String FRONT_RIGHT_NAME = "motor2";
    private static final String BACK_RIGHT_NAME  = "motor3";
    private static final String INTAKE_NAME      = "motor10";

    // Drive speed modes
    private static final double NORMAL_SPEED    = 0.6;
    private static final double PRECISION_SPEED = 0.3;
    private static final double FAST_SPEED      = 0.8;

    private static final double STRAFE_CORRECTION = 1.1;
    private static final double STICK_DEADZONE = 0.05;

    // Intake runs at full power when R2 is pressed
    private static final double INTAKE_SPEED = 1.0;

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor intake;

    @Override
    public void runOpMode() {

        // Drive motors
        // Left side reversed so positive power drives forward
        frontLeft = initMotor(
                FRONT_LEFT_NAME,
                DcMotorSimple.Direction.REVERSE
        );

        backLeft = initMotor(
                BACK_LEFT_NAME,
                DcMotorSimple.Direction.REVERSE
        );

        frontRight = initMotor(
                FRONT_RIGHT_NAME,
                DcMotorSimple.Direction.FORWARD
        );

        backRight = initMotor(
                BACK_RIGHT_NAME,
                DcMotorSimple.Direction.FORWARD
        );

        // Intake motor on Expansion Hub
        // Change FORWARD to REVERSE if the intake spins backwards
        intake = initMotor(
                INTAKE_NAME,
                DcMotorSimple.Direction.FORWARD
        );

        telemetry.addLine("MECANUM + INTAKE READY");
        telemetry.addLine("");
        telemetry.addLine("Left stick: drive / strafe");
        telemetry.addLine("Right stick X: turn");
        telemetry.addLine("LB: precision (0.3)");
        telemetry.addLine("RB: fast (0.8)");
        telemetry.addLine("R2: intake (100%)");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        while (opModeIsActive()) {

            updateDrive();
            updateIntake();

            telemetry.update();

            sleep(20);
        }

        stopAllMotors();
    }

    private void updateDrive() {

        // FTC joystick Y is inverted, so negate it
        double y = -gamepad1.left_stick_y;

        // Slight boost to compensate for weaker mecanum strafing
        double x = gamepad1.left_stick_x * STRAFE_CORRECTION;

        // Right stick controls rotation
        double rotation = gamepad1.right_stick_x;

        /*
         * Deadzone on the entire left stick.
         * This ignores tiny accidental stick movements.
         */
        double magnitude = Math.hypot(x, y);

        if (magnitude > 0) {

            double scale =
                    applyDeadzone(magnitude, STICK_DEADZONE)
                            / magnitude;

            x *= scale;
            y *= scale;
        }

        rotation =
                applyDeadzone(rotation, STICK_DEADZONE);

        /*
         * Drive speed modes.
         *
         * LB = Precision
         * RB = Fast
         * Neither = Normal
         *
         * If both bumpers are held,
         * Precision wins.
         */
        double speed;
        String mode;

        if (gamepad1.left_bumper) {

            speed = PRECISION_SPEED;
            mode = "Precision";

        } else if (gamepad1.right_bumper) {

            speed = FAST_SPEED;
            mode = "Fast";

        } else {

            speed = NORMAL_SPEED;
            mode = "Normal";
        }

        // Standard robot-centric mecanum calculations
        double fl = y + x + rotation;
        double bl = y - x + rotation;
        double fr = y - x - rotation;
        double br = y + x - rotation;

        /*
         * Normalize wheel powers so none exceed 1.0
         * while preserving the ratio between the wheels.
         */
        double max = Math.max(
                1.0,
                Math.max(
                        Math.max(
                                Math.abs(fl),
                                Math.abs(bl)
                        ),
                        Math.max(
                                Math.abs(fr),
                                Math.abs(br)
                        )
                )
        );

        fl = (fl / max) * speed;
        bl = (bl / max) * speed;
        fr = (fr / max) * speed;
        br = (br / max) * speed;

        // Send power commands to drivetrain
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        frontRight.setPower(fr);
        backRight.setPower(br);

        // Driver Hub telemetry
        telemetry.addLine("--- Drive ---");

        telemetry.addData(
                "Mode",
                "%s (%.2f)",
                mode,
                speed
        );

        telemetry.addData(
                "Stick",
                "x %.2f | y %.2f | rot %.2f",
                x,
                y,
                rotation
        );

        telemetry.addData(
                "FL [" + FRONT_LEFT_NAME + "]",
                "pwr %5.2f | enc %d",
                fl,
                frontLeft.getCurrentPosition()
        );

        telemetry.addData(
                "BL [" + BACK_LEFT_NAME + "]",
                "pwr %5.2f | enc %d",
                bl,
                backLeft.getCurrentPosition()
        );

        telemetry.addData(
                "FR [" + FRONT_RIGHT_NAME + "]",
                "pwr %5.2f | enc %d",
                fr,
                frontRight.getCurrentPosition()
        );

        telemetry.addData(
                "BR [" + BACK_RIGHT_NAME + "]",
                "pwr %5.2f | enc %d",
                br,
                backRight.getCurrentPosition()
        );
    }

    private void updateIntake() {

        /*
         * R2 is an analog trigger from 0.0 to 1.0.
         *
         * Here we use it as an ON/OFF control.
         * Any press above 0.05 runs the intake at 100%.
         */
        boolean intakeHeld =
                gamepad1.right_trigger > 0.05;

        double power =
                intakeHeld ? INTAKE_SPEED : 0.0;

        intake.setPower(power);

        telemetry.addLine("");
        telemetry.addLine("--- Intake ---");

        telemetry.addData(
                "R2",
                "%.2f",
                gamepad1.right_trigger
        );

        telemetry.addData(
                "Intake [" + INTAKE_NAME + "]",
                "pwr %5.2f | enc %d",
                power,
                intake.getCurrentPosition()
        );
    }

    /*
     * Gets a motor from the active robot configuration
     * and applies the common setup.
     */
    private DcMotor initMotor(
            String name,
            DcMotorSimple.Direction direction
    ) {

        DcMotor motor =
                hardwareMap.get(
                        DcMotor.class,
                        name
                );

        motor.setDirection(direction);

        motor.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        motor.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER
        );

        motor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        return motor;
    }

    /*
     * Stop every motor when the OpMode ends.
     */
    private void stopAllMotors() {

        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        intake.setPower(0);
    }

    /*
     * Ignores small stick movements and rescales
     * the remaining range smoothly from 0 to 1.
     */
    private static double applyDeadzone(
            double value,
            double deadzone
    ) {

        if (Math.abs(value) < deadzone) {
            return 0.0;
        }

        return Math.signum(value)
                * (Math.abs(value) - deadzone)
                / (1.0 - deadzone);
    }
}
