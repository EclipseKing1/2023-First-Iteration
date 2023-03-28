/*
 set mechanism command to control the arm and elevator mechanisms
*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CompleteMechanism;
import frc.robot.subsystems.CompleteMechanism.MechanismState;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class SetMechanism extends CommandBase {

    private MechanismState state;
    private CompleteMechanism s_Mechanism;
    boolean done; 

    public SetMechanism(MechanismState state) {
        this.state = state;
        s_Mechanism = CompleteMechanism.getInstance();
    }

    @Override
    public void initialize() {
        done = false; 
        // slight arm delay if the new state L3 to prevent arm from hitting the scoring structures
        if(state == MechanismState.L3CUBE || state == MechanismState.L3CONE) {
            CommandScheduler.getInstance().schedule(
                new ParallelCommandGroup(
                        new WaitCommand(0.4).andThen(new SetArm(state.armState)),
                        new SetElevator(state.elevState)));

        } else {
            CommandScheduler.getInstance().schedule(
                    new ParallelCommandGroup(
                            new SetArm(state.armState),
                            new SetElevator(state.elevState)));
        }

        s_Mechanism.setState(state);
    }

    @Override
    public void execute() {
        
    }

    @Override
    public boolean isFinished() {
        // ends when both mechanisms are within a certain threshold from current pos to goal pos
        // the set arm and set elevator commands will still perpetually run though
        return s_Mechanism.inState();
    }

    @Override
    public void end(boolean interrupted) {
        done = true;
    }
}
