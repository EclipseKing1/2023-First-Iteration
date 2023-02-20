package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CompleteMechanism;
import frc.robot.subsystems.CompleteMechanism.MechanismState;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;


public class SetMechanism extends CommandBase{

    private MechanismState state;
    private CompleteMechanism mech;
    
    public SetMechanism(MechanismState state){
        this.state = state;
        mech = CompleteMechanism.getInstance();
    }
    
    @Override
    public void initialize() {
        CommandScheduler.getInstance().schedule(
            new ParallelCommandGroup(
                new SetArm(state.armState),
                new SetElevator(state.elevState)
            ) 
        );
    }

    @Override
    public void execute() {
        
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
