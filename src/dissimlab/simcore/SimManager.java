package dissimlab.simcore;

import dissimlab.broker.Dispatcher;
import dissimlab.simcore.SimParameters.SimMode;
import dissimlab.simcore.SimParameters.SimProcessStatus;

/**
 * Description...
 * 
 * @author Dariusz Pierzchala
 *
 */
public class SimManager {
	private double currentSimTime = SimParameters.MinSimTimeValue;
	private double endSimTime = SimParameters.MaxSimTimeValue;
	private double simTimeScale = SimParameters.DefaultSimTimeScale;//'number [second]' per 'number [simtime unit]'
	private double simTimeRatio = SimParameters.DefaultSimTimeRatio;//'simtime' to 'astronomical time' - '2:1' means: simulation goes 2 times faster than astronomical
	private double astronomicalTimeStep = SimParameters.defaultAstronomicalTimeStep;
	private double astronomicalTimeShift = SimParameters.MinSimTimeValue;
	private double pauseStartTime = 0.0;
	private double astronomicalTimeCorrection = 0.0;
	
	private SimMode simMode = SimMode.ASAP;//ASTRONOMICAL;
	private SimProcessStatus controlState = SimProcessStatus.IDLE;
	private SimCalendar simCalendar;
	private SimContext commonSimContext;  
	private long eventsProcessed = 0;
	
	private static SimManager simManager = new SimManager(); // Singleton
    private static int stChngCounter = 0;
    private Dispatcher dispatcher;
    
	public static int getStChngCounter() {
		return stChngCounter;
	}
    
    public static void incStChng() {
		stChngCounter++;
	}

	public static SimManager getInstance() {
		if (simManager == null) {
			simManager = new SimManager();
		}
		return simManager;
	}
	
	public static SimManager resetInstance() {
		simManager = null;
		return getInstance();
	}

	private SimManager() {
		simCalendar = SimCalendar.getInstance();
		commonSimContext = SimContext.getInstance("Common SimContext", this);
		dispatcher = new Dispatcher();
	}

	public SimProcessStatus getControlStatus() {
		return controlState;
	}
	
	public SimContext getCommonSimContext() {
		return commonSimContext;
	}

	public void restartSimulation(double currentSimTime)
			throws SimControlException {
		if (controlState == SimProcessStatus.IDLE) {
			setCurrentSimTime(currentSimTime);
			startSimulation();
		} else
			//throw new SimControlException("Simulation already started");
			System.err.println("Simulation already started");

	}

	public void startSimulation() throws SimControlException {
		if (controlState == SimProcessStatus.IDLE) {	
			mainloop: while (currentSimTime <= endSimTime && controlState != SimProcessStatus.STOPPED) {
				switch (controlState) {
				case IDLE:
					controlState = SimProcessStatus.RUNNING;
					System.out.println(simTime()+": SimModel: Simulation run");					
					//
					if (simMode == SimMode.ASTRONOMICAL){
						astronomicalTimeShift = System.currentTimeMillis();
						astronomicalTimeCorrection = 0.0;
					} else {
						astronomicalTimeShift = 0.0;
						astronomicalTimeCorrection = 0.0;
					}
					//
					break;

				case RESTARTED:
					//Removed in the limited version
					break;

				case RESUMED:
					controlState = SimProcessStatus.RUNNING;
					System.out.println(simTime()+": SimModel: Simulation rerun after pause");					
					if (simMode == SimMode.ASTRONOMICAL){
						astronomicalTimeCorrection = astronomicalTimeCorrection + (System.currentTimeMillis() - pauseStartTime); //Stop counting Pause time and sum all pauses
					}
					break;

				case RUNNING:
					nextEvent();
					break;

				case WAITINGFORSTOP:					
					commonSimContext.clearContext();
					//
					controlState = SimProcessStatus.STOPPED;					
					System.out.println(simTime()+": SimModel: Simulation stopped");													
					break;

				case STOPPED:
					//Due to condition in 'while' it could not be reached
					break;

				case PAUSED:
					pauseStartTime = System.currentTimeMillis(); //Start counting Pause time
					break mainloop;

				default:
					//Removed in the limited version
					break;
				}

			}
//			System.out.println("Simulation ended");
		} else
			//throw new SimControlException("Simulation already started");
			System.err.println("Simulation already started");
	}
	
	public void nextEvent() throws SimControlException {
		BasicSimEvent<BasicSimObj, Object> simEventToTransform = null;
		// Looking for the soonest change of state
		double tempSimTime = SimParameters.MaxSimTimeValue;
		if (commonSimContext.getSimEventCalendar().getSize() > 0) {
			if (commonSimContext.getSimEventCalendar().readFirst().getRunTime() < tempSimTime) {
				simEventToTransform = commonSimContext.getSimEventCalendar().readFirst();
				tempSimTime = simEventToTransform.getRunTime();
			}
		}
		if (simEventToTransform != null) {
			if (simMode == SimMode.ASTRONOMICAL) {
				double currentAstronomicalTime = 0.0;
				double durationRemainingToEvent = 0.0;
				
				System.out.println("@SimModel - tempSimTime: "+tempSimTime);
				currentAstronomicalTime = System.currentTimeMillis();
				durationRemainingToEvent = (tempSimTime - (currentAstronomicalTime - astronomicalTimeShift - astronomicalTimeCorrection)/1000)/simTimeRatio;
				System.out.println("@SimModel - currentAstronomicalTime - astronomicalTimeShift: "+(currentAstronomicalTime -astronomicalTimeShift));
				System.out.println("@SimModel - "+Double.toString(currentSimTime)+" - durationRemainingToEvent: " + Double.toString(durationRemainingToEvent));
				if (durationRemainingToEvent > 0.0 && durationRemainingToEvent > astronomicalTimeStep/1000) {
					try {
						Thread.sleep((long) (astronomicalTimeStep));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setCurrentSimTime (currentSimTime + astronomicalTimeStep/1000*simTimeRatio);	
					astronomicalTimeShift = astronomicalTimeShift - astronomicalTimeStep*(simTimeRatio - 1);
					System.out.println("@SimModel 1a- currentSimTime: "+currentSimTime);
					System.out.println("@SimModel 1b- zmiana astronomicalTimeShift: "+astronomicalTimeStep*(simTimeRatio - 1));
				}else{
					if (durationRemainingToEvent > 0.0){							
						try {
							Thread.sleep((long) (durationRemainingToEvent*1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						astronomicalTimeShift = astronomicalTimeShift - 1000*durationRemainingToEvent*(simTimeRatio - 1);
						System.out.println("@SimModel 2a- currentSimTime: "+currentSimTime);
						System.out.println("@SimModel 2b- zmiana astronomicalTimeShift: "+1000*durationRemainingToEvent*(simTimeRatio - 1));
					}
					simEventToTransform = commonSimContext.getSimEventCalendar().getFirst();
					setCurrentSimTime(simEventToTransform.getRunTime());
					System.out.println("@SimModel 3- currentSimTime: "+currentSimTime);
					simEventToTransform.getSimObj().transformSimEvent(simEventToTransform);
				}
			}
			else {
				simEventToTransform = commonSimContext.getSimEventCalendar().getFirst();
				setCurrentSimTime(simEventToTransform.getRunTime());
				simEventToTransform.getSimObj().transformSimEvent(simEventToTransform);
			}
			eventsProcessed++;
		} else {
			if (simMode == SimMode.ASTRONOMICAL) {
				try {
					Thread.sleep((long) (astronomicalTimeStep/simTimeRatio));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setCurrentSimTime (currentSimTime + astronomicalTimeStep/1000);
			 } else {
				//Temporary the following action in the astronomical 
				controlState = SimProcessStatus.STOPPED;
			 }
		}
	}

	public long processedEvents() {
		return eventsProcessed;
	}
	
	public void stopSimulation() {		
		controlState = SimProcessStatus.WAITINGFORSTOP;			
	}

	public void pauseSimulation() {
		controlState = SimProcessStatus.PAUSED;
		}

	public void resumeSimulation() {
		if (controlState == SimProcessStatus.PAUSED) {
//			controlState = SimProcessStatus.RESUMED;
			controlState = SimProcessStatus.IDLE;
			
			//copied from main loop
			System.out.println(simTime()+": SimModel: Simulation rerun after pause");					
			if (simMode == SimMode.ASTRONOMICAL){
				astronomicalTimeCorrection = astronomicalTimeCorrection + (System.currentTimeMillis() - pauseStartTime); //Stop counting Pause time and sum all pauses
			}
		}
	}

	public void initializeSimTime(double startTime) {//throws SimControlException {
		if (controlState == SimProcessStatus.IDLE) {
			setCurrentSimTime(startTime);
		} else
			//throw new SimControlException("Simulation already started");
			System.err.println("Simulation already started");
	}

	public void setCurrentSimTime(double currentSimTime) {
		this.currentSimTime = currentSimTime;
	}

	public double simTime() {
		return currentSimTime;
	}

	/**
	 *
	 *'0' means January and '11' = December | 
	 *Days of month are counted from '1' to ... 28,29,30 or 31 | 		
	 *'24' hours in a day
	 */
	public int simDate(SimParameters.SimDateField field) {
		return simCalendar.getSimDate(field, currentSimTime, simTimeScale); 	
	}
	
	public double getEndSimTime() {
		return endSimTime;
	}

	public void setEndSimTime(double endSimTime) {//throws SimControlException {
		if (controlState == SimProcessStatus.IDLE) {
			this.endSimTime = endSimTime;
			// create new end event with endSimTime
		} else
			//throw new SimControlException("Simulation already started");
			System.err.println("Simulation already started");
	}
	public double getSimTimeStep() {
		return astronomicalTimeStep;
	}

	public void setSimTimeStep(double simTimeStep) {
		this.astronomicalTimeStep = simTimeStep;
	}

	public double getSimTimeScale() {
		return simTimeScale;
	}

	public void setSimTimeScale(double simTimeScale) {
		this.simTimeScale = simTimeScale;
	}

	public double getSimTimeRatio() {
		return simTimeRatio;
	}

	public void setSimTimeRatio(double simTimeRatio) {
		if (simTimeRatio > 0.0)
			this.simTimeRatio = simTimeRatio;
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

}
