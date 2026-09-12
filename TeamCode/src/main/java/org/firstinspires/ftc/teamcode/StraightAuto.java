package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(
        name="StraightAuto",
        group="One"
)
public class StraightAuto extends LinearOpMode {
    private DcMotor frontLeft; /* motor0 */
    private DcMotor backLeft; /* motor1 */
    private DcMotor frontRight; /* motor2 */
    private DcMotor backRight; /* motor3 */

    private static final double REV_COUNT = 537.7; /* encoder ticks per revolution 19.2:1 */
    private static final double FORWARD_DIST = 1; /* meters forward in auto */
    private static final double WHEEL_DIAMETER = 0.104; /* wheel size for distance */
    private static final double POWER = 0.4; /* motor power set */

    @Override
    public void runOpMode() {
        /*motor init*/
        frontLeft = hardwareMap.get(DcMotor.class, "motor0");
        backLeft = hardwareMap.get(DcMotor.class, "motor1");
        frontRight = hardwareMap.get(DcMotor.class, "motor2");
        backRight = hardwareMap.get(DcMotor.class, "motor3");

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /* allows free control, encoder is manually read */
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        /* motor direction */
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /* calculations */
        double wheelCir = Math.PI * WHEEL_DIAMETER;
        double wheelRev = FORWARD_DIST / wheelCir;
        int targetTicks = (int) Math.round(wheelRev * REV_COUNT);

        telemetry.addLine("Straight Auto Initialized");
        telemetry.addData("Distance", "%.2f meters", FORWARD_DIST);
        telemetry.addData("Target ticks", targetTicks);
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        boolean done = false;
        boolean done0 = false;
        boolean done1 = false;
        boolean done2 = false;
        boolean done3 = false;

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        /* start moving */
        frontRight.setPower(POWER);
        frontLeft.setPower(POWER);
        backRight.setPower(POWER);
        backLeft.setPower(POWER);

        while (opModeIsActive() && !done) {
            if (Math.abs(frontRight.getCurrentPosition()) >= targetTicks) {
                frontRight.setPower(0);
                done0 = true;
            }

            if (Math.abs(frontLeft.getCurrentPosition()) >= targetTicks) {
                frontLeft.setPower(0);
                done1 = true;
            }

            if (Math.abs(backRight.getCurrentPosition()) >= targetTicks) {
                backRight.setPower(0);
                done2 = true;
            }

            if (Math.abs(backLeft.getCurrentPosition()) >= targetTicks) {
                backLeft.setPower(0);
                done3 = true;
            }

            if (done0 && done1 && done2 && done3) {
                done = true;
            }

            telemetry.addData("Front Left: ", frontLeft.getCurrentPosition());
            telemetry.addData("Back Left: ", backLeft.getCurrentPosition());
            telemetry.addData("Front Right: ", frontRight.getCurrentPosition());
            telemetry.addData("Back Right: ", backRight.getCurrentPosition());

            telemetry.update();

            sleep(20);
        }

        double timeTaken = timer.seconds();
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);

        telemetry.addLine("Movement Complete");
        telemetry.addData("Time Taken: ", timeTaken);
        telemetry.update();
    };
}
