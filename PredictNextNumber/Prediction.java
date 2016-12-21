/**
 * Created by Steven Lu on 10/11/2016.
 */
public class Prediction {
    //This is essentially a collection of data values that will be returned by
    //each pattern predicting strategy. Essentially, whatNumber is what the
    //next number will most likely be, and likelihood is the percentage chance
    //that the prediction is true.
    private int prediction;
    private double likelihood;
    public Prediction(int whatNumber, double howLikely) {
        prediction = whatNumber;
        likelihood = howLikely;
    }
    public int getPrediction() {
        return prediction;
    }
}