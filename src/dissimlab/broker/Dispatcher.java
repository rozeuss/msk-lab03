package dissimlab.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class Dispatcher {
	// Map with a name of Event's class as a key and list of registered subscribers as value
	//
	private int id;
	private static int globalId; // Common counter for all Dispatchers 
	private Map<String, List<ISubscriber>> eventTypesAndSubscriptionsMap;
	
	public Dispatcher() {
		this.id = getGlobalId();
		this.eventTypesAndSubscriptionsMap = new HashMap<String, List<ISubscriber>>();
	}

	static int getGlobalId() {
		return globalId++;
	}

	//'eventClass' should be compatible with 'INotificationEvent' interface
	//@SuppressWarnings("unchecked")
	public void subscribe(ISubscriber subscriber, Class<?> eventClass){
		List<ISubscriber> subscribersList;
		// Verification whether 'eventClass' is compatible with 'INotificationEvent' interface
		if(INotificationEvent.class.isAssignableFrom(eventClass)) {
			if (eventTypesAndSubscriptionsMap.containsKey(eventClass.getSimpleName())) {
				subscribersList = eventTypesAndSubscriptionsMap.get(eventClass.getSimpleName());
			} else {
				subscribersList = new ArrayList<ISubscriber>();
			}
			subscribersList.add(subscriber);
			eventTypesAndSubscriptionsMap.put(eventClass.getSimpleName(),subscribersList);
		}
		//Is it necessary to return any boolean value?
	}
	
//	@SuppressWarnings("unchecked")
	public void unsubscribe(ISubscriber subscriber, Class<?> eventClass){
		// Verification whether 'eventClass' is compatible with 'INotificationEvent' interface is not necessary
		if (eventTypesAndSubscriptionsMap.containsKey(eventClass.getSimpleName())){
			List<ISubscriber> subscribersList = eventTypesAndSubscriptionsMap.get(eventClass.getSimpleName());
			subscribersList.remove(subscriber);
			eventTypesAndSubscriptionsMap.put(eventClass.getSimpleName(), subscribersList);
			//Is it necessary to return any boolean value?
		} 
	}
	
	public void publish(IPublisher publisher,INotificationEvent event) {
		if (eventTypesAndSubscriptionsMap.containsKey(event.getClass().getSimpleName())){
			for (ISubscriber subscriber : eventTypesAndSubscriptionsMap.get(event.getClass().getSimpleName())) {
				if (subscriber.filter(publisher, event))
					subscriber.reflect(publisher, event);
			}
		} 		
	}
	
	public int getId() {
		return id;
	}
}
