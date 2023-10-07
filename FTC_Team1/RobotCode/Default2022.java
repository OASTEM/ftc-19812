
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2022-2023 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine which image is being presented to the robot.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous

public class Default2022 extends LinearOpMode {
    private DcMotor left;
    private DcMotor right;
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime bigCounter = new ElapsedTime();
    private DcMotor slide;
    private Servo dumper;
    
    private final double wheelCircumference = 90*3.14;
    private final double gearReduction = 72.0;
    private final double counts = 4.0;
    
    private final double rev = counts*gearReduction;
    private final double revPerMM = rev/wheelCircumference;
    private final double inches = revPerMM*25.4;
    /*
     * Specify the source for the Tensor Flow Model.
     * If the TensorFlowLite object model is included in the Robot Controller App as an "asset",
     * the OpMode must to load it using loadModelFromAsset().  However, if a team generated model
     * has been downloaded to the Robot Controller's SD FLASH memory, it must to be loaded using loadModelFromFile()
     * Here we assume it's an Asset.    Also see method initTfod() below .
     */
    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";
//test #1

    private static final String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AUscD7//////AAABmWjo21/+j0eis3A9VPzgcj+AdwgUeZsSDdcXpSNf1N/5xJLjfwcdKb+jXrd+77iyA848zq6fW568wfYOV82hVXlt/U4k/wujQcurYu48zkmJfxRYbCJHxybBjM2x0oFD2DGOXY/0Vb247FDY+DySXqFGGABlQLaIciGzmr5uGyfvC0edIQ6h+HFtax3be7KPjbbsk8w5ax6dN03ypkrX8twP+GcgEvvwuGNLRcEPbPvJ/mqNKJbX4KMz0iRBVGHMC7SOxpWGronkQFjTYudRJSLRaT7q37vPmJueTYp3FjA2hCMp0NJ8GJefkTHVGyEbeU5wtf5O13YK648SIPhT2pwURPL1UZ7sZEkKfeRJhBo7";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    public void useTimer(double sec){
        runtime.reset();
        while (opModeIsActive()&&runtime.seconds()<sec)
        {
            left.setPower(0.9);
            right.setPower(0.8);
        }
        left.setPower(0.0);
        right.setPower(0.0);
    }
    public void turn(double direction, String turnRight)
    {
        int x = 8;
        int ratio = x/360;
        int tgtPower = (int)direction*(int)ratio*(int)inches;
        waitForStart();
        if(opModeIsActive()){
            left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            if (turnRight.equals("right"))
            {
                right.setTargetPosition(-tgtPower);
                left.setTargetPosition(tgtPower);
                left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            else {
                right.setTargetPosition(tgtPower);
                left.setTargetPosition(-tgtPower);
                left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            left.setPower(0.5);
            right.setPower(0.5);
            while (opModeIsActive()&&(left.isBusy()||right.isBusy())) {
                    telemetry.addData("Left Motor Power", left.getPower());
                    telemetry.addData("Right Motor Power", right.getPower());
                    telemetry.addData("Left Motor Encoder", left.getCurrentPosition());
                    telemetry.addData("Right Motor Encoder", right.getCurrentPosition());
                    telemetry.addData("Status", "Running");
                    telemetry.addData("LeftPosition", left.getCurrentPosition());
                    telemetry.addData("RightPosition", right.getCurrentPosition());
                    telemetry.update();
                }
                left.setPower(0.0);
                right.setPower(0.0);
        }
    }
    public void drive(double power, int target)
        {
            int tgt = (target)*(int)(inches);
            waitForStart();
            if(opModeIsActive()){
                right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                right.setTargetPosition(tgt);
                right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                right.setPower(0.8);
                while (opModeIsActive()&&(right.isBusy())) {
                    left.setPower(0.9);
                    telemetry.addData("Left Motor Power", left.getPower());
                    telemetry.addData("Right Motor Power", right.getPower());
                    telemetry.addData("Left Motor Encoder", left.getCurrentPosition());
                    telemetry.addData("Right Motor Encoder", right.getCurrentPosition());
                    telemetry.addData("Status", "Running");
                    telemetry.addData("LeftPosition", left.getCurrentPosition());
                    telemetry.addData("RightPosition", right.getCurrentPosition());
                    telemetry.update();
                }
                left.setPower(0.0);
                right.setPower(0.0);
            }
        }

    @Override
    public void runOpMode() {
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.0, 16.0/9.0);
        }
        
        left=hardwareMap.get(DcMotor.class, "left");
        right=hardwareMap.get(DcMotor.class, "right");
        right.setDirection(DcMotor.Direction.REVERSE);
        left.setDirection(DcMotor.Direction.REVERSE);
        
        /** Wait for the game to begin */
        
        
        waitForStart();
        boolean atDestination = false;
        boolean run = true;
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.addData("BigCounter", bigCounter.seconds());
        telemetry.addData("Run", run);
        telemetry.update();
        bigCounter.reset();
        while (opModeIsActive()&&(bigCounter.seconds()<15.0))
        {
            while ((opModeIsActive())&&(atDestination==false) && (bigCounter.seconds()<5.0)) {
                telemetry.addData(">", "Running tfod");
                telemetry.addData("BigCounter", bigCounter.seconds());
                telemetry.addData("Run", run);
                telemetry.update();
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData("timer", runtime.seconds());
                            telemetry.addData("LeftPosition", left.getCurrentPosition());
                            telemetry.addData("RightPosition", right.getCurrentPosition());
                            runtime.reset();
                            while (opModeIsActive()&&atDestination==false)
                            {
                                //move straight
                                if (recognition.getLabel()==LABELS[1])
                                {
                                    tfod.deactivate();
                                    runtime.reset();
                                    while(opModeIsActive()&&(runtime.seconds()<1.4))
                                    {
                                        telemetry.addData("Runtime", runtime.seconds());
                                        telemetry.addData("Label", LABELS[1]);
                                        telemetry.update();
                                        left.setPower(0.9);
                                        right.setPower(0.8);
                                    }
                                    left.setPower(0.0);
                                    right.setPower(0.0);
                                    run=false;
                                    atDestination=true;
                                }
                                //move right
                                if (recognition.getLabel()==LABELS[2])
                                {
                                    tfod.deactivate();
                                    runtime.reset();
                                    while(opModeIsActive()&&(runtime.seconds()<2.5))
                                    {
                                        telemetry.addData("Runtime", runtime.seconds());
                                        telemetry.addData("Label", LABELS[2]);
                                        telemetry.update();
                                        left.setPower(0.5);
                                        right.setPower(1.0);
                                    }
                                    left.setPower(0.0);
                                    right.setPower(0.0);
                                    run=false;
                                    atDestination=true;
                                }
                                //move left
                                if (recognition.getLabel()==LABELS[0])
                                {
                                    tfod.deactivate();
                                    runtime.reset();
                                    while(opModeIsActive()&&(runtime.seconds()<2.53))
                                    {                                    
                                        telemetry.addData("Runtime", runtime.seconds());
                                        telemetry.addData("Label", LABELS[0]);
                                        telemetry.update();
                                        left.setPower(1.0);
                                        right.setPower(0.5);
                                    }
                                    left.setPower(0.0);
                                    right.setPower(0.0);
                                    run=false;
                                    atDestination=true;
                                }
                            }
                            //telemetry.update();
                        }
                    }
                }
            }
            telemetry.addData("Run: ", run);
            telemetry.update();
            runtime.reset();
            while(opModeIsActive()&&(runtime.seconds()<2.0))
            {
                left.setPower(0.0);
                right.setPower(0.0);
            }
            left.setPower(0.0);
            right.setPower(0.0);
            atDestination=true;                            
        }
    }
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
}
