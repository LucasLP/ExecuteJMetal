#!/bin/sh

#Como executar o Irace
# 1 - Configure paramenters.txt
# 2 - configure as instâncias de teste (tune-conf) (o da instância vira nos argumentos de entrada)
# 3 - Codifique target-runner para impirimir somente o valor que deve ser minimizado (caso seja um maximizador deve ter um sinal negativo)
# 4 - Execute este arquivo "sh execute.sh nome" o paramentro "nome" é para os dados de saída.
# 5 - Os resultados estarão na pasta "Tests"


# OBS:
#	1. Caso seja impresso log de erro na saída do algoritmo isto irá gerar erro.
#	2. Certifique que a ultima coisa que seu algoritmo irá imprimir será o indicador de qualidade (fitness).
#	3. Lembre-se o Irace é um minimizador então, se seu algoritmo/problema for um maximizador então multiplique a saída final por -1 (isto pode ser feito por meio do arquivo target-runner).


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





