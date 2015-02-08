/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
//All code written by Lead Programmer: Garret Sampel
//1/4/14 (kickoff)

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;

public class Main extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    Compressor compressor = new Compressor(1,1); //pressureSwitchChannel, compressorRelay is the second
    Solenoid pistonExtend = new Solenoid(1); //first slot on solenoid module
    Solenoid pistonRetract = new Solenoid(2); //second solenoid slot
    Solenoid pistonExtend2 = new Solenoid(3); //third solenoid slot
    Solenoid pistonRetract2 = new Solenoid(4); //fourth solenoid slot
    //Relay spikeRelay = new Relay(2);

    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    Jaguar blockTalon = new Jaguar(3);
    Jaguar lifterTalon = new Jaguar(4);
    Jaguar leftDrive = new Jaguar(1);
    Jaguar rightDrive = new Jaguar(2);
    RobotDrive drive = new RobotDrive(leftDrive, rightDrive);
    Print p = new Print();
    //boolean down = true;
    AxisCamera camera;
    String o = "[PERRY]: ";
    double throttleVal = 0.00;

    public void robotInit() {
        pistonRetract.set(false);
        pistonRetract2.set(false);
        pistonExtend.set(false);
        pistonExtend2.set(false);
        p.p(1, o + "Hello user :-)");
        compressor.start();
        p.p(2, o + "Compressor enabled");
        camera = AxisCamera.getInstance();
        //cameras are not normal objects, you can only have one, so you
        //can't just do AxisCamera camera = new AxisCamera
        //(only one instance)

        camera.writeResolution(AxisCamera.ResolutionT.k320x240);
        camera.writeBrightness(0);
        p.p(3, o + "Check the camera feed");
        getWatchdog().setEnabled(false);
        p.p(4, o + "Watchdog enabled");
        //according to the world, we need safety...
        //I disagree, I shall devide by zero and spill coffee in
        //data centers all I want. (JOKING)
    }
    boolean doDrive = true;
    public void attemptDrive() {
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new java.util.TimerTask() {
            public void run() {
                doDrive = false;
            }
        }, 1200);

        while(doDrive) {
            drive.arcadeDrive(-1.0, 0);
        }
        doDrive = true;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        //NO AUTONOMUS YET!!!!!
         p.p(1, "Autonomus initiated");
         attemptDrive();
         
         lifterTalon.set(-0.25);
         Timer.delay(1);
         lifterTalon.set(0.0);
         Timer.delay(2);
         pistonRetract.set(false);
         pistonRetract2.set(false);
         pistonExtend.set(true);
         pistonExtend2.set(true);
         Timer.delay(2);
         pistonRetract.set(true);
         pistonRetract2.set(true);
         pistonExtend.set(false);
         pistonExtend2.set(false);
         Timer.delay(3);
         pistonRetract.set(false);
         pistonRetract2.set(false);
         pistonExtend.set(false);
         pistonExtend2.set(false);
         Timer.delay(5);
         lifterTalon.set(0.25);
         Timer.delay(0.6);
         lifterTalon.set(0.0);
        while (true && isAutonomous() && isEnabled()) {
            update();
            p.p(1, "Autonomus already ran once,");
            p.p(2, "waiting for teleop.");
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
       // p.clear();
        p.p(1, "Teleop initiated");
        while (true && isOperatorControl() && isEnabled()) {
            update(); //update camara
            pnumatic();//pnumatic statements
            blocker();
            pickup();
            drive.arcadeDrive(leftStick);
            Timer.delay(0.005);
            //the timer is so the code doesnt over calculate.
            //think of it like look sensitivity in a video game.
        }
    }

    public void pnumatic(){
        if(leftStick.getRawButton(1) == true){
            pistonRetract.set(false);
            pistonRetract2.set(false);
            pistonExtend.set(true);
            pistonExtend2.set(true);
            p.p(4, "Piston: extending");
        }else if(leftStick.getRawButton(4) == true){
            pistonExtend.set(false);
            pistonExtend2.set(false);
            pistonRetract.set(true);
            pistonRetract2.set(true);
            p.p(4, "Piston: retracting");
        }else{
            pistonRetract.set(false);
            pistonRetract2.set(false);
            pistonExtend.set(false);
            pistonExtend2.set(false);
            p.p(4, "Piston: stopped");
        }
    }

    public void blocker(){
        if(leftStick.getRawButton(6) == true){//if the up button is pressed
            blockTalon.set(1.0);//let the motor move
            p.p(3, "Blocker: on");//print to the display that its moving
        }
        else if(leftStick.getRawButton(7) == true){//if they down button is pressed
            blockTalon.set(-1.0);//let the motor move backwards
            p.p(3, "Blocker: reverse");//print to the display that its moving backwards
        }
        else{//if its anything else (aka button not pressed)
            blockTalon.set(0.0); //stop the motor
            p.p(3, "Blocker: off");//and tell the user that the motor is off
        }
    }
    
    public void pickup(){
        if(leftStick.getRawButton(3) == true){
            throttleVal = 0.5;
        }else if(leftStick.getRawButton(2) == true){
            throttleVal = -0.5;
        }else if(leftStick.getRawButton(5)== true){
            throttleVal = 0.0;
        }
        p.p(2, "Lifter: " + throttleVal);
        lifterTalon.set(throttleVal);
    }

    public void update() {
        DriverStationLCD.getInstance().updateLCD();
        //this function just keeps the code clean
        //(which is another word for being lazy and not wanting to type what it really is)
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
