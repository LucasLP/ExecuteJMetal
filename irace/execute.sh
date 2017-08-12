#!/bin/sh

#How to execute IRace
# 1 - Set paramenters.txt
# 2 - Set instances (tune-conf)
# 3 - Code hook-run to print only an float value to be maximized
# 4 - Execute THIS sh "sh execute.sh name" the parameter name is for output data
# 5 - Results at output directory "Tests"


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

mv irace.Rdata Irace/Tests/

cd ./Irace/Tests/

# Gera o txt dos resultados na pasta /Irace/Tests/
R -f Results.R > "./"$name"_output_Rdata.txt"

#rename Rdata
mv irace.Rdata $name"_irace.Rdata"


#move output files
cd ../
cp -R Output/ Tests/$name"_Output"/





