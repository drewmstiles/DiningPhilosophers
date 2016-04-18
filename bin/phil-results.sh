#!/bin/bash

for i in {1..10}
do
	java PhilTest 5 >> unfair5.txt
	printf "\n" >> unfair5.txt
done
for i in {1..10}
do
	java PhilTest 25 >> unfair25.txt
	printf "\n" >> unfair25.txt
done
for i in {1..10}
do
	java PhilTest 100 >> unfair100.txt
	printf "\n" >> unfair100.txt
done
 
