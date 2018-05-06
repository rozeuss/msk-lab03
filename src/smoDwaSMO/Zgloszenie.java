package smoDwaSMO;

import smoDwaSMO.KoniecNiecierpliwienia;
import smoDwaSMO.Smo;
import smoDwaSMO.StartNiecierpliwienia;
import dissimlab.broker.INotificationEvent;
import dissimlab.broker.IPublisher;
import dissimlab.simcore.BasicSimObj;
import dissimlab.simcore.SimControlException;

/**
 * Description: Klasa zgloszenia obsługiwanego w gnieździe obsługi.
 * 
 * @author Dariusz Pierzchala
 */

public class Zgloszenie extends BasicSimObj
{
    double czasOdniesienia;
    static int nr=0;
    int tenNr;
    StartNiecierpliwienia startNiecierpliwienia; 
    public KoniecNiecierpliwienia koniecNiecierpliwienia;
    public Smo smo;

	public Zgloszenie(double Czas, Smo smo) throws SimControlException
    {
        czasOdniesienia = Czas;
        setTenNr();
        this.smo = smo;
        startNiecierpliwienia = new StartNiecierpliwienia(this);
    }

    public void setCzasOdniesienia(double t)
    {
        czasOdniesienia = t;
    }

    public double getCzasOdniesienia()
    {
        return czasOdniesienia;
    }

	public void setTenNr() {
		this.tenNr = nr++;
	}

    public int getTenNr() {
		return tenNr;
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