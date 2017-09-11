import random
import prediction as p
import math
import csv

#Predicts based off of lagrange interpolation "experts" with varying numbers
#of data points using multiplicative weights.

#Initialize
symbol = input("Please enter the symbol of a stock.")
current_symbol = open("current_symbol.txt", "r")
prices = []
#Predict Intraday stock movement
weights = []
# if symbol == current_symbol.readline():
#     with open("weights.csv", "r") as csvfile:
#         filereader = csv.reader(csvfile)
#         for row in filereader:
#             weights.append(float(row[0]))
# else:
for n in range(1,9):
    weights.append(1)
probSum = 0
for weight in weights:
    probSum += weight
predictions = []
canContinue = True
current_symbol.close()

#Predits once
def predict(offset, verbose=False):
    global canContinue
    #Choose an expert
    num = random.random() * probSum
    expert = 0
    while num > weights[expert]:
        num -= weights[expert]
        expert += 1

    #Make each expert predict
    for ei in range(0, len(weights)):
        res = p.predict(10 + ei + 1, offset + (8 - ei)) #ei is the expert number
        predictions.append(res[0])
        if ei == len(weights) - 1:
            canContinue = res[1] #Whether you can continue predicting (whether or not the end of the prices array has been reached)
    if verbose:
        #For debugging
        print(predictions)
        print(weights)
        print(expert)
        print(probSum)
    #Return the expert's precition
    return predictions[expert]

#Updates the wieghts of the experts based off of whether they were
#right or wrong.
def update(correct):
    global probSum, predictions
    for i in range(0,len(weights)):
        if predictions[i] != correct:
            weights[i] /= 2
            probSum -= weights[i]
    predictions[:] = []


#Run predictions for each expert while possible
i = 0
accuracy = 1.0
numPredictions = 0
netGain = 0
correct = False
p.importData(symbol, True)
print("HISTORICAL PREDICTIONS")
print("PREDICTIONS / ACTUAL")
while canContinue:
    print("--------------------------")
    result = predict(i, True)
    print("Day " + str(i + 1) + ": " + result)
    # if p.getPrice(18 + i) - p.getPrice(17 + i) < 0: #If using opening_prices or closing_prices
    if p.getPrice(18 + i) < 0: #If using differences
        update("N")
        correct = (result == "N")
        print("Actual: N")
    else:
        update("P")
        correct = (result == "P")
        print("Actual: P")
    if correct:
        accuracy = ((accuracy * numPredictions) + 1) / (numPredictions + 1)
        # netGain += math.fabs(p.getPrice(18 + i) - p.getPrice(17 + i)) #If using opening_prices or closing_prices
        netGain += math.fabs(p.getPrice(18 + i)) #If using differences
    else:
        accuracy = (accuracy * numPredictions) / (numPredictions + 1)
        # netGain -= math.fabs(p.getPrice(18 + i) - p.getPrice(17 + i)) #If using opening_prices or closing_prices
        netGain -= math.fabs(p.getPrice(18 + i)) #If using differences
    print("Current Profit: " + str(int(netGain * 100) / 100))
    print("Current Price: " + str(p.getPrice(18 + i)))
    numPredictions += 1
    i += 1
print("ACCURACY: " + str(int(accuracy * 10000)/100) + "%")
print(".........")
print(".........")
print(".........")
print("FUTURE PREDICTION")
print(".........")
print(".........")
print(".........")
futurePredict = predict(i, True)
if futurePredict == "N":
    print("This stock is predicted to go down tomorrow.")
else:
    print("This stop is predicted to go up tomorrow.")


# with open('weights.csv', "w", newline='') as csvfile:
#     filewriter = csv.writer(csvfile)
#     for weight in weights:
#         filewriter.writerow([weight])
