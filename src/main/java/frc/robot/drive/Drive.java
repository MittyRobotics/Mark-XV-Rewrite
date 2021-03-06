package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Defaults;

public final class Drive {
	private static final int[] LEFT_DRIVE_TALONS = {2, 3};
	private static final int[] RIGHT_DRIVE_TALONS = {0, 1};

	private static WPI_TalonSRX[] leftTalons;
	private static WPI_TalonSRX[] rightTalons;
	private static DoubleSolenoid shifter;

	public static void init() {
		leftTalons = new WPI_TalonSRX[LEFT_DRIVE_TALONS.length];
		for (int i = 0; i < leftTalons.length; i++) {
			leftTalons[i] = new WPI_TalonSRX(LEFT_DRIVE_TALONS[i]);
			leftTalons[i].setInverted(false);
			if (i > 0) {
				leftTalons[i].set(ControlMode.Follower, LEFT_DRIVE_TALONS[0]);
			}
		}
		rightTalons = new WPI_TalonSRX[RIGHT_DRIVE_TALONS.length];
		for (int i = 0; i < rightTalons.length; i++) {
			rightTalons[i] = new WPI_TalonSRX(RIGHT_DRIVE_TALONS[i]);
			rightTalons[i].setInverted(true);
			if (i > 0) {
				rightTalons[i].set(ControlMode.Follower, RIGHT_DRIVE_TALONS[0]);
			}
		}
		ramp(0.1);

		shifter = new DoubleSolenoid(6, 7);

		updatePID(leftTalons);
		updatePID(rightTalons);
		DriveAuton.init();

		SmartDashboard.putNumber("Drive P", Defaults.DRIVE_P);
		SmartDashboard.putNumber("Drive I", Defaults.DRIVE_I);
		SmartDashboard.putNumber("Drive D", Defaults.DRIVE_D);
		SmartDashboard.putNumber("Turn P", Defaults.TURN_P);
		SmartDashboard.putNumber("Turn I", Defaults.TURN_I);
		SmartDashboard.putNumber("Turn D", Defaults.TURN_D);
	}

	private static void ramp(@SuppressWarnings("SameParameterValue") final double seconds) {
		for (final WPI_TalonSRX talon : leftTalons) {
			talon.configOpenloopRamp(seconds, 0);
		}
		for (final WPI_TalonSRX talon : rightTalons) {
			talon.configOpenloopRamp(seconds, 0);
		}
	}

	static void setLeftTalons(final ControlMode controlMode, final double value) {
		leftTalons[0].set(controlMode, value);
	}

	static void setRightTalons(final ControlMode controlMode, final double value) {
		rightTalons[0].set(controlMode, value);
	}

	static void invertLeftTalons(final boolean shouldInvert) {
		for (final WPI_TalonSRX currentTalon : leftTalons) {
			currentTalon.setInverted(shouldInvert);
		}
	}

	static void invertRightTalons(final boolean shouldInvert) {
		for (final WPI_TalonSRX talon : rightTalons) {
			talon.setInverted(shouldInvert);
		}
	}

	static int getLeftEncoder() {
		return leftTalons[0].getSelectedSensorPosition(0);
	}

	static int getRightEncoder() {
		return rightTalons[0].getSelectedSensorPosition(0);
	}

	static int getLeftError() {
		return leftTalons[0].getClosedLoopError(0);
	}

	static int getRightError() {
		return rightTalons[0].getClosedLoopError(0);
	}

	static int getLeftTarget() {
		return leftTalons[0].getClosedLoopTarget(0);
	}

	static int getRightTarget() {
		return rightTalons[0].getClosedLoopTarget(0);
	}

	static int getGear() {
		if (shifter.get() == Value.kForward) {
			return 1;
		} else {
			return 0;
		}
	}

	static void setGear(final int gear) {
		switch (gear) {
			case 0:
				shifter.set(DoubleSolenoid.Value.kReverse);
				break;
			case 1:
				shifter.set(DoubleSolenoid.Value.kForward);
				break;
		}
	}

	static WPI_TalonSRX getLeftTalon() {
		return leftTalons[0];
	}

	static WPI_TalonSRX getRightTalon() {
		return rightTalons[0];
	}

	private static void updatePID(final WPI_TalonSRX[] talons) {
		final double P = SmartDashboard.getNumber("Drive P", Defaults.DRIVE_P);
		final double I = SmartDashboard.getNumber("Drive I", Defaults.DRIVE_I);
		final double D = SmartDashboard.getNumber("Drive D", Defaults.DRIVE_D);

		talons[0].config_kP(0, P, 0);
		talons[0].config_kI(0, I, 0);
		talons[0].config_kD(0, D, 0);
		talons[0].configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
	}
}
