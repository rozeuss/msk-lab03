package dissimlab.monitors;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: class used four counting basic statistics
 *
 * @author Dariusz Pierzchala
 */
public class RStatistics {

    private static final int MAX_COLUMNS_OF_STUDENTS_DISTRIBUTION = 120;
    private static final int MAX_ROWS_OF_STUDENTS_DISTRIBUTION = 14;
    private static final int MAX_COLUMNS_OF_CHI_SQUARE_DISTRIBUTION = 50;
    private static final int MAX_ROWS_OF_CHI_SQUARE_DISTRIBUTION = 15;
    private static final double[] przedzialyKwantyla = {1.0, 0.9, 0.8, 0.7,
            0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.001};
    private static final double[] przedzialyWariancjiPrawy = {1.0, 0.99, 0.98,
            0.95, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.05, 0.02,
            0.01, 0.001};
    private static final double[] przedzialyWariancjiLewy = {1.0, 0.98, 0.95,
            0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.05, 0.02, 0.01,
            0.001};

    private static RStatistics rStatistics = new RStatistics();
    private static RConnection connection;

    private RStatistics() {
        try {
            connection = new RConnection();
        } catch (RserveException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        connection.close();
    }

    public static RStatistics getInstance() {
        if (rStatistics == null) {
            rStatistics = new RStatistics();
        }
        return rStatistics;
    }

    /*
         * obliczanie srendiej arytmetycznej na podstawie danej zmiennej
         * monitorowanej
         */
    public double arithmeticMean(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            connection.eval("meanVal=mean(myData)");
            result = connection.eval("meanVal").asDouble();
//            System.out.println("The mean of given vector is=" + result);
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }


    /*
     * srednia harmoniczna
     */
    public double harmonicMean(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("harmonicMeanVal=1/mean(1/myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }


    /*
     * rozstep
     */
    public double range(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("rangeVal=range(myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
//        TODO
//        Histogram histogram = monitoredVar.getHistogram();
//        return histogram.getMaxValue() - histogram.getMinValue();
    }

    /*
     * wariancja
     */
    public double variance(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("variance=var(myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * odchylenie standardowe
     */
    public double standardDeviation(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("standardDeviation=sd(myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double min(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("minVal=min(myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }

    public double max(MonitoredVar monitoredVar) {
        List<Double> values = getChangesValues(monitoredVar);
        double result = 0;
        try {
            connection.assign("myData", getDoublesArray(values));
            REXP x = connection.eval("maxVal=max(myData)");
            result = x.asDouble();
        } catch (REXPMismatchException | REngineException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * liczba prob
     */
    public static int numberOfSamples(MonitoredVar monitoredVar) {
        return monitoredVar.getChanges().size();
    }

    /*
     * estymacja przedzialowa wartosci oczekiwanej E{X} = 0gdy nie znamy
     * odchylenia standardowego, wykorzystujemy tutaj rozklad Studenta
     */
    public double[] intervalEstimationOfEX(MonitoredVar monitoredVar,
                                                  double gamma) {
        double quantile = (1 + gamma) / 2;
        int numberOfSamples = monitoredVar.numberOfSamples();
        int column;
        if (numberOfSamples > MAX_COLUMNS_OF_STUDENTS_DISTRIBUTION)
            column = MAX_COLUMNS_OF_STUDENTS_DISTRIBUTION;
        else
            column = numberOfSamples - 1;
        int row = getRightRowForQuantile(quantile, przedzialyKwantyla);

        double tFromStudentsDistribution = Distribution.getStudentsDistribution()[column][row];
        double interval = getInterval(monitoredVar, tFromStudentsDistribution);
        double arithmeticMean = arithmeticMean(monitoredVar);

        double[] result = new double[2];
        result[0] = arithmeticMean - interval;
        result[1] = arithmeticMean + interval;
        return result;
    }

    private static int getRightRowForQuantile(double quantile,
                                              double[] przedzialy) {
        int row;
        for (row = 0; row < przedzialy.length; row++) {
            if (quantile >= przedzialy[row + 1] && quantile < przedzialy[row])
                break;
        }
        return row;
    }

    private static int getLeftRowForQuantile(double quantile,
                                             double[] przedzialy) {
        int row;
        for (row = 0; row < przedzialy.length; row++) {
            if (quantile > przedzialy[row + 1] && quantile <= przedzialy[row])
                break;
        }
        return row;
    }

    private double getInterval(MonitoredVar var,
                                      double tFromStudentsDistribution) {
        int samples = var.numberOfSamples();
        return (standardDeviation(var) * tFromStudentsDistribution)
                / Math.sqrt(samples);
    }

    /*
     * estymacja przedziałowa wariancji lewa i prawa granica przedzialu
     */
    public  double[] intervalEstimationOfVariance(
            MonitoredVar monitoredVar, double gamma) {
        int column, rowRight, rowLeft;
        double[] result = new double[2];
        int numberOfSamples = monitoredVar.numberOfSamples();
        double right = (1 + gamma) / 2;
        double left = (1 - gamma) / 2;

        if (numberOfSamples > MAX_COLUMNS_OF_CHI_SQUARE_DISTRIBUTION)
            column = MAX_COLUMNS_OF_CHI_SQUARE_DISTRIBUTION - 1;
        else
            column = numberOfSamples - 1;

        double variance = variance(monitoredVar);
        rowRight = getRightRowForQuantile(right, przedzialyWariancjiPrawy);
        rowLeft = getLeftRowForQuantile(left, przedzialyWariancjiLewy);
        result[0] = calc(variance, numberOfSamples, column, rowRight);
        result[1] = calc(variance, numberOfSamples, column, rowLeft);
        return result;
    }

    private static double calc(double variance, int numberOfSamples,
                               int column, int row) {
        double chiRight = Distribution.getChiSquareDistribution()[column][row];
        return variance * (numberOfSamples - 1) / chiRight;

    }

    // dmin, max , mean z przedzialu czasowego
    public static double meanFromTimeRange(MonitoredVar monitoredVar,
                                           double time1, double time2) {
        return monitoredVar.getChanges().getMeanFromTimeRange(time1, time2);
    }

    public static double maxFromTimeRange(MonitoredVar monitoredVar,
                                          double time1, double time2) {
        return monitoredVar.getChanges().getMaxFromTimeRange(time1, time2);
    }

    public static double minFromTimeRange(MonitoredVar monitoredVar,
                                          double time1, double time2) {
        return monitoredVar.getChanges().getMinFromTimeRange(time1, time2);
    }

    // liczba wystapien wartosci pomiaru z zadanego przedzialu
    public static int numberOfAppearanceFromRange(MonitoredVar monitoredVar, double begin, double end) {
        return monitoredVar.getHistogram().getNumberFromRange(begin, end);
    }


    private static List<Double> getChangesValues(MonitoredVar monitoredVar) {
        return monitoredVar.getChanges().getChangesList()
                .stream().map(change -> (change.getValue())).collect(Collectors.toList());
    }

    private static double[] getDoublesArray(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }

}
