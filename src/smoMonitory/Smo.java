package smoMonitory;
/**
 * @author Dariusz Pierzchala
 * 
 * Description: Description: Klasa gniazda obsługi obiektów - zgłoszeń 
 */

import java.util.LinkedList;

import smoMonitory.RozpocznijObsluge;
import smoMonitory.ZakonczObsluge;
import smoMonitory.Zgloszenie;
import dissimlab.broker.INotificationEvent;
import dissimlab.broker.IPublisher;
import dissimlab.monitors.MonitoredVar;
import dissimlab.simcore.BasicSimObj;
import dissimlab.simcore.SimEventSemaphore;
import dissimlab.simcore.SimControlException;



public class Smo extends BasicSimObj
{
    private LinkedList <Zgloszenie> kolejka;
	private boolean wolne = true;
    public RozpocznijObsluge rozpocznijObsluge;
    public ZakonczObsluge zakonczObsluge;
    // Zmienne monitorowane
    //public MonitoredVar MVczasy_obslugi;
    public MonitoredVar MVczasy_oczekiwania;
    //public MonitoredVar MVdlKolejki;
    //public MonitoredVar MVutraconeZgl;

	
    /** Creates a new instance of Smo 
     * @throws SimControlException */
    public Smo() throws SimControlException
    {
        // Utworzenie wewnętrznej listy w kolejce
        kolejka = new LinkedList <Zgloszenie>();
        // Powołanie zmiennych monitorowanych
        MVczasy_oczekiwania = new MonitoredVar();
    }

    // Wstawienie zgłoszenia do kolejki
    public int dodaj(Zgloszenie zgl)
    {
        kolejka.add(zgl);
        return kolejka.size();
    }

    // Pobranie zgłoszenia z kolejki
    public Zgloszenie usun()
    {
    	Zgloszenie zgl = (Zgloszenie) kolejka.removeFirst();
        return zgl;
    }

    // Pobranie zgłoszenia z kolejki
    public boolean usunWskazany(Zgloszenie zgl)
    {
    	Boolean b= kolejka.remove(zgl);
        return b;
    }
    
    public int liczbaZgl()
    {
        return kolejka.size();
    }

	public boolean isWolne() {
		return wolne;
	}

	public void setWolne(boolean wolne) {
		this.wolne = wolne;
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