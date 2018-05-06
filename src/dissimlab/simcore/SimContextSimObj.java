package dissimlab.simcore;

import dissimlab.broker.INotificationEvent;
import dissimlab.broker.IPublisher;



/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimContextSimObj extends BasicSimObj {

	public SimContextSimObj(SimContext context) {
		super(context);
	}

	@Override
	public void reflect(IPublisher publisher, INotificationEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean filter(IPublisher publisher, INotificationEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
