package smoDwaSMO;
/**
 * @author Dariusz Pierzchala
 * 
 * Description: Klasa gniazda obsługi obiektów - zgłoszeń 
 */

import java.util.LinkedList;

import smoDwaSMO.RozpocznijObslugeBis;
import smoDwaSMO.ZakonczObslugeBis;
import smoDwaSMO.Zgloszenie;
import dissimlab.broker.INotificationEvent;
import dissimlab.broker.IPublisher;
import dissimlab.monitors.MonitoredVar;
import dissimlab.simcore.BasicSimObj;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimEventSemaphore;

public class SmoBis extends BasicSimObj 
{
    private LinkedList <Zgloszenie> kolejka;
    private boolean wolne = true;
    private int maxDlKolejki;
    //
	SimEventSemaphore semafor;
	//
    public RozpocznijObslugeBis rozpocznijObsluge;
    public ZakonczObslugeBis zakonczObsluge;
    //
    public MonitoredVar MVczasy_obslugi;
    public MonitoredVar MVczasy_oczekiwania;
    public MonitoredVar MVdlKolejki;

    public SmoBis(int maxDlKolejki, SimEventSemaphore semafor) throws SimControlException
    {
        kolejka = new LinkedList <Zgloszenie>();
        this.maxDlKolejki = maxDlKolejki;
        this.semafor = semafor;
        //
        // Deklaracja zmiennych monitorowanych
        MVczasy_obslugi = new MonitoredVar();
        MVczasy_oczekiwania = new MonitoredVar();
        MVdlKolejki = new MonitoredVar();
    }

    // Wstawienie zgłoszenia do kolejki
    public boolean dodaj(Zgloszenie zgl)
    {
    	if(liczbaZgl() < maxDlKolejki){
    		kolejka.add(zgl);
    		MVdlKolejki.setValue(kolejka.size());
    		return true;
    	}
        return false;
    }

    // Pobranie zgłoszenia z kolejki
    public Zgloszenie usun()
    {
    	Zgloszenie zgl = (Zgloszenie) kolejka.removeFirst();
        MVdlKolejki.setValue(kolejka.size());
        return zgl;
    }

    // Pobranie zgłoszenia z kolejki
    public boolean usunWskazany(Zgloszenie zgl)
    {
    	Boolean b= kolejka.remove(zgl);
        MVdlKolejki.setValue(kolejka.size());
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
	
	public SimEventSemaphore getSemafor() {
		return semafor;
	}

	public void setSemafor(SimEventSemaphore semafor) {
		this.semafor = semafor;
	}
	
	public int getMaxDlKolejki() {
		return maxDlKolejki;
	}

	public void setMaxDlKolejki(int maxDlKolejki) {
		this.maxDlKolejki = maxDlKolejki;
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