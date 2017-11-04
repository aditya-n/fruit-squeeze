#!/usr/bin/env python

import os
import sys
os.system("rm 1.ls 2.ls 1.time 2.time score.txt temp.txt")
li = {"java":"java ","cpp":"./"}
player1 = 0
player2 = 0
cmd1 = sys.argv[1]
cmd2 = sys.argv[2]
cmd3 = sys.argv[3]
cmd4 = sys.argv[4]
cmd1 = li[cmd1]+cmd2
cmd2 = li[cmd3]+cmd4

print (cmd1)
print (cmd2)

def gameON():
    input = open("input.txt","r")
    n = int((input.readline()).rstrip())
    p = int((input.readline()).rstrip())
    time = float((input.readline()).rstrip())
    nur = [[0]*n]*n
    for i in range(n):
        nur[i] = (input.readline()).rstrip()
    input.close()

    stillPlaying = False
    nursery = [[0] * n for i in range(n)]
    for i in range(n):
        for j in range(n):
            if nur[i][j] != '*':
                stillPlaying = True
                break;
    return stillPlaying

def outputToInput():
    input = open("input.txt","r")
    temp = open("temp.txt","w")
    n = int((input.readline()).rstrip())
    p = int((input.readline()).rstrip())
    time = float((input.readline()).rstrip())
    temp.write(str(n)+"\n"+str(p)+"\n"+str(time)+"\n")
    input.close()

    output = open("output.txt","r")
    location = str((output.readline()).rstrip())
    for i in range(n):
        board = output.readline()
        temp.write(board)
    temp.close()
    output.close()
    os.system("cp temp.txt input.txt")
    
    
while True:
    os.system("{ time -p "+cmd1+"; } 2> 1.ls  | grep silent")
    os.system("cat 1.ls | grep user >> 1.time")
    outputToInput()

    score = open("score.txt","r")
    scoreread= int((score.readline()).rstrip())
    player1 += scoreread*scoreread
    score.close()

    if gameON() == False:
        break;

    os.system("{ time -p "+cmd2+"; } 2> 2.ls  | grep silent")
    os.system("cat 2.ls | grep user >> 2.time")
    outputToInput()

    score = open("score.txt","r")
    scoreread = int((score.readline()).rstrip())
    player2 += scoreread*scoreread
    score.close()

    if gameON() == False:
        break;

print("player 1: ",cmd1)
print("score: ", player1)
print("time1: ")
os.system("cat 1.time | cut -d ' ' -f2 | paste -s -d+ - | bc ")
print("player 2: ",cmd2)
print("score: ", player2)
print("time2: ")
os.system("cat 2.time | cut -d ' ' -f2 | paste -s -d+ - | bc ")