package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(
        name = "Basic Auto",
        group = "Test",
        preselectTeleOp = "Motor Test TeleOp"
)
public class BasicAuto extends LinearOpMode {

    @Override
    public void runOpMode() {

        telemetry.addLine("Basic Auto initialized.");
        telemetry.addLine("30 second wait");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        while (opModeIsActive() && timer.seconds() < 30.0) {

            telemetry.addData(
                    "Time remaining",
                    "%.1f seconds",
                    30.0 - timer.seconds()
            );
            telemetry.update();

            sleep(20);
        }
    }
}