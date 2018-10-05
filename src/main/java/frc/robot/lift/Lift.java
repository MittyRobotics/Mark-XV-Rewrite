package frc.robot.lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Defaults;

public final class Lift {
	public static final double SWITCH_HEIGHT = 48;
	public static final double SCALE_HEIGHT = 84; // TODO

	private static final int TALON_IDS[] = {9, 5};
	private static final int TICKS_PER_INCH = 651;

	private static WPI_TalonSRX[] talons;

	private static volatile boolean shouldRun = false;
	private static volatile double setpoint = 0;

	public static void init() {
		talons = new WPI_TalonSRX[TALON_IDS.length];
		for (int i = 0; i < TALON_IDS.length; i++) {
			talons[i] = new WPI_TalonSRX(TALON_IDS[i]);
			if (i > 0) {
				talons[i].set(ControlMode.Follower, TALON_IDS[0]);
			}
		}

		talons[0].configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		talons[0].setSensorPhase(true);
		talons[0].configContinuousCurrentLimit(25, 0);
		talons[0].enableCurrentLimit(true);
		talons[0].setSelectedSensorPosition(0, 0, 0); // TODO("This Should be Hardware Set Not Programmed") Clean

		configPID();
		configAmpLimit();

		SmartDashboard.putNumber("Lift P", Defaults.LIFT_P);
		SmartDashboard.putNumber("Lift I", Defaults.LIFT_I);
		SmartDashboard.putNumber("Lift D", Defaults.LIFT_D);
		SmartDashboard.putNumber("Lift Amp Limit", Defaults.LIFT_AMP_LIMIT);
	}

	public static void setSetpoint(final double setpoint) {
		Lift.setpoint = Math.max(-2, Math.min(84, setpoint));
		if (shouldRun) {
			talons[0].set(ControlMode.Position, setpoint * TICKS_PER_INCH);
		} else {
			talons[0].set(ControlMode.PercentOutput, 0);
		}
	}

	public static double getSetpoint() {
		return setpoint;
	}

	public static void start() {
		shouldRun = true;
	}

	public static void configPID() {
		talons[0].config_kP(0, SmartDashboard.getNumber("Lift P", Defaults.LIFT_P), 0);
		talons[0].config_kI(0, SmartDashboard.getNumber("Lift I", Defaults.LIFT_I), 0);
		talons[0].config_kD(0, SmartDashboard.getNumber("Lift D", Defaults.LIFT_D), 0);
	}

	public static void configAmpLimit() {
		talons[0].configContinuousCurrentLimit((int) SmartDashboard.getNumber("Lift Amp Limit", Defaults.LIFT_AMP_LIMIT), 0);
		talons[0].enableCurrentLimit(true);
	}

	public static void stop() {
		shouldRun = false;
	}
}
