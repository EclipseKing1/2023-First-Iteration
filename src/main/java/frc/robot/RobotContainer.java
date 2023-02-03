package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.commands.*;
import frc.robot.factories.AutoCommandFactory;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Extension.ExtensionStates;
import frc.robot.subsystems.Intake.IntakeStates;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    /* Controllers */
    private final Joystick driver = new Joystick(0);
    private final Joystick operator = new Joystick(1);

    /* Drive Controls */
    private final int translationAxis = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    private final int rotationAxis = XboxController.Axis.kRightX.value;

    /* Driver Buttons */
    private final JoystickButton driverBack = new JoystickButton(driver, XboxController.Button.kBack.value);
    private final JoystickButton driverA = new JoystickButton(driver, XboxController.Button.kA.value);
    private final JoystickButton driverB = new JoystickButton(driver, XboxController.Button.kB.value);
    private final JoystickButton driverX = new JoystickButton(driver, XboxController.Button.kX.value);
    private final JoystickButton driverY = new JoystickButton(driver, XboxController.Button.kY.value);

    /* Operator Buttons, currently just used for testing */
    private final JoystickButton operatorA = new JoystickButton(operator, XboxController.Button.kA.value);
    private final JoystickButton operatorY = new JoystickButton(operator, XboxController.Button.kY.value);
    private final JoystickButton operatorB = new JoystickButton(operator, XboxController.Button.kB.value);
    private final JoystickButton operatorX = new JoystickButton(operator, XboxController.Button.kX.value);
    private final JoystickButton operatorStart = new JoystickButton(operator, XboxController.Button.kStart.value);

    /* Subsystems */
    private final Swerve s_Swerve = Swerve.getInstance() ;
    private final Limelight s_Limelight = Limelight.getInstance() ;
    private final Extension s_Extension = Extension.getInstance();
    private final Pivot s_Pivot = Pivot.getInstance();
    private final Intake s_Intake = Intake.getInstance();

    private final Light s_Lights = Light.getInstance();

    /* Commands */

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        //initialize subsystems
        // s_Swerve = Swerve.getInstance();
        // s_Limelight = Limelight.getInstance();
        // s_Extension = Extension.getInstance();
        // s_Pivot = Pivot.getInstance();
        // s_Intake = Intake.getInstance();

        // s_Swerve.resetOdometry(new Pose2d());
        s_Swerve.resetOdometry(new Pose2d());
        s_Swerve.zeroGyro();
        s_Swerve.setDefaultCommand(
                new TeleopSwerve(
                        s_Swerve,
                        () -> -driver.getRawAxis(translationAxis),
                        () -> -driver.getRawAxis(strafeAxis),
                        () -> -driver.getRawAxis(rotationAxis)));
        // () -> robotCentric.getAsBoolean()));

        // Configure the button bindings
        configureButtonBindings();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be
     * created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing
     * it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        driverBack.onTrue(new InstantCommand(() -> s_Swerve.resetOdometry(new Pose2d())));
        driverA.onTrue(AutoCommandFactory.getSelectedAuto()); // change based on which auto needs to be tested
        driverB.onTrue(new ConditionalCommand(
                new InstantCommand(() -> AutoCommandFactory.cancelLastCommand()),
                new InstantCommand(() -> CommandScheduler.getInstance().schedule(new OnTheFlyGeneration(0, true))),
                s_Swerve.isPathRunningSupplier));
        driverX.onTrue(new SmartResetOdometry());
        Command autoBalanceCommand = new AutoBalance();
        driverY.onTrue(new ConditionalCommand(
                autoBalanceCommand,
                new InstantCommand(() -> autoBalanceCommand.cancel()),
                s_Swerve.isPathRunningSupplier));

        // operator binds for testing
        // operatorA.onTrue(new InstantCommand(() -> s_Intake.testSolenoid(false)));
        // operatorB.onTrue(new InstantCommand(() -> s_Intake.testSolenoid(true)));

        operatorX.onTrue(new InstantCommand(() -> s_Lights.setSelected(1)));
        operatorX.onFalse(new InstantCommand(() -> s_Lights.setSelected(6))); // change to a or b. move a and b to triggers
        operatorY.onTrue(new InstantCommand(() -> s_Lights.setSelected(2)));
        operatorA.onTrue(new InstantCommand(() -> s_Lights.increaseTime()));
        operatorB.onTrue(new InstantCommand(() -> s_Lights.decreaseTime()));
        
        // operatorX.onTrue(new InstantCommand(() -> s_Intake.testVelo(1)));
        // operatorY.onTrue(new InstantCommand(() -> s_Intake.testVelo(0)));
        // operatorStart.onTrue(new InstantCommand(() -> s_Extension.testPosition(false)));
        // operatorA.onTrue(new InstantCommand(() -> s_Pivot.testPosition(true)));
        // operatorA.onTrue(new InstantCommand(() -> s_Pivot.testPosition(false)));
    }

    public void onRobotDisabled() {
        // reset mechanisms so it does not have to be done manually
    }

}
