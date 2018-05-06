package smo;

import dissimlab.random.SimGenerator;
import dissimlab.simcore.BasicSimEvent;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimParameters.SimDateField;

/**
 * 
 * @author Dariusz Pierzchala
 * Description: Zdarzenie początkowe niecierpliwości zgłoszenia. Rozpoczyna niecierpliwość przez losowy czas
 */
public class StartNiecierpliwienia extends BasicSimEvent<Zgloszenie, Object>
{
    private SimGenerator generator;
    private Zgloszenie parent;

    public StartNiecierpliwienia(Zgloszenie parent, double delay) throws SimControlException
    {
    	super(parent, delay);
    	generator = new SimGenerator();
        this.parent = parent;
    }

    public StartNiecierpliwienia(Zgloszenie parent) throws SimControlException
    {
    	super(parent);
    	generator = new SimGenerator();
        this.parent = parent;
    }
    
	@Override
	protected void onInterruption() throws SimControlException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTermination() throws SimControlException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stateChange() throws SimControlException {
        System.out.println(simTime()+" - "+simDate(SimDateField.HOUR24)+" - "+simDate(SimDateField.MINUTE)+" - "+simDate(SimDateField.SECOND)+" - "+simDate(SimDateField.MILLISECOND)+": Początek niecierpliwości zgl. nr: " + parent.getTenNr());
        double odstep = generator.normal(15.0, 1.0);
        parent.koniecNiecierpliwosci = new KoniecNiecierpliwienia(parent, odstep);
	}

	@Override
	public Object getEventParams() {
		// TODO Auto-generated method stub
		return null;
	}
}