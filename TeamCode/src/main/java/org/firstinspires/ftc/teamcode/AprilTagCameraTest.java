package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

@TeleOp(name = "AprilTag Camera Test", group = "Test")
public class AprilTagCameraTest extends LinearOpMode {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        /*
         * Create the AprilTag processor.
         *
         * SDK 12.0 uses the current-season AprilTag library
         * automatically.
         *
         * We use meters and degrees because those are the units
         * we eventually want for automatic alignment.
         */
        aprilTag = new AprilTagProcessor.Builder()
                .setOutputUnits(
                        DistanceUnit.METER,
                        AngleUnit.DEGREES
                )
                .build();

        /*
         * Lower decimation = potentially longer detection range
         * but more processing.
         *
         * 2 is a reasonable starting point.
         */
        aprilTag.setDecimation(2);

        /*
         * Connect the Microsoft LifeCam HD-3000.
         *
         * "Webcam 1" must exactly match the name in the
         * Driver Station robot configuration.
         */
        visionPortal = new VisionPortal.Builder()
                .setCamera(
                        hardwareMap.get(
                                WebcamName.class,
                                "Webcam 1"
                        )
                )
                .setCameraResolution(
                        new Size(640, 480)
                )
                .addProcessor(aprilTag)
                .build();

        telemetry.addLine("APRILTAG CAMERA READY");
        telemetry.addLine("");
        telemetry.addLine("Camera: Webcam 1");
        telemetry.addLine("Resolution: 640 x 480");
        telemetry.addLine("");
        telemetry.addLine("Use 3 dots -> Camera Stream");
        telemetry.addLine("to verify the camera image.");
        telemetry.addLine("");
        telemetry.addLine("Press START to detect AprilTags.");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            visionPortal.close();
            return;
        }

        while (opModeIsActive()) {

            List<AprilTagDetection> detections =
                    aprilTag.getDetections();

            telemetry.addData(
                    "Total detections",
                    detections.size()
            );

            /*
             * Keep track of the closest detected HIVE cluster.
             *
             * Later, this is what we can use for the
             * Square automatic alignment function.
             */
            AprilTagClusterDetection closestCluster = null;
            double closestClusterRange = Double.MAX_VALUE;

            for (AprilTagDetection detection : detections) {

                telemetry.addLine("");
                telemetry.addLine("-------------------------");

                /*
                 * Individual AprilTag
                 */
                if (detection instanceof AprilTagSingleDetection) {

                    AprilTagSingleDetection single =
                            (AprilTagSingleDetection) detection;

                    if (single.metadata != null) {

                        telemetry.addLine("SINGLE APRILTAG");

                        telemetry.addData(
                                "ID",
                                single.id
                        );

                        telemetry.addData(
                                "Name",
                                single.metadata.name
                        );

                        if (single.ftcPose != null) {

                            telemetry.addData(
                                    "X (right)",
                                    "%.3f m",
                                    single.ftcPose.x
                            );

                            telemetry.addData(
                                    "Y (forward)",
                                    "%.3f m",
                                    single.ftcPose.y
                            );

                            telemetry.addData(
                                    "Range",
                                    "%.3f m",
                                    single.ftcPose.range
                            );

                            telemetry.addData(
                                    "Bearing",
                                    "%.2f deg",
                                    single.ftcPose.bearing
                            );

                            telemetry.addData(
                                    "Yaw",
                                    "%.2f deg",
                                    single.ftcPose.yaw
                            );
                        }

                    } else {

                        telemetry.addLine("UNKNOWN APRILTAG");

                        telemetry.addData(
                                "ID",
                                single.id
                        );
                    }
                }

                /*
                 * BIOBUZZ AprilTag cluster
                 */
                else if (detection instanceof AprilTagClusterDetection) {

                    AprilTagClusterDetection cluster =
                            (AprilTagClusterDetection) detection;

                    telemetry.addLine("APRILTAG CLUSTER");

                    if (cluster.metadata != null) {

                        telemetry.addData(
                                "Name",
                                cluster.metadata.name
                        );
                    }

                    telemetry.addData(
                            "Percent tags found",
                            "%d%%",
                            cluster.percentClusterFound
                    );

                    if (cluster.ftcPose != null) {

                        telemetry.addData(
                                "X (right)",
                                "%.3f m",
                                cluster.ftcPose.x
                        );

                        telemetry.addData(
                                "Y (forward)",
                                "%.3f m",
                                cluster.ftcPose.y
                        );

                        telemetry.addData(
                                "Range",
                                "%.3f m",
                                cluster.ftcPose.range
                        );

                        telemetry.addData(
                                "Bearing",
                                "%.2f deg",
                                cluster.ftcPose.bearing
                        );

                        telemetry.addData(
                                "Yaw",
                                "%.2f deg",
                                cluster.ftcPose.yaw
                        );

                        /*
                         * Find closest visible cluster.
                         */
                        if (cluster.ftcPose.range
                                < closestClusterRange) {

                            closestClusterRange =
                                    cluster.ftcPose.range;

                            closestCluster =
                                    cluster;
                        }
                    }
                }
            }

            /*
             * Show the cluster that our eventual alignment
             * system would choose.
             */
            telemetry.addLine("");
            telemetry.addLine("=========================");
            telemetry.addLine("CLOSEST HIVE TARGET");
            telemetry.addLine("=========================");

            if (closestCluster != null) {

                if (closestCluster.metadata != null) {

                    telemetry.addData(
                            "Target",
                            closestCluster.metadata.name
                    );
                }

                telemetry.addData(
                        "X",
                        "%.3f m",
                        closestCluster.ftcPose.x
                );

                telemetry.addData(
                        "Y",
                        "%.3f m",
                        closestCluster.ftcPose.y
                );

                telemetry.addData(
                        "Range",
                        "%.3f m",
                        closestCluster.ftcPose.range
                );

                telemetry.addData(
                        "Yaw",
                        "%.2f deg",
                        closestCluster.ftcPose.yaw
                );

            } else {

                telemetry.addLine(
                        "No HIVE cluster detected."
                );
            }

            telemetry.update();

            sleep(50);
        }

        /*
         * Release camera resources when STOP is pressed.
         */
        visionPortal.close();
    }
}