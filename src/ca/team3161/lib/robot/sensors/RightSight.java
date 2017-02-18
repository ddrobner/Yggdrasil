package org.usfirst.frc.team3161.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class RightSight extends DigitalInput {
	
	private volatile boolean inverted = false;

	public RightSight(int channel) {
		super(channel);
	}
	
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	
	@Override
	public boolean get() {
		boolean val = super.get();
		if (inverted) {
			return !val;
		} else {
			return val;
		}
	}

}
