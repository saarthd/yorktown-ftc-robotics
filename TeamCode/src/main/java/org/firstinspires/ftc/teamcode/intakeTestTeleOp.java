package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(
        name = "Core Hex Motor Test",
        group = "Test"
)
public class intakeTestTeleOp extends LinearOpMode {

    private DcMotor testMotor;

    private static final double MAX_SPEED = 1.0; /* speed when R2 is fully pressed */

    @Override
    public void runOpMode() {

        testMotor = hardwareMap.get(DcMotor.class, "motor0");

        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        testMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("CORE HEX MOTOR TEST READY");
        telemetry.addLine("R2 = run motor (0 to 0.5)");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        while (opModeIsActive()) {

            double trigger = gamepad1.right_trigger; /* PS5 R2, ranges 0.0 to 1.0 */

            double power = trigger * MAX_SPEED;

            testMotor.setPower(power);

            telemetry.addData("R2 trigger", "%.2f", trigger);
            telemetry.addData("Motor power", "%.2f", power);
            telemetry.addData("Encoder", testMotor.getCurrentPosition());
            telemetry.update();

            sleep(20);
        }

        testMotor.setPower(0);
    }
}