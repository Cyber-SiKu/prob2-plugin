package de.prob2.rodin.disprover.core;

import org.eventb.core.seqprover.IReasoner;

public class Disprover {

	private Disprover() {
		throw new RuntimeException("This constructor should never be called.");
	}

	/**
	 * Factory method for creating a new DisproverReasoner. We want to keep the
	 * actual Disprover private to the package, mainly to loosen access to the
	 * internal methods for testing.
	 * 
	 * @return a new {@link DisproverReasoner} instance.
	 */
	public static IReasoner createDisproverReasoner() {
		return new DisproverReasoner(1);
	}

	public static IReasoner createExtendedTimeoutDisproverReasoner(int factor) {
		return new DisproverReasoner(factor);
	}

}
