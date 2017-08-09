#!/bin/bash

min=0
max=49

cd ../experiment/MyExperiments/data/$1/$2/

## Example: Files in dir -> new names
## FUN.0   VAR.0   -> FUN0.tsv VAR0.tsv
## FUN.1   VAR.1   -> FUN1.tsv VAR1.tsv
## ...
## FUN.49   VAR.49   -> FUN49.tsv VAR49.tsv

for i in $(seq $min $max); do 

	for file in *.$i; do

		NAME=`echo "$file" | cut -d'.' -f1`
		EXTENSION=`echo "$file" | cut -d'.' -f2`
		#echo $NAME "  and   " $EXTENSION
		mv "$file" "$NAME$EXTENSION.tsv"
	done

done
