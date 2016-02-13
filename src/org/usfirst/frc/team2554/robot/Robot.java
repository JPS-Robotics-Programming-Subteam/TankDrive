package org.usfirst.frc.team2554.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.Victor;

import org.usfirst.frc.team2554.robot.commands.*;
import org.usfirst.frc.team2554.robot.subsystems.*;
import edu.wpi.first.wpilibj.buttons.*;

public class Robot extends SampleRobot {
    RobotDrive myRobot;  // class that handles basic drive operations
    RobotDrive shooter;
    
    // Two drivers necessary!
    
    /*
     	PORT 0 MUST BE JOYSTICK OR THINGS WON'T WORK
    	PORT 1 MUST BE CONTROLLER OR THINGS WON'T WORK
    	CONTROLLER MUST BE SET TO XINPUT
    	CONTROLLER BINDS AT END OF FILE
     */
    Joystick rightStick, controller; // set CONTROLLER to ID 1 in DriverStation
    JoystickButton launchButton;
    //double right;
    SendableChooser autoChooser;
    Command autonomousCommand;
    Victor armBar,armShooter;
    final double DEADZONE = 0.0;
    
    public void initSmartBoard()
    {
    	autoChooser.addDefault("Under Low Bar", new DefaultAutonomous(myRobot));
    	autoChooser.addObject("Rough Terrain", new rtAutonomous());
        SmartDashboard.putData("Auto Command",autoChooser);
    }
    
    public Robot() {
        myRobot = new RobotDrive(1,0,3,2); //FrontLeft, BackLeft, FrontRight, BackRight
        shooter = new RobotDrive(4,5);
        armBar = new Victor(6);
        armShooter = new Victor(7);
        myRobot.setExpiration(0.1);
        rightStick = new Joystick(0);
        controller = new Joystick(1);
        launchButton = new JoystickButton(controller, 7);
        autoChooser = new SendableChooser();
        initSmartBoard();
    }
    public void autonomous()
    {
    	autonomousCommand = (Command) autoChooser.getSelected();
    	autonomousCommand.start();
    	Timer.delay(0.05);
    }
    /**
     * The motors using arcade steering
     */
    public void operatorControl() {
        myRobot.setSafetyEnabled(true);
        while (isOperatorControl() && isEnabled()) 
        {
        	myRobot.arcadeDrive( -rightStick.getY(), -rightStick.getZ() );			
        	//myRobot.arcadeDrive(rightStick, 1,rightStick,2);
        	//Set speed of each arm based on y-axis of each joystick on controller
        		//1 is L Y Axis
        		//5 is R Y Axis
	        armBar.set(controller.getRawAxis(1)/5.0);
	        
	        
	        // R Y axis DEADZONE set at instance create
	        if( controller.getRawAxis(5) <= DEADZONE && controller.getRawAxis(5) >= -DEADZONE ) {
	        	armShooter.set( -0.1 );
	        } else if( controller.getRawAxis(5) < -DEADZONE ) {
	        	armShooter.set(controller.getRawAxis(5)/3.0);
	        } else {
	        	armShooter.set(controller.getRawAxis(5)/5.0);
	        }
	        
	        //Shooter System NOTE: Not a subsystem. THIS WORKS!
	        //3 is Right Trigger
	        if( controller.getRawAxis(3) > 0.1 )
	        	shooter.arcadeDrive(0,controller.getRawAxis(3)); 					
	        //Collector; 2 is left trigger
	        if( controller.getRawAxis(2) > 0.1 )
	        	shooter.arcadeDrive(0,-controller.getRawAxis(2));
	        if( controller.getRawAxis(2) > 0.1 && controller.getRawAxis(3) > 0.1 )
	        	shooter.arcadeDrive(0,0);
	        
	        
	        launchButton.whenPressed(new Launch());
            Timer.delay(0.005);		// wait for a motor update time
        }
    }

}

/**
*	CONTROLLER BINDS:
*	Left Y axis: armBar moves up/down?
*	Right Y Axis: armShooter moves up/down?
*	Left Trigger: Get ball
*	Right Trigger: Shoot ball
*	Select: 
*/