package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(
        name = "Motor Test TeleOp",
        group = "Test"
)
public class MotorTestTeleOpPS extends LinearOpMode {

    private DcMotor motor0;
    private DcMotor motor1;
    private DcMotor motor2;
    private DcMotor motor3;

    private static final double TEST_POWER = 0.20;

    @Override
    public void runOpMode() {

        /*motor init*/
        motor0 = hardwareMap.get(DcMotor.class, "motor0");
        motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor3 = hardwareMap.get(DcMotor.class, "motor3");

        motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /*hub not using encoder to regulate motor speed*/
        motor0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("MOTOR TEST READY");
        telemetry.addLine("");
        telemetry.addLine("CROSS = motor0");
        telemetry.addLine("CIRCLE = motor1");
        telemetry.addLine("SQUARE = motor2");
        telemetry.addLine("TRIANGLE = motor3");
        telemetry.addLine("");

        telemetry.addData(
                "motor0 configured ticks/rev",
                "%.1f",
                motor0.getMotorType().getTicksPerRev()
        );

        telemetry.addData(
                "motor1 configured ticks/rev",
                "%.1f",
                motor1.getMotorType().getTicksPerRev()
        );

        telemetry.addData(
                "motor2 configured ticks/rev",
                "%.1f",
                motor2.getMotorType().getTicksPerRev()
        );

        telemetry.addData(
                "motor3 configured ticks/rev",
                "%.1f",
                motor3.getMotorType().getTicksPerRev()
        );

        telemetry.update();

        /* START BLOCK */
        waitForStart();

        if (isStopRequested()) {
            return;
        }

        /* continuous loop after START */
        while (opModeIsActive()) {

            /* hold button to run corresponding motor */
            double power0 = gamepad1.a ? TEST_POWER : 0.0;
            double power1 = gamepad1.b ? TEST_POWER : 0.0;
            double power2 = gamepad1.x ? TEST_POWER : 0.0;
            double power3 = gamepad1.y ? TEST_POWER : 0.0;

            motor0.setPower(power0);
            motor1.setPower(power1);
            motor2.setPower(power2);
            motor3.setPower(power3);

            /*
             * Display everything useful on the Driver Hub.
             */
            telemetry.addLine("=== MOTOR TEST ===");

            telemetry.addData(
                    "motor0 [CROSS]",
                    "power %.1f | encoder %d",
                    power0,
                    motor0.getCurrentPosition()
            );

            telemetry.addData(
                    "motor1 [CIRCLE]",
                    "power %.1f | encoder %d",
                    power1,
                    motor1.getCurrentPosition()
            );

            telemetry.addData(
                    "motor2 [SQUARE]",
                    "power %.1f | encoder %d",
                    power2,
                    motor2.getCurrentPosition()
            );

            telemetry.addData(
                    "motor3 [TRIANGLE]",
                    "power %.1f | encoder %d",
                    power3,
                    motor3.getCurrentPosition()
            );

            telemetry.addLine("");

            telemetry.update();

            sleep(20);
        }

        motor0.setPower(0);
        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
    }
}