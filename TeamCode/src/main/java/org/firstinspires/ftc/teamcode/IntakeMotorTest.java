package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(
        name = "Intake Motor Test",
        group = "Test"
)
public class IntakeMotorTest extends LinearOpMode{
    private DcMotor motor10;

    private static final double TEST_POWER = 1.0;

    @Override
    public void runOpMode() {

        /* motor init */
        motor10 = hardwareMap.get(DcMotor.class, "motor10");

        motor10.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /* hub not using encoder to regulate motor speed */
        motor10.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor10.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("INTAKE MOTOR TEST READY");
        telemetry.addLine("");
        telemetry.addLine("SQUARE = motor10 at 100%");
        telemetry.addLine("");

        telemetry.addData(
                "motor10 configured ticks/rev",
                "%.1f",
                motor10.getMotorType().getTicksPerRev()
        );

        telemetry.update();

        /* START BLOCK */
        waitForStart();

        if (isStopRequested()) {
            return;
        }

        /* continuous loop after START */
        while (opModeIsActive()) {

            /* hold SQUARE to run motor10 at 100% */
            double power10 = gamepad1.x ? TEST_POWER : 0.0;

            motor10.setPower(power10);

            telemetry.addLine("=== INTAKE MOTOR TEST ===");

            telemetry.addData(
                    "motor10 [SQUARE]",
                    "power %.1f | encoder %d",
                    power10,
                    motor10.getCurrentPosition()
            );

            telemetry.addLine("");

            telemetry.update();

            sleep(20);
        }

        motor10.setPower(0);
    }
}
