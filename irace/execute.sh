#!/bin/sh

#How to execute IRace
# 1 - Set paramenters.txt
# 2 - Set instances (tune-conf)
# 3 - Code target-runner to print only an float value to be minimized
# 4 - Execute THIS sh "sh execute.sh name" the parameter name is for output data
# 5 - Results at output directory "Tests"


# OBS:
#	1. The JMetalLogger don't can print anything, else it will generate an error.
#	2. certify if the last thing of you program will show is the quality indicator.
#	3. Remember, Irace is a minimizer, then, if you use a max, you need multiply by -1.


name="Default"
if [ -z $1 ]; then
	echo "Default File Name! ./Tests/"$name
else
	name=$1
	echo "Change Path to: ./Tests/"$name
fi


#Data e hora do inicio da execução
start=`date +%d-%m-%y_%H:%M:%S`

# Executa um script R a partir de um arquivo (-f) enviado
R --vanilla --slave -e "library(irace)" -e "irace.cmdline()" #--args %* 

#Data e hora do termino da execução
end=`date +%d-%m-%y_%H:%M:%S`



echo "

===============================
Start:" $start "End" $end "
==============================="


cd ../

mv irace.Rdata irace/Tests/

cd ./irace/Tests/

# Gera o txt dos resultados na pasta /Irace/Tests/
R -f Results.R > "./"$name"_output_Rdata.txt"


#rename Rdata
mv irace.Rdata $name"_irace.Rdata"


#move output files
cd ../
cp -R Output/ Tests/$name"_Output"/





