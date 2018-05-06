package smoMonitory;

/**
 * @author Dariusz Pierzchala
 * 
 * Description: Klasa główna. Tworzy dwa SMO, inicjalizuje je.Startuje symulację. Wyświetla statystyki.
 * 
 * Wersja testowa.
 */

import java.math.BigDecimal;

import dissimlab.monitors.Diagram;
import dissimlab.monitors.Statistics;
import dissimlab.monitors.Diagram.DiagramType;
import dissimlab.simcore.SimControlEvent;
import dissimlab.simcore.SimControlException;
import dissimlab.simcore.SimManager;
import dissimlab.simcore.SimParameters.SimControlStatus;

public class AppSMO {
	public static void main(String[] args) {
		try {
			SimManager model = SimManager.getInstance();
			// Powołanie Smo 
			Smo smo = new Smo();
			// Utworzenie otoczenia
			Otoczenie generatorZgl = new Otoczenie(smo);
			// Dwa sposoby zaplanowanego końca symulacji
			//model.setEndSimTime(10000);
			// lub
			SimControlEvent stopEvent = new SimControlEvent(1000.0, SimControlStatus.STOPSIMULATION);
			// Uruchomienie symulacji za pośrednictwem metody "startSimulation" 
			model.startSimulation();

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

			Diagram d3 = new Diagram(DiagramType.HISTOGRAM,
					"Czasy oczekiwania na obsługę");
			d3.add(smo.MVczasy_oczekiwania, java.awt.Color.BLUE);
			d3.show();
			
		} catch (SimControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
