package dissimlab.broker;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public interface ISubscriber {
	
	public void reflect(IPublisher publisher, INotificationEvent event);
	public boolean filter(IPublisher publisher, INotificationEvent event);	
}
