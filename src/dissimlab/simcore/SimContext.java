package dissimlab.simcore;

import java.util.LinkedList;

import dissimlab.broker.Dispatcher;
import dissimlab.simcore.SimParameters.SimEventStatus;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimContext {

	@SuppressWarnings("unused")
	private int id; // Obligatory
	private String name; // Optional
	private SimManager simModel;
	private static int globalId; // Common counter for all simContexts 
	private SimEventInitializer simEventFactory;
	private SimEventCalendar simEventCalendar;
	private LinkedList<BasicSimEvent<BasicSimObj, Object>> simSemaphoreList;
	private SimContextSimObj contextsimObj; // Common simObj for general/common model/context stateChanges
	//private Map<Integer, Dispatcher> contextDispatcherList;

	private SimContext(String name, SimManager simModel) {
		this.name = name;
		this.id = getglobalId();
		this.simModel = simModel;
		this.simEventFactory = new SimEventInitializer(this);
		this.simEventCalendar = new SimEventCalendar(this);
		this.simSemaphoreList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
		this.contextsimObj = new SimContextSimObj(this);
	}

	private SimContext(String name, SimManager simModel, int initCalendarLength) {
		this.name = name;
		this.id = getglobalId();
		this.simModel = simModel;
		this.simEventFactory = new SimEventInitializer(this);
		this.simEventCalendar = new SimEventCalendar(this, initCalendarLength);
		this.simSemaphoreList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
		this.contextsimObj = new SimContextSimObj(this);
	}

	static SimContext getInstance(String name, SimManager simModel) {
		return new SimContext(name, simModel);
	}

	static SimContext getInstance(String name, SimManager simModel, int initCalendarLength) {
		return new SimContext(name, simModel, initCalendarLength);
	}

	static int getglobalId() {
		return globalId++;
	}
	
	void proceedCreateSimEvent(BasicSimObj entity, BasicSimEvent<BasicSimObj, Object> simObj, double delay) throws SimControlException {
		//possible problem with negative delay value - where to verify?
		if (delay >= 0.0){
			simEventFactory.createSimEventWaitingForTransition(entity, simObj, simTime() + delay);
		} else
			throw new SimControlException("Time duration must not be negative");
			//System.err.println("Time duration must not be negative");
	}

	void proceedCreateSimEvent(BasicSimObj entity, BasicSimEvent<BasicSimObj, Object> simEvent) {
		simEventFactory.createSimEventWaitingOnBarrier(entity, simEvent);
	}

	boolean proceedRescheduleSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent, double delay) throws SimControlException {
		if (delay >= 0.0){
			boolean bRemove = getSimEventCalendar().removeThis(simEvent);
			if (bRemove) {
				simEvent.setRunTime(simTime() + delay);
				getSimEventCalendar().add(simEvent);
			}
				//else System.err.println("The stateChange does not exists in Calendar");
			return bRemove;
		} else
			throw new SimControlException("Time duration must not be negative");
			//System.err.println("Time duration must not be negative");
	}

	boolean proceedTerminateSimEvent(BasicSimEvent<BasicSimObj, Object> simObj) throws SimControlException {
		boolean bReturn = false;
		if (simObj.getSimEventSemaphore() == null) {
			bReturn = proceedRescheduleSimEvent(simObj, 0.0);
			if (bReturn)
				simObj.setSimStatus(SimEventStatus.TERMINATED);
		} else {
			bReturn = simSemaphoreList.remove(simObj);			
			if (bReturn) {
				simObj.setSimEventSemaphore(null);
				simEventFactory.createSimEventWaitingForTermination(simObj.getSimObj(), simObj, simTime());
			}
			else
				throw new SimControlException("The event does not exists in Calendar");
				//System.err.println("Time duration must not be negative");
		}
		return bReturn;
	}

	boolean proceedInterruptSimEvent(BasicSimEvent<BasicSimObj, Object> simObj) throws SimControlException {
		boolean bReturn = false;
		if (simObj.getSimEventSemaphore() == null) {
			bReturn = proceedRescheduleSimEvent(simObj, 0.0);
			if (bReturn)
				simObj.setSimStatus(SimEventStatus.INTERRUPTED);
		} else {
			bReturn = simSemaphoreList.remove(simObj);			
			if (bReturn) {
				simObj.setSimEventSemaphore(null);
				simEventFactory.createSimEventWaitingForInterruption(simObj.getSimObj(), simObj, simTime());
			}
			else
				throw new SimControlException("The event does not exists in Calendar");
		}
		return bReturn;
	}

	void proceedStopSimulation() {
		simModel.stopSimulation();
	}
	
	void proceedPauseSimulation() {
		simModel.pauseSimulation();
	}

	void proceedResumeSimulation() {
		simModel.resumeSimulation();
	}

	double simTime() {
		return simModel.simTime();
	}

	/**
	 *
	 *'0' means January and '11' = December | 
	 *Days of month are counted from '1' to ... 28,29,30 or 31 | 		
	 *'24' hours in a day
	 */
	public int simDate(SimParameters.SimDateField field) {
		return simModel.simDate(field);
	}
	
	public double getSimTimeStep() {
		return simModel.getSimTimeStep();
	}

	public void setSimTimeStep(double simTimeStep) {
		simModel.setSimTimeStep(simTimeStep);
	}

	public double getSimTimeScale() {
		return simModel.getSimTimeScale();
	}

	public void setSimTimeScale(double simTimeScale) {
		simModel.setSimTimeStep(simTimeScale);
	}

	public LinkedList<BasicSimEvent<BasicSimObj, Object>> getSimConditionalStChngList() {
		return simSemaphoreList;
	}

	public SimEventCalendar getSimEventCalendar() {
		return simEventCalendar;
	}

	public SimContextSimObj getContextsimObj() {
		return contextsimObj;
	}

	public Dispatcher getDispatcher() {
		return simModel.getDispatcher();
	}

	public void clearContext() {
		simEventCalendar.removeAll();
		simSemaphoreList.removeAll(simSemaphoreList);
	}
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
