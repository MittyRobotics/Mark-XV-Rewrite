package frc.robot.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public final class Intake {
	private static final int[] INTAKE_TALONS = {6, 7};

	private static WPI_TalonSRX[] talons;
	private static DoubleSolenoid intakeExtend, intakeClose40, intakeClose60;

	private static long time = 0;
	private static Pressure pressure = Pressure.Hard;

	public static void init() {
		talons = new WPI_TalonSRX[INTAKE_TALONS.length];
		talons[0] = new WPI_TalonSRX(INTAKE_TALONS[0]);
		talons[1] = new WPI_TalonSRX(INTAKE_TALONS[1]);
		talons[1].setInverted(true);
		talons[1].set(ControlMode.Follower, INTAKE_TALONS[0]);

		intakeClose40 = new DoubleSolenoid(2, 3);
		intakeClose60 = new DoubleSolenoid(0, 1);
		intakeExtend = new DoubleSolenoid(4, 5);
	}

	public static void intake() {
		talons[0].set(ControlMode.PercentOutput, -1);
	}

	public static void extake() {
		talons[0].set(ControlMode.PercentOutput, 1);
	}

	public static void halt() {
		talons[0].set(ControlMode.PercentOutput, 0);
	}

	public static synchronized void toggle() {
		if (System.currentTimeMillis() - time > 500) {
			time = System.currentTimeMillis();
			if (pressure == Pressure.Hard) {
				release();
			} else if (pressure == Pressure.Open) {
				hold();
			} else {
				release();
			}
		}
	}

	public static void hold() {
		intakeClose40.set(Value.kReverse); //40 branch off
		intakeClose60.set(Value.kForward); //60 branch on
		pressure = Pressure.Hard;
	}

	public static void release() {
		intakeClose40.set(Value.kForward); //40 branch on
		intakeClose60.set(Value.kReverse); //60 branch off
		pressure = Pressure.Open;
	}

	public static synchronized void soft() {
		time = System.currentTimeMillis();
		intakeClose40.set(Value.kForward);
		intakeClose60.set(Value.kForward);
		pressure = Pressure.Soft;
	}

	public static void lower() {
		intakeExtend.set(Value.kReverse);
	}

	public static void raise() {
		intakeExtend.set(Value.kForward);
	}

	enum Pressure {
		Hard, Open, Soft
	}
}
