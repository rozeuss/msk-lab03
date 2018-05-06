package dissimlab.simcore;

import dissimlab.simcore.SimParameters.SimEventStatus;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimEventInitializer extends BasicSimFactory {
	private SimContext simContext;

	public SimEventInitializer(SimContext context) {
		this.simContext = context;
	}

	void createStateChangeWaiting(BasicSimObj entity,
			BasicSimEvent<BasicSimObj, Object> stateChange,
			double runSimTime, SimEventStatus status) throws SimControlException {
		if (runSimTime >= simContext.simTime()) {
			stateChange.setRunTime(runSimTime);
			simContext.getSimEventCalendar().add(stateChange);
			stateChange.setSimStatus(status);
		}
		else
			throw new SimControlException("SimTime cannot be decreased");
			//stateChange.setRunSimTime(simContext.getSimTime());
	}
	
	void createSimEventWaitingForTransition(BasicSimObj entity,
			BasicSimEvent<BasicSimObj, Object> stateChange,
			double runSimTime) throws SimControlException {
		createStateChangeWaiting(entity, stateChange,runSimTime, SimEventStatus.WAITINGFORTRANSITION);	
		SimManager.incStChng();
	}

	void createSimEventWaitingForInterruption(BasicSimObj entity,
			BasicSimEvent<BasicSimObj, Object> stateChange,
			double runSimTime) throws SimControlException {
		createStateChangeWaiting(entity, stateChange,runSimTime, SimEventStatus.INTERRUPTED);		
	}

	void createSimEventWaitingForTermination(BasicSimObj entity,
			BasicSimEvent<BasicSimObj, Object> stateChange,
			double runSimTime) throws SimControlException {
		createStateChangeWaiting(entity, stateChange,runSimTime, SimEventStatus.TERMINATED);		
	}

	void createSimEventWaitingOnBarrier(BasicSimObj entity,
			BasicSimEvent<BasicSimObj, Object> stateChange) {
		simContext.getSimConditionalStChngList().add(stateChange);
		stateChange.getSimEventSemaphore().add(stateChange);
		stateChange.setRunTime(simContext.simTime()); //Is the time necessary?
		stateChange.setSimStatus(SimEventStatus.ONBARRIER);
		SimManager.incStChng();
	}

}
