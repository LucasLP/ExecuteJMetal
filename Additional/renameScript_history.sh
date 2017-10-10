#!/bin/sh



for file in *.dat; do

	NAME=`echo "$file" `
	echo $NAME

	args=$(echo $NAME | tr "_" "\n")

	i=0

	NEWNAME=""
	for arg in $args; do
		if [ $i -ne 2 ]; then #not equal
		   echo "> [$arg]"
			if [ $i -ne 3 ]; then #not equal
				NEWNAME=$NEWNAME$arg"_"
			else
				NEWNAME=$NEWNAME$arg
			fi
		fi
		i=$((i+1))
	done
	echo "> [$NEWNAME]"

	#| cut -d'.' -f1
	#EXTENSION=`echo "$file" | cut -d'.' -f2`
	#echo $NAME "  and   " $EXTENSION

	mv "$file" "$NEWNAME"
done

