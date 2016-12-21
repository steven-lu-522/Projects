
public class PatternPrediction implements Predictor{
    public class Number {
      //Val is the number, beforeCount is the count of the values that came before
      //this number, beforeAfterCount is the count of each number that came
      //after this value, given each number that came before, afterCount is the
      //aggregate total count of numbers that came after this value.
        private int[] beforeCount;
        private int[][] beforeAfterCount;
        private int[] afterCount;
        public Number() {
            beforeCount = new int[10];
            beforeAfterCount = new int[10][10];
            afterCount = new int[10];
        }
        public void incrementBeforeAfterCount(int precedingNumber, int after) {
            beforeCount[precedingNumber] += 1;
            beforeAfterCount[precedingNumber][after] += 1;
            afterCount[after] += 1;
        }
        public void incrementAfterCount(int num) {
            afterCount[num] += 1;
        }
        public Prediction predictNext(int precedingNumber) {
        //Predicts what number will come after this number
            if (beforeCount[precedingNumber] >= 2) {
                //If the preceding number has come before this number more than
                //once, this may be indicative of a pattern in this sequence of
                //numbers.
                int retval = 0;
                int highestCount = 0;
                for (int i = 0; i < 10; i++) {
                    if (beforeAfterCount[precedingNumber][i] > highestCount) {
                      highestCount = beforeAfterCount[precedingNumber][i];
                      retval = i;
                    }
                }
                return new Prediction(retval,
                              ((double) highestCount) / beforeCount[precedingNumber]);
            }
            else {
                //In the case that the preceding number has been before this number
                //Less than 2 times, predicting based off of the preceding number is
                //unreliable.
                int totalCount = 0;
                int retval = 0;
                int highestCount = 0;
                for (int i = 0; i < 10; i++) {
                    if (afterCount[i] > highestCount) {
                        highestCount = afterCount[i];
                        retval = i;
                    }
                    totalCount += afterCount[i];
                }
                if (totalCount == 0) {
                    //if this number has never appeared before, then there is no
                    //way of predicting a pattern. Return a random number with
                    //a low likelihood.
                    return new Prediction((int) (Math.random() * 10), 0.1);
                }
                else {
                  return new Prediction(retval, ((double)highestCount ) / totalCount);
                }
            }
        }
    }
    private Number[] numbers;
    private int[] data;
    private int index, predictions, numCorrect;
    public PatternPrediction() {
        numbers = new Number[10];
        data = new int[10];
        index = 0;
        predictions = 0;
        numCorrect = 0;
        for (int i = 0; i < 10; i++) {
            numbers[i] = new Number();
        }
    }
    public void correctPrediction() {
        predictions++;
        numCorrect++;
    }
    public void wrongPrediction() {
        predictions++;
    }
    public double getAccuracy() {
        return ((double) numCorrect) / (double) predictions;
    }
    public Prediction predictNext() {
        int num1 = 0;
        int num2 = 0;
        if (index > 0) {
            num1 = data[index - 1];
        }
        if (index > 1) {
            num2 = data[index - 2];
        }
        return numbers[num1].predictNext(num2);
    }
    public void addNumber(int number) {
        data[index] = number;
        if (index == data.length - 1) {
            int[] newArr = new int[data.length * 2];
            System.arraycopy(data, 0, newArr, 0, data.length);
            data = newArr;
        }
        index++;
        if (index == 2) {
            numbers[data[0]].incrementAfterCount(number);
        } else if (index > 2) {
            numbers[data[index - 2]].incrementBeforeAfterCount(data[index - 3], number);
        }

    }
}
