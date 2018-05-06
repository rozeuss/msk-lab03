package smoDwaSMO;
/**
 * @author Dariusz Pierzchala
 * 
 * Description: Zdarzenie końcowe aktywności gniazda obsługi. Kończy obsługę przez losowy czas obiektów - zgłoszeń.
 */

import dissimlab.simcore.BasicSimEvent;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimParameters.SimDateField;

public class ZakonczObslugeBis extends BasicSimEvent<SmoBis, Zgloszenie>
{
    private SmoBis smoParent;

    public ZakonczObslugeBis(SmoBis parent, double delay, Zgloszenie zgl) throws SimControlException
    {
    	super(parent, delay, zgl);
        this.smoParent = parent;
    }

	@Override
	protected void onInterruption() throws SimControlException {
        System.out.println(simTime()+" - "+simDate(SimDateField.HOUR24)+" - "+simDate(SimDateField.MINUTE)+" - "+simDate(SimDateField.SECOND)+" - "+simDate(SimDateField.MILLISECOND)+": SMOBis - Przerwanie obsługi przy zgl. nr: " + transitionParams.getTenNr());			
	}

	@Override
	protected void onTermination() throws SimControlException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stateChange() throws SimControlException {
		// Odblokuj gniazdo
		smoParent.setWolne(true);
        System.out.println(simTime()+" - "+simDate(SimDateField.HOUR24)+" - "+simDate(SimDateField.MINUTE)+" - "+simDate(SimDateField.SECOND)+" - "+simDate(SimDateField.MILLISECOND)+": SMOBis - Koniec obsługi zgl. nr: " + transitionParams.getTenNr());
		// Zaplanuj dalsza obsługe
        if (smoParent.liczbaZgl() > 0)
        {
        	smoParent.rozpocznijObsluge = new RozpocznijObslugeBis(smoParent);        	
        }		
	}

	@Override
	public Object getEventParams() {
		// TODO Auto-generated method stub
		return (Zgloszenie) transitionParams;
	}
}