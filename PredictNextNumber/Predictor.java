/**
 * Created by Steven Lu on 10/11/2016.
 */
public interface Predictor {
    Prediction predictNext();
    void correctPrediction();
    void wrongPrediction();
    double getAccuracy();
    void addNumber(int n);
}
