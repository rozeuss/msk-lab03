package dissimlab.simcore;


/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimParameters {
	static double MinSimTimeValue = 0.0;
	static double MaxSimTimeValue = Double.MAX_VALUE;
	static double DefaultSimTimeStep = 3600; //??
	static double DefaultSimTimeScale = 1; //'number [second]' per 'number [simtime unit]'
	static double DefaultSimTimeRatio = 2; //'simtime' to 'astronomical time' - '2:1' means: simulation goes 2 times faster than astronomical
	//
	static int referenceAstronomicalYear = 2014;		
	static int referenceAstronomicalMonth = 0; //'0' means January and '11' = December
	static int referenceAstronomicalDayMonth = 1; //Day of month, counted from '1' to ... 28,29,30 or 31		
	static int referenceAstronomicalHour24 = 0; //'24' hours in a day
	static int referenceAstronomicalMinute = 0;		
	static int referenceAstronomicalSecond = 0;		
	//
	static int defaultAstronomicalTimeStep = 1000; //Astronomical time step used for duration in each iteration of: Pause,  
	//
	public static int MinSimPriority = 0;
	public static int DefaultSimPriority = 5;
	public static int MaxSimPriority = 10;
	//
	static int StopSimPriority = 10;
	public static int PauseSimPriority = 10;
	static int SaveStateSimPriority = 0;
	//
	//static double StateSavePeriod = 100;
	/**
	 * Description...
	 * 
	 * @author Dariusz Pierzchala
	 *
	 */
	public enum SimMode {
		ASAP, ASTRONOMICAL; 
	}
	/**
	 * Description...
	 * 
	 * @author Dariusz Pierzchala
	 *
	 */
	public enum SimProcessStatus {
		IDLE, RUNNING, RESUMED, PAUSED, RESTARTED, WAITINGFORSTOP, STOPPED; 
	}
	/**
	 * Description...
	 * 
	 * @author Dariusz Pierzchala
	 *
	 */
	public enum SimControlStatus {
		STARTSIMULATION, STOPSIMULATION, PAUSESIMULATION, RESUMESIMULATION, RESTARTSIMULATION, SAVESIMULATION; 
	}

	/**
	 * Description...
	 * 
	 * @author Dariusz Pierzchala
	 *
	 */
	public enum SimEventStatus {
		WAITINGFORTRANSITION, ONBARRIER, INTERRUPTED, TERMINATED, PROCESSED, DONE;
	}
	/**
	 * Description...
	 * 
	 * @author Dariusz Pierzchala
	 *
	 */
	public enum SimDateField {
		YEAR, MONTH, DAYMONTH, HOUR24, MINUTE, SECOND, MILLISECOND, DAYWEEK;
	}
}
