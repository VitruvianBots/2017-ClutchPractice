package org.usfirst.frc.team4201.robot.subsystems;

import org.usfirst.frc.team4201.robot.Robot;
import org.usfirst.frc.team4201.robot.RobotMap;
import org.usfirst.frc.team4201.robot.commands.SetSplitArcade;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.team254.frc2016.CheesyDriveHelper;
import com.team254.lib.util.DriveSignal;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveTrain extends Subsystem {
	CANTalon[] leftMotors = {
		new CANTalon(RobotMap.driveTrainMotorLeftFront),
		new CANTalon(RobotMap.driveTrainMotorLeftRear),
	};
	CANTalon[] rightMotors = {
		new CANTalon(RobotMap.driveTrainMotorRightFront),
		new CANTalon(RobotMap.driveTrainMotorRightRear),
	};
	RobotDrive robotDrive = new RobotDrive(leftMotors[0], leftMotors[1], rightMotors[0], rightMotors[1]);

	CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();
	public DriveSignal signal = new DriveSignal(0, 0);
	
	DoubleSolenoid driveTrainShifters = new DoubleSolenoid(RobotMap.PCMOne, RobotMap.driveTrainShifterForward, RobotMap.driveTrainShifterReverse);
	
	double upperShiftThreshold = 12;
	double lowerShiftThreshold = 10;
	
	public DriveTrain() {
		super();

		leftMotors[0].changeControlMode(TalonControlMode.PercentVbus);
		leftMotors[1].changeControlMode(TalonControlMode.Follower);
		leftMotors[1].set(leftMotors[0].getDeviceID());
        rightMotors[0].changeControlMode(TalonControlMode.PercentVbus);
        rightMotors[1].changeControlMode(TalonControlMode.Follower);
        rightMotors[1].set(rightMotors[0].getDeviceID());
	}
	
	void initDriverControl() {
		// Clear sticky faults to avoid errors
		leftMotors[0].clearStickyFaults();
		leftMotors[1].clearStickyFaults();
        rightMotors[0].clearStickyFaults();
        rightMotors[1].clearStickyFaults();
        
        // Set Control Mode to ensure proper control mode
		leftMotors[0].changeControlMode(TalonControlMode.PercentVbus);
		leftMotors[1].changeControlMode(TalonControlMode.PercentVbus);
        rightMotors[0].changeControlMode(TalonControlMode.PercentVbus);
        rightMotors[1].changeControlMode(TalonControlMode.PercentVbus);
		
        // Ensure motors are not inverted
        robotDrive.setInvertedMotor(MotorType.kFrontLeft, true);
        robotDrive.setInvertedMotor(MotorType.kFrontRight, true);
        robotDrive.setInvertedMotor(MotorType.kRearLeft, true);
        robotDrive.setInvertedMotor(MotorType.kRearRight, true);
	}
	
	public void tankDrive(Joystick leftJoystick, Joystick rightJoystick) {
		initDriverControl();
        // Set Tank Drive with cubed inputs. I don't know why the left/right joysticks are reversed
    	robotDrive.tankDrive(Math.pow(leftJoystick.getAxis(AxisType.kY), 3), Math.pow(rightJoystick.getAxis(AxisType.kY), 3));
	}
	
	public void splitArcadeDrive(Joystick leftJoystick, Joystick rightJoystick) {
		initDriverControl();
		
        // Set split Arcade Drive with cubed inputs
    	robotDrive.arcadeDrive(Math.pow(leftJoystick.getAxis(AxisType.kY), 3), Math.pow(rightJoystick.getAxis(AxisType.kX), 3)); 
	}
	
	public void splitArcadeDriveModified(Joystick leftJoystick, Joystick rightJoystick, double leftJoystickValue, double rightJoystickValue, double changeOfInput) {
		initDriverControl();
		leftJoystickValue = leftJoystick.getAxis(AxisType.kY);
		leftJoystickValue = leftJoystickValue*9/10; //change later
        rightJoystickValue = rightJoystick.getAxis(AxisType.kY);
        changeOfInput = -Math.pow(leftJoystickValue, 2);
        rightJoystickValue = rightJoystickValue*changeOfInput;
        
        // Set split Arcade Drive with squared inputs
    	robotDrive.arcadeDrive(leftJoystick.getAxis(AxisType.kY), rightJoystickValue, true); 
	}
	
	public void cheesyDrive(Joystick leftJoystick, Joystick rightJoystick) {
		// Clear sticky faults to avoid errors
		leftMotors[0].clearStickyFaults();
		leftMotors[1].clearStickyFaults();
        rightMotors[0].clearStickyFaults();
        rightMotors[1].clearStickyFaults();
        
        // Set Control Mode to ensure proper control mode
		leftMotors[0].changeControlMode(TalonControlMode.PercentVbus);
		leftMotors[1].changeControlMode(TalonControlMode.PercentVbus);
        rightMotors[0].changeControlMode(TalonControlMode.PercentVbus);
        rightMotors[1].changeControlMode(TalonControlMode.PercentVbus);
		
        // Ensure motors are not inverted
        robotDrive.setInvertedMotor(MotorType.kFrontLeft, false);
        robotDrive.setInvertedMotor(MotorType.kRearLeft, false);
        robotDrive.setInvertedMotor(MotorType.kFrontRight, true);
        robotDrive.setInvertedMotor(MotorType.kRearRight, true);
        
        signal = cheesyDrive.cheesyDrive(-leftJoystick.getY(), rightJoystick.getX(), RobotMap.cheesyDriveBrakeMode);
        
		leftMotors[0].set(signal.leftMotor);
		rightMotors[0].set(signal.rightMotor);
	}
	
	public void setHighGear() {
		driveTrainShifters.set(Value.kForward);
	}
	
	public void setLowGear() {
		driveTrainShifters.set(Value.kReverse);
	}
	
	public Value getShiftStatus() {
		return driveTrainShifters.get();
	}
	
	/** This is a model, need to get values before implementing.
	 * 
	 *	<p> Automatically shift between low/high gear using pre-defined thresholds. 
	 *	Thresholds are separate to avoid gearbox damage if you hover between a single threshold.
	 */
	public void autoGearShifting() {
		double encoderValue = 11;
		
		if(!RobotMap.manualShiftOverride)
			if(encoderValue > upperShiftThreshold)
				setHighGear();
			else if(encoderValue < lowerShiftThreshold)
				setLowGear();
	}
	
	public void updateSmartDashboard() {
		SmartDashboard.putBoolean("High Gear", driveTrainShifters.get() == Value.kForward ? true : false);
		SmartDashboard.putBoolean("Auto Shifting", !RobotMap.manualShiftOverride);
		SmartDashboard.putNumber("DT Left Value", leftMotors[0].get());
		SmartDashboard.putNumber("DT Right Value", rightMotors[0].get());
		SmartDashboard.putNumber("Y1", Robot.oi.leftJoystick.getAxis(AxisType.kY));
		SmartDashboard.putNumber("X2", Robot.oi.rightJoystick.getAxis(AxisType.kX));
		SmartDashboard.putNumber("Cheesy Left Motor", signal.leftMotor);
		SmartDashboard.putNumber("Cheesy Right Motor", signal.rightMotor);
		SmartDashboard.putBoolean("Cheesy Breake Mode", signal.breakMode);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		setDefaultCommand(new SetSplitArcade());
	}
}
