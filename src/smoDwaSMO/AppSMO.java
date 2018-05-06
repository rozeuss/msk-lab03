package smoDwaSMO;

/**
 * @author Dariusz Pierzchala
 * 
 * Description: Klasa główna. Tworzy dwa SMO, inicjalizuje je.Startuje symulację. Wyświetla statystyki.
 * 
 * Wersja testowa.
 */

import java.math.BigDecimal;
import java.util.Date;

import dissimlab.monitors.Change;
import dissimlab.monitors.Diagram;
import dissimlab.monitors.Diagram.DiagramType;
import dissimlab.monitors.Statistics;
import dissimlab.simcore.SimControlEvent;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimEventSemaphore;
import dissimlab.simcore.SimManager;
import dissimlab.simcore.SimParameters.SimControlStatus;

public class AppSMO {
	public static void main(String[] args) {
		try {
			SimManager model = SimManager.getInstance();
			//Utylizator zgłoszeń
			UtylizatorKoncowy kosz = new UtylizatorKoncowy();
			//Semafor
			SimEventSemaphore semafor = new SimEventSemaphore("Semafor dla SMO");
			// Powołanie 2-ego Smo
			SmoBis smoBis = new SmoBis(5, semafor);
			// Powołanie Smo nr 1 
			Smo smo = new Smo(smoBis, semafor);
			// Utworzenie otoczenia
			Otoczenie generatorZgl = new Otoczenie(smo);
			// Dwa sposoby zaplanowanego końca symulacji
			//model.setEndSimTime(10000);
			// lub
			SimControlEvent stopEvent = new SimControlEvent(1000.0, SimControlStatus.STOPSIMULATION);
			// Badanie czasu trwania eksperymentu - początek
			long czst = new Date().getTime();
			// Uruchomienie symulacji za pośrednictwem metody "start" z
			model.startSimulation();
			// Badanie czasu trwania eksperymentu - koniec
			czst = new Date().getTime() - czst;
			
			// Wyniki
			System.out.println("Czas trwania eksperymentu: " + czst);

			// Formatowanie liczby do 2 miejsc po przecinku		
			double wynik = new BigDecimal(Statistics
					.arithmeticMean(smo.MVczasy_oczekiwania)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out
					.println("Wartość średnia czasu oczekiwania na obsługę:   "
							+ wynik);
			wynik = new BigDecimal(Statistics
					.standardDeviation(smo.MVczasy_oczekiwania)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out
					.println("Odchylenie standardowe dla czasu obsługi:       "
							+ wynik);
			wynik = new BigDecimal(Statistics.max(smo.MVczasy_oczekiwania))
			.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out.println("Wartość maksymalna czasu oczekiwania na obsługę: "
					+ wynik);
			wynik = new BigDecimal(Statistics
					.arithmeticMean(smo.MVdlKolejki)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out
					.println("Wartość średnia długości kolejki:       "
							+ wynik);
			wynik = new BigDecimal(Statistics
					.max(smo.MVdlKolejki)).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out
					.println("Wartość maksymalna długości kolejki:       "
							+ wynik);


			Diagram d1 = new Diagram(DiagramType.DISTRIBUTION, "Czas obsługiwania");
			d1.add(smo.MVczasy_obslugi, java.awt.Color.GREEN);
			d1.show();

			Diagram d2 = new Diagram(DiagramType.DISTRIBUTION,
					"Dlugość kolejki w SMO nr 2");
			d2.add(smoBis.MVdlKolejki, java.awt.Color.BLUE);
			d2.show();

			Diagram d3 = new Diagram(DiagramType.HISTOGRAM,
					"Czasy oczekiwania na obsługę");
			d3.add(smo.MVczasy_oczekiwania, java.awt.Color.BLUE);
			d3.show();

			Diagram d4 = new Diagram(DiagramType.DISTRIBUTION,
					"Długość kolejki w czasie");
			d4.add(smo.MVdlKolejki, java.awt.Color.RED);
			d4.show();
		} catch (SimControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
