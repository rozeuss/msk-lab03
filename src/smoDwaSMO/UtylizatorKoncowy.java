package smoDwaSMO;
/**
 * @author Dariusz Pierzchala
 * 
 * Description: Obiekt przejmujący zgłoszenia do utylizacji. Działa w wyniku powiadomień z brokera (na sybskrybowane zdarzenia)
 */

import dissimlab.broker.Dispatcher;
import dissimlab.broker.INotificationEvent;
import dissimlab.broker.IPublisher;
import dissimlab.simcore.BasicSimObj;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimParameters.SimDateField;



public class UtylizatorKoncowy extends BasicSimObj
{
	//
	public Dispatcher infoDystr;
	//

    public UtylizatorKoncowy() throws SimControlException
    {
    	super();
    	infoDystr = getDispatcher();
    	infoDystr.subscribe(this, ZakonczObslugeBis.class);
    }


	@Override
	public void reflect(IPublisher publisher, INotificationEvent event) {
		System.out.println(simTime()+" - "+simDate(SimDateField.HOUR24)+" - "+simDate(SimDateField.MINUTE)+" - "+simDate(SimDateField.SECOND)+" - "+simDate(SimDateField.MILLISECOND)+" - Utylizator końcowy: zgloszenie nr: " + ((Zgloszenie)((ZakonczObslugeBis)event).getEventParams()).getTenNr());
	}


	@Override
	public boolean filter(IPublisher arg0, INotificationEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}