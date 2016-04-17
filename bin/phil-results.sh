#!/bin/bash

for i in {1..10}
do
	java PhilTest 5 >> fair5.txt
	printf "\n" >> fair5.txt
done
for i in {1..10}
do
	java PhilTest 25 >> fair25.txt
	printf "\n" >> fair25.txt
done
for i in {1..10}
do
	java PhilTest 100 >> fair100.txt
	printf "\n" >> fair100.txt
done
 
