#!/bin/sh
#Created by: Lucas Prestes - lucas.prestes.lp@gmail.com
#	All Rscript result and PDFs result will be created on:
# 	"./experiment/MyExperiments/Result/" (Default path)
#	$1 is the argument to change default path
# For use in JMEtal 5.2

newPath=$1

shift #remove first arg, is the path

java -jar JMetal.jar --comparative $*

cd "./experiment/";
path="Result/"

if [ -z $newPath ]; then
	echo "Default Path! ./experiment/MyExperiments/"$path
else
	path=$newPath
	echo "Change Path to: ./experiment/MyExperiments/"$path
fi

#################################
#USING LATEX AND DVI
#TO GENERATE .DVI
echo "Executing tex and dvi..."
cd "./MyExperiments/latex/";
for i in *.tex ; do
	latex $i 2>/dev/null;
done

#TO GENERATE .PDF
for i in *.dvi ; do
	dvipdf $i 2>/dev/null;
done

#CREATE RESULT DIR on "./MyExperiments/Result/
mkdir "../"$path;

#TO MOVEMENT FILES
for i in *.pdf ; do
	mv $i "../"$path;
done


# REMOVE *.log | *.aux | *.dvi 
# Remove ONLY auxiliars files
echo "Removing auxiliar files...";

for i in *.log; do rm $i; done
for i in *.aux; do rm $i; done
for i in *.dvi; do rm $i; done
#for i in *.pdf; do rm $i; done

#################################
#USING R
#TO GENERATE .EPS
echo "Moving files..."
cd "../R/";

for i in *.R ; do
	Rscript $i 2>/dev/null;
done

for i in *.tex ; do
	latex $i 2>/dev/null;
done

for i in *.dvi ; do
	dvipdf $i 2>/dev/null;
done

for i in *.log; do rm $i; done
for i in *.aux; do rm $i; done
for i in *.dvi; do rm $i; done

#MOVE .EPS to Result
for i in *.eps ; do
	mv $i "../"$path;
done

for i in *.pdf ; do
	mv $i "../"$path;
done

#DELETE .EPS
#for i in *.eps; do rm $i; done
#################################
cd "../";
mv Execute_log_algorithm.txt "./"$path;
mv Execute_log.txt "./"$path;
mv "./latex" "./"$path;
mv "./R" "./"$path;
Rscript "./EvolutionProcess/main.R"
mv "./EvolutionProcess" "./"$path;

#clear
echo "


Done!";
#################################


