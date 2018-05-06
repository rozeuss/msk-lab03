package dissimlab.simcore;

import java.util.PriorityQueue;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimEventCalendar {
	/*
	 * An insert on PriorityQuery is documented as O(ln n). Insertion into a
	 * LinkedList is O(n).
	 */
	// private LinkedList<BasicSimEntity.BasicSimStateChange> simStateChangeList;
	private PriorityQueue<BasicSimEvent<BasicSimObj, Object>> simEventQueue;

	private final static int initialLength = 100; //Parameter to initialize based on external value 
	@SuppressWarnings("unused")
	private SimContext simContext;

	public SimEventCalendar(SimContext context) {
		// simStateChangeList = new
		// LinkedList<BasicSimEntity.BasicSimStateChange>();
		this.simContext = context;
		simEventQueue = new PriorityQueue<BasicSimEvent<BasicSimObj, Object>>(
				initialLength, new SimEventsComparator());
	}

	public SimEventCalendar(SimContext context, int initLength) {
		// simStateChangeList = new
		// LinkedList<BasicSimEntity.BasicSimStateChange>();
		this.simContext = context;
		simEventQueue = new PriorityQueue<BasicSimEvent<BasicSimObj, Object>>(
				initLength, new SimEventsComparator());
	}

	public void add(BasicSimEvent<BasicSimObj, Object> object) {
		simEventQueue.add(object);
		// Collections.sort(simStateChangeList, new
		// BasicSimStateChangeComparator());
	}

	public boolean removeThis(BasicSimEvent<BasicSimObj, Object> object) {
		return simEventQueue.remove(object);
	}

	public void removeAll() {
		simEventQueue.clear();
	}

	public int getSize() {
		return simEventQueue.size();
	}

	public BasicSimEvent<BasicSimObj, Object> readFirst() {
		return (BasicSimEvent<BasicSimObj, Object>) simEventQueue.peek();
	}

	public BasicSimEvent<BasicSimObj, Object> getFirst() {
		return (BasicSimEvent<BasicSimObj, Object>) simEventQueue.poll();
	}
}
