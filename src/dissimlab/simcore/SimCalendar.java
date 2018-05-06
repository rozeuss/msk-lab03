package dissimlab.simcore;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimCalendar {
	private int referenceAstronomicalYear = SimParameters.referenceAstronomicalYear;	
	private int referenceAstronomicalMonth = SimParameters.referenceAstronomicalMonth; //Calendar requests: '0' = January and '11' = December		
	private int referenceAstronomicalDayMonth = SimParameters.referenceAstronomicalDayMonth; //Day of month, counted from '1' to ... 28,29,30 or 31		
	private int referenceAstronomicalHour = SimParameters.referenceAstronomicalHour24; //'24' hours in a day		
	private int referenceAstronomicalMinute = SimParameters.referenceAstronomicalMinute;		
	private int referenceAstronomicalSecond = SimParameters.referenceAstronomicalSecond;		
	//
	private GregorianCalendar calendar;
	private double lastSimTime = 0.0;
	//
	private static SimCalendar simCalendar = new SimCalendar(); // Singleton

	public static SimCalendar getInstance() {
		if (simCalendar == null) {
			simCalendar = new SimCalendar();
		}
		return simCalendar;
	}
	public SimCalendar() {
		calendar = new GregorianCalendar(
				referenceAstronomicalYear,
				referenceAstronomicalMonth,
				referenceAstronomicalDayMonth,
				referenceAstronomicalHour,
				referenceAstronomicalMinute,
				referenceAstronomicalSecond);
	}

	public int getSimDate(SimParameters.SimDateField field, double currentSimTime, double simTimeScale) {
		double deltaTime; 
		if (simTimeScale > 0.0){
			deltaTime = simTimeScale * (currentSimTime - lastSimTime);
			lastSimTime = currentSimTime;
		} else {
			deltaTime = 0.0;
		}		
		calendar.add(Calendar.SECOND, (int)(deltaTime)); 
		//
		switch (field) {
		case YEAR:
			return calendar.get(Calendar.YEAR);  
		case MONTH:
			return calendar.get(Calendar.MONTH); 
		case DAYMONTH:
			return calendar.get(Calendar.DAY_OF_MONTH);
		case HOUR24:
			return calendar.get(Calendar.HOUR); 
		case MINUTE:
			return calendar.get(Calendar.MINUTE); 
		case SECOND:
			return calendar.get(Calendar.SECOND); 
		case MILLISECOND:
			return calendar.get(Calendar.MILLISECOND); 
		case DAYWEEK:
			return calendar.get(Calendar.DAY_OF_WEEK); 
		default:
			return -1;
		}
	}
}
