#!/bin/bash


#    Benchmarks
#Name		nº instances
#UF			10
#ZDT			5
#WFG			9
#DTLZ			6

#	Indicators
#HV		max
#ep		min
#IGD		min

#	Agorithms
#MOEAD
#MOEADDRA
#NSGAII
#SPEA2

benchmark="WFG"

testName="_UCBNewVersionDRA_"

algorithm1="MOEADDRAUCB"
tag1="MOEADDRAUCBnew"
algorithm2="MOEADDRA"



	#Execution Line - Execute the algorithm and save the results in algorithm tag 
#java -jar JMetal.jar --statistic /$benchmark --algorithm $algorithm1 --tag $tag1


	#Comparative Line	- Make the comparative tables and organize the folders
sh QualityIndicators.sh Result$testName$benchmark/ $benchmark --algorithm $algorithm1 --tag $tag1 --algorithm $algorithm2 







echo "
  _____                           _ 
 |  __ \                         | |
 | |  | |   ___    _ __     ___  | |
 | |  | |  / _ \  | '_ \   / _ \ | |
 | |__| | | (_) | | | | | |  __/ |_|
 |_____/   \___/  |_| |_|  \___| (_)"
