package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorStates;

public class SetElevator extends CommandBase {
	Elevator s_Elevator;
	Elevator.ElevatorStates state;
	double elevatorVoltage;
	PIDController elevatorController = new PIDController(0.0, 0.0, 0.0); // tune PID

	public SetElevator(ElevatorStates state) {
		s_Elevator = Elevator.getInstance();
		addRequirements(s_Elevator);
		this.state = state;
	}

	@Override
	public void initialize() {
		s_Elevator.setState(state);
	}

	@Override
	public void execute() {
		elevatorVoltage = elevatorController.calculate(s_Elevator.getCANCoderPosition(), s_Elevator.getCANCoderSetpoint());
		s_Elevator.setVelocity(elevatorVoltage);
		if (Math.abs(s_Elevator.getCANCoderPosition() - s_Elevator.getCANCoderSetpoint()) < 5) {
			elevatorController.reset();
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void end(boolean interrupted) {
		s_Elevator.setVelocity(0);
	}
}
