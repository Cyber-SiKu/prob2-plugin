package de.prob2.rodin.disprover.core.internal;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;
import org.eventb.core.seqprover.tactics.BasicTactics;

import de.prob2.rodin.disprover.core.*;

public class DisproverAutoTactic extends AbsractLazilyConstrTactic {
	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new DisproverReasoner(1),
				new DisproverReasonerInput());
	}
}