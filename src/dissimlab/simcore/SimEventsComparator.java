package dissimlab.simcore;

import java.util.Comparator;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimEventsComparator
		implements
		Comparator<BasicSimEvent<BasicSimObj, Object>> {

	public SimEventsComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(
			BasicSimEvent<BasicSimObj, Object> o1,
			BasicSimEvent<BasicSimObj, Object> o2) {
		double delta = o1.getRunTime() - o2.getRunTime();
		int rank = o1.getSimPriority() - o2.getSimPriority();
		// First time and second priority
		if (delta < 0)
			return -1;
		else if (delta > 0)
			return 1;
		else
			return rank;
	}

}
