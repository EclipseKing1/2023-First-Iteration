package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CompleteMechanism;
import frc.robot.subsystems.CompleteMechanism.MechanismState;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;


public class SetMechanism extends CommandBase{

    private MechanismState state;
    private CompleteMechanism s_Mechanism;
    
    public SetMechanism(MechanismState state){
        this.state = state;
        s_Mechanism = CompleteMechanism.getInstance();
        // s_Mechanism.setState(state);
    }
    
    @Override
    public void initialize() {
        if (s_Mechanism.getState() == MechanismState.L3CONE || s_Mechanism.getState() == MechanismState.L3CUBE ||
        s_Mechanism.getState() == MechanismState.L2CONE || s_Mechanism.getState() == MechanismState.L2CUBE) {
            CommandScheduler.getInstance().schedule(
                new ParallelCommandGroup(
                    new WaitCommand(0.3).andThen(new SetArm(state.armState)),
                    new SetElevator(state.elevState)
                ) 
            );
        } else if (s_Mechanism.getState() == MechanismState.CONEINTAKE || s_Mechanism.getState() == MechanismState.CUBEINTAKE) {
            CommandScheduler.getInstance().schedule(
                new ParallelCommandGroup(
                    new WaitCommand(0.4).andThen(new SetElevator(state.elevState)),
                    new SetArm(state.armState)
                ) 
            );
        } 
        
        else {

            CommandScheduler.getInstance().schedule(
                new ParallelCommandGroup(
                    new WaitCommand(0.3).andThen(new SetArm(state.armState)),
                    new SetElevator(state.elevState)
                ) 
            );
        }

        s_Mechanism.setState(state);
    }

    @Override
    public void execute() {
        
    }

    @Override
    public boolean isFinished() {
        return s_Mechanism.inState();
    }
}
