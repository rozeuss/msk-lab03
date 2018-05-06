package dissimlab.simcore;

import java.util.LinkedList;

import dissimlab.broker.Dispatcher;
import dissimlab.broker.IPublisher;
import dissimlab.broker.ISubscriber;
import dissimlab.simcore.SimParameters.SimControlStatus;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */

public abstract class BasicSimObj implements IPublisher, ISubscriber{
	private SimContext contextRoot;
	//private PriorityQueue<BasicSimStateChange<BasicSimEntity, Object>> simStChngList;
	//private final static int initialLength = 10; //Parameter to initialize based on external value 
	private LinkedList<BasicSimEvent<BasicSimObj, Object>> simSimEventList;

	//SimObj created in the default context for the whole model
	public BasicSimObj() {
		this.contextRoot = SimManager.getInstance().getCommonSimContext();
		this.simSimEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
	}
	
	public BasicSimObj(SimContext context) {
		this.contextRoot = context;
		this.simSimEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
	}
	
	void createSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent,
			double delay) throws SimControlException {
		if (contextRoot != null) {
			contextRoot.proceedCreateSimEvent(this, simEvent, delay);
			simSimEventList.add(simEvent);
		} else
			throw new SimControlException("Simulation context does not exist");
			//System.err.println("Simulation context does not exist");
	}

	void createSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent) throws SimControlException {
		if (contextRoot != null) {
			contextRoot.proceedCreateSimEvent(this, simEvent);
			simSimEventList.add(simEvent);
		} else
			throw new SimControlException("Simulation context does not exist");
		//System.err.println("Simulation context does not exist");
	}
	
	boolean proceedRescheduleSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent,double delay) throws SimControlException {
		//change runSimTime and sort calendar consequently
		if (getSimStateChangeList().contains(simEvent)) {
			if (contextRoot != null) {
				return contextRoot.proceedRescheduleSimEvent(simEvent, delay);
			} else
				throw new SimControlException("Simulation context does not exist");
		} else
			throw new SimControlException("SimEntity does not contain the stateChange");		
	}

	void transformSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent) throws SimControlException{
		removeThis(simEvent);
		simEvent.service();
	}
	
/*
	public void terminateStateChange(BasicSimStateChange<BasicSimEntity, Object> stateChange) throws SimControlException {
		if (stateChange != null) {
			stateChange.terminate();
		} else
			throw new SimControlException("State change does not exist");
	}
*/
	boolean proceedTerminateSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent) throws SimControlException {
		//change runSimTime and sort calendar consequently
		if (contextRoot != null) {
			return contextRoot.proceedTerminateSimEvent(simEvent); // Is it necessary?
		} else
			throw new SimControlException("Simulation context does not exist");
	}

	public void terminateAllSimEvents() throws SimControlException {
		//change runSimTime and sort calendar consequently
		if (contextRoot != null) {
			//Copy states to temporary list
			LinkedList<BasicSimEvent<BasicSimObj, Object>> simEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
			for (BasicSimEvent<BasicSimObj, Object> simEvent : simSimEventList) {
				simEventList.add(simEvent);
			}
			//Process states from temporary list - finally, it results in "simStChngList"
			for (BasicSimEvent<BasicSimObj, Object> stateChange : simEventList) {
				stateChange.terminate();
			}
		}
		else
			throw new SimControlException("Simulation context does not exist");
	}

	boolean proceedInterruptSimEvent(BasicSimEvent<BasicSimObj, Object> simEvent) throws SimControlException {
		if (contextRoot != null) {
			return contextRoot.proceedInterruptSimEvent(simEvent);
		} else
			throw new SimControlException("Simulation context does not exist");
	}
/*
	public void interruptStateChange(BasicSimStateChange<BasicSimEntity, Object> stateChange) throws SimControlException {
		//change runSimTime and sort calendar consequently
		if (stateChange != null) {
			stateChange.interrupt();
		} else
			throw new SimControlException("State change does not exist");
	}
*/
	public void interruptAllSimEvents() throws SimControlException {
		//change runSimTime and sort calendar consequently
		if (contextRoot != null) {
			//Copy states to temporary list
			LinkedList<BasicSimEvent<BasicSimObj, Object>> simEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
			for (BasicSimEvent<BasicSimObj, Object> simEvent : simSimEventList) {
				simEventList.add(simEvent);
			}
			//Process states from temporary list - finally, it results in "simStChngList"
			for (BasicSimEvent<BasicSimObj, Object> simEvent : simEventList) {
				simEvent.interrupt();
			}
		} else
			throw new SimControlException("Simulation context does not exist");
	}
	
	void proceedStopSimulation() {
		getContextInstance().proceedStopSimulation();
	}
	
	void proceedPauseSimulation() {
		getContextInstance().proceedPauseSimulation();
	}
	
	protected void stopSimulation(double duration) throws SimControlException {
		if (duration >=0.0) {
			@SuppressWarnings("unused")
			SimControlEvent stopEvent = new SimControlEvent(duration, SimControlStatus.STOPSIMULATION, SimParameters.StopSimPriority);
		}
		else
			throw new SimControlException("Time duration must not be negative");	
	}

	protected void stopSimulation() throws SimControlException {
		stopSimulation(0.0);
	}

	public double simTime() {
		return contextRoot.simTime();
	}

	/**
	 *
	 *'0' means January and '11' = December | 
	 *Days of month are counted from '1' to ... 28,29,30 or 31 | 		
	 *'24' hours in a day
	 */
	public int simDate(SimParameters.SimDateField field) {
		return contextRoot.simDate(field);
	}
	
	public double getSimTimeStep() {
		return contextRoot.getSimTimeStep();
	}

	public void setSimTimeStep(double simTimeStep) {
		contextRoot.setSimTimeStep(simTimeStep);
	}

	public double getSimTimeScale() {
		return contextRoot.getSimTimeScale();
	}

	public void setSimTimeScale(double simTimeScale) {
		contextRoot.setSimTimeStep(simTimeScale);
	}
	
	public LinkedList<BasicSimEvent<BasicSimObj, Object>> getSimStateChangeList() {
		return simSimEventList;
	}

	void add(BasicSimEvent<BasicSimObj, Object> object) {
		simSimEventList.add(object);
		// Collections.sort(simStateChangeList, new
		// BasicSimStateChangeComparator());
	}

	boolean removeThis(BasicSimEvent<BasicSimObj, Object> object) {
		return simSimEventList.remove(object);
	}

	void removeAll() {
		simSimEventList.clear();
	}

	int getSize() {
		return simSimEventList.size();
	}

	BasicSimEvent<BasicSimObj, Object> getFirst() {
		return (BasicSimEvent<BasicSimObj, Object>) simSimEventList.poll();
	}
	
	public SimContext getContextInstance() {
		return contextRoot;
	}
	
	public Dispatcher getDispatcher() {
		return contextRoot.getDispatcher();
	}
}
