import datetime
import requests
import calculations as calc
import csv

prices = []
def importData(symbol, override=False):
    global prices
    #last_updated.txt is a file holding the date that data.csv was last updated.
    lastUpdate = open("last_updated.txt", "r")

    #current_symbol.txt is a file holding the symbol of the stock whose data is in data.csv
    current_symbol = open("current_symbol.txt", "r")

    #Get the current da y and convert to readable format.
    now = datetime.datetime.now()
    yyyy = str(now.year)
    mm = str(now.month)
    dd = str(now.day)
    if (now.month < 10):
        mm = '0' + mm
    if (now.day < 10):
        dd = '0' + dd
    if now.hour > 18 and (lastUpdate.readline() != yyyy + "-" +  mm + "-" + dd or current_symbol.readline() != symbol)or override:
        #Only update data if stock market has already closed and data has not been updated since thenself.
    #if True:
        #If the contents of the file are not the same as the current date, then update data.csv with
        #newer data.

        #Figure out if the symbol is in the NASDAQ or in the NYSE
        symbol = symbol.upper()
        x = ""
        with open ('nasdaq.txt', 'r') as NASDAQ:
            NASDAQ = NASDAQ.read().split("\n")[1:]
            NASDAQ = NASDAQ[0:len(NASDAQ) - 2]
            NASDAQ = {company.split("|")[0]: True for company in NASDAQ}

        if symbol in NASDAQ.keys():
            x = "NASDAQ"
        else:
            x = "NYSE"

        #Get the stock's historical data from Google Finance.
        #q = the Stock's symbol, x = the stock exchange the stock is in
        #i = the time period between each price update in seconds, p = time period of the data
        #f = the format the data is coming back in (date, close, high, low, open, volume)
        link = "https://www.google.com/finance/getprices?q=" + symbol + "&x=" + x + "&i=3600&p=2M&f=d,c,h,l,o,v"
        #Separate the stock data from the information about the data, and separate the data into
        #Arrays organized by update time.
        f = requests.get(link).text.split("\n")[7:]
        #Separate each data value into separate indices in each array.
        f = [k.split(",") for k in f]
        f[0][0] = '0'
        #Separate the data by day.
        f = [[f[(i * 7) + j] for j in range(0,7)] for i in range(0, int(len(f) / 7))]
        # Format each entry as [Open, Close] for any given day
        f = [[row[0][4], row[6][1]]  for row in f]
        #Write the data into data.csv. newline='' prevents double spaced data entry.
        with open('data.csv', 'w', newline='') as csvfile:
            filewriter = csv.writer(csvfile)
            for row in f:
                filewriter.writerow(row)
        #Update last_updated.txt with the current date.
        lastUpdate.close()
        with open("last_updated.txt", "w") as lastUpdate:
            lastUpdate.write(yyyy + "-" +  mm + "-" + dd)

        current_symbol.close()
        with open("current_symbol.txt", "w") as current_symbol:
            current_symbol.write(symbol)

    #Get the data needed for lagrange from data.csv
    opening_prices = []
    closing_prices = []
    differences = []
    with open('data.csv', 'r') as csvfile:
        filereader = csv.reader(csvfile, delimiter=',')
        for row in filereader:
            #row[0] is stock price at open, and row[1] is price at close.
            opening_prices.append(float(row[0]))
            closing_prices.append(float(row[1]))
            differences.append(float(row[1]) - float(row[0]))
    prices =  differences

# importData("AAPL") #Import stock data for AAPL by default

def predict(numPoints, offset=0):
#Predict based off of using Lagrange on the EMA
#Increasing offset by 1 shifts the start and end day later by 1
    end = not (offset + numPoints == len(prices))
    if numPoints > 10 and numPoints < 19:
        return (calc.predictEMA(prices[offset:offset + numPoints]), end)
    else:
        #Use 18 points as default
        return (calc.predictEMA(prices[offset:offset + 18]), end)

def predictNext(numPoints):
    return calc.predictEMA(prices[len(prices) - numPoints:])

def getPrice(index):
    return prices[index]
def getPrices():
    return prices
# i = 0
# while not predict(18, i)[1]:
#     print(predict(18, i)[0])
#     i += 1
# print(predict(18, i)[0])
