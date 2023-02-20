package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

// import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Elevator extends SubsystemBase {
    private static Elevator instance;
    public static Elevator getInstance() {
        if (instance == null) instance = new Elevator();
        return instance;
    }

    private WPI_TalonFX mLeaderElevatorMotor, mFollowerElevatorMotor;
    private double velocity;
    private double voltage;
    private CANCoder elevatorCANCoder = new CANCoder(Constants.HardwarePorts.elevatorCANCoder); // max 1860
    CANCoderConfiguration canCoderConfig = new CANCoderConfiguration();
    ElevatorStates elevatorState = ElevatorStates.ZERO;

    public enum ElevatorStates { //all are measured values unless otherwise indicated
		ZERO(0.0), //bottomed out
		GROUNDCONE(0.0), //intaking cone from ground
        GROUNDCUBE(0.0), //intaking cube from ground
		SUBSTATION(600), //not measured yet
		L1CONE(0.0), 
		L2CONE(1198.0), //middle scoring thing
		L3CONE(1200), //upper scoring thing - not measured yet
        L1CUBE(0),
		L2CUBE(900.0), 
		L3CUBE(1849.0),
		TEST(0.0); 

		double statePosition = 0.0;

		private ElevatorStates(double statePosition) {
			this.statePosition = statePosition;
		}
	}
    
    public Elevator() {
        mLeaderElevatorMotor = new WPI_TalonFX(Constants.HardwarePorts.elevatorLeaderMotor);
        configureMotor(mLeaderElevatorMotor, false);
        mFollowerElevatorMotor = new WPI_TalonFX(Constants.HardwarePorts.elevatorFollowerMotor);
        configureMotor(mFollowerElevatorMotor, false);
        mFollowerElevatorMotor.set(ControlMode.Follower, Constants.HardwarePorts.elevatorLeaderMotor);
        mLeaderElevatorMotor.setSelectedSensorPosition(0);
        mFollowerElevatorMotor.setSelectedSensorPosition(0);
        canCoderConfig.sensorDirection = true;
        canCoderConfig.unitString = "centimeters";
        elevatorCANCoder.configAllSettings(canCoderConfig);
        setCANCoderPosition(0);
    }

    private void configureMotor(WPI_TalonFX talon, boolean inverted){
        talon.setInverted(inverted);
        talon.configVoltageCompSaturation(12.0, Constants.timeOutMs);
        talon.enableVoltageCompensation(false);
        talon.setNeutralMode(NeutralMode.Brake);
        talon.config_kF(0, 0.05, Constants.timeOutMs);
        talon.config_kP(0, 0.12, Constants.timeOutMs);
        talon.config_kI(0, 0, Constants.timeOutMs);
        talon.config_kD(0, 0, Constants.timeOutMs);
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
        mLeaderElevatorMotor.set(ControlMode.Velocity, velocity);
    }

    public void setVoltage(double voltage) {
       this.voltage = voltage;
        mLeaderElevatorMotor.setVoltage(voltage);
    }

    public void setState(ElevatorStates state) {
        elevatorState = state;
    }

    public void setMotorPosition(double position) {
        mLeaderElevatorMotor.setSelectedSensorPosition(position);
        mFollowerElevatorMotor.setSelectedSensorPosition(position);
    }

    public double getVelocitySetpoint() {
        return velocity;
    }
    
    public double getVoltageSetpoint() {
        return voltage;
    }
  
    public double getCANCoderSetpoint() {
        return elevatorState.statePosition;
    }

    public double getMotorPosition() {
		return mLeaderElevatorMotor.getSelectedSensorPosition();
	}

    public void setCANCoderPosition(double position) {
        elevatorCANCoder.setPosition(position);
    }

    public double getCANCoderPosition() {
        return elevatorCANCoder.getPosition();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("eleCANpos", getCANCoderPosition());
        SmartDashboard.putNumber("elePosSet", getCANCoderSetpoint());
		SmartDashboard.putNumber("ele set velo", getVelocitySetpoint());
		SmartDashboard.putNumber("ele set volt", getVoltageSetpoint());
    }
}
