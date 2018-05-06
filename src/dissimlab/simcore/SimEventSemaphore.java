package dissimlab.simcore;

import java.util.LinkedList;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimEventSemaphore {
	private int id; // Obligatory
	private static int globalId; // Common counter for all barriers
	private String name; // Optional
	private LinkedList<BasicSimEvent<BasicSimObj, Object>> simEventList;

	public SimEventSemaphore() {
		this.simEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
		this.id = SimEventSemaphore.getglobalId();
	}

	public SimEventSemaphore(String name) {
		this.simEventList = new LinkedList<BasicSimEvent<BasicSimObj, Object>>();
		this.id = SimEventSemaphore.getglobalId();
		this.name = name;
	}
	/*
	 * Activates all but StateChanges when more are blocked
	 */
	public void open() throws SimControlException {
		while (!simEventList.isEmpty()){
			BasicSimEvent<BasicSimObj, Object> state = this.removeFirstState();
			state.setSimEventSemaphore(null);
			state.getSimObj().getContextInstance().proceedCreateSimEvent(state.getSimObj(), state, 0.0);
		}		
	}

	public BasicSimEvent<BasicSimObj, Object> readFirstBlocked(){
		return simEventList.getFirst();
	}
	
	public int numberOfBlocked() {
		return getNumberOfStates();
	}
	void add(BasicSimEvent<BasicSimObj, Object> object) {
		simEventList.add(object);
	}

	boolean removeThis(BasicSimEvent<BasicSimObj, Object> object) {
		return simEventList.remove(object);
	}

	void removeAll() {
		simEventList.clear();
	}

	int getNumberOfStates() {
		return simEventList.size();
	}

	BasicSimEvent<BasicSimObj, Object> removeFirstState() {
		return (BasicSimEvent<BasicSimObj, Object>) simEventList.poll();
	}
	
	static int getglobalId() {
		return globalId++;
	}

/*
	public void terminateStateChange(
			BasicSimStateChange<BasicSimEntity, Object> stateChange) {
		simStChngList.remove(stateChange);		
	}

	public void terminateAllStateChanges() {
		for (BasicSimStateChange<BasicSimEntity, Object> stateChange : simStChngList) 
		{
			
		}
	}
*/
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	LinkedList<BasicSimEvent<BasicSimObj, Object>> getSimConditionalStChngList() {
		return simEventList;
	}

	@Override
	public String toString() {
		return name;
	}
}
