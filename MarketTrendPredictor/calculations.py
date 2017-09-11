    #import pyspark
#from pyspark import SparkContext
#import cv2
import numpy as np

def predictEMA(A):
    #Predicts the direction of movement of exponential moving average of a stock
    prev = 0
    indices = []
    prices = []
    for i in range(0, 10):
        prev += A[i]
    multiplier = (2 / (len(A) + 1))
    prev = prev / 10
    prices.append(prev)
    indices.append(1)
    for i in range(10, len(A)):
        prev = (A[i] - prev) * multiplier + prev
        prices.append(prev)
        indices.append(i - 8)
    #print(prices)
    currentPrice = A[len(A) - 1]
    # prices = A
    # indices = [1,2,3,4,5]
    polynomial = lagrange(indices, prices)
    base = len(indices)
    counter = 0
    # for i in indices:
    #     x = i
    #     res = 0
    #     #print(x)
    #     for j in range(0, len(polynomial)):
    #         res += (x ** j) * polynomial[j]
    #     print(res)
    #     print('-------------------------------')
    for i in range(0,5):
        x = base + i
        res = 0
        #print(x)
        for j in range(1, len(polynomial)):
            #print((x ** j) * polynomial[j])
            res += (x ** j) * polynomial[j]
        #print('--------------------------------------')
        if res > 0:
            counter = counter + 1
        else:
            counter = counter - 1
    if counter > 0:
        return "P"
    else:
        return "N"

def lagrange(indices, prices):
    #Uses Lagrange Interpolation to come up with a polynomial representation of inputted points
    index = 0
    denominator = 1
    result = []
    for num in indices:
        denominator = denominator * num * -1
    denominator /= len(indices) * -1
    counter = 1
    for price in prices:
        #price = y_i
        #calculate L = sum(y_i * l_i) where l_i = (x - x_0) / (x_i - x_0) * ...
        indexes = indices[0:index]
        indexes.extend(indices[index + 1:])
        numerator = polynomialFind(indexes)
        for i in range(0, len(numerator)):
            numerator[i] = (numerator[i]  * price) / denominator
        if counter < len(indices):
            denominator = (denominator * (counter)) / ((len(indices) - counter) * -1)
        if len(result) == 0:
            result = numerator
        else:
            for i in range(0, len(result)):
                result[i] = result[i] + numerator[i]
        index = index + 1
        counter = counter + 1
    return result


def polynomialFind(A):
    #Given a list of roots, returns the coefficients of a polynomial with these roots
    if len(A) == 0:
        return []
    if len(A) == 1:
        return [-1 * A[0], 1]
    polynomial1 = polynomialFind(A[0:int(len(A) / 2)])
    polynomial2 = polynomialFind(A[int((len(A)/ 2)):])
    coefficients = fastPolynomialMultiply(polynomial1, polynomial2)
    return coefficients

def fastPolynomialMultiply(polynomial1, polynomial2):
    #Uses FFT to multiply two polynomials in O(nlog(n)) time
    if len(polynomial1) == 0:
        return polynomial2
    elif len(polynomial2) == 0:
        return polynomial1
    n = max(len(polynomial1), len(polynomial2))
    points1 = np.fft.fft(polynomial1, n * 2)
    points2 = np.fft.fft(polynomial2, n * 2)
    #Use Spark to multiply each point in points1 with each point in points2
    points = []
    for i in range(0, n * 2):
        points.append(points1[i] * points2[i])
    coefficients = np.fft.ifft(points)
    coefficients = np.trim_zeros(np.real(coefficients))
    return coefficients
