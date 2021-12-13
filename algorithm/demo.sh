#!/bin/bash

KEY=""
URL=""

curl "$URL" -H "X-API-Key: $KEY" -o compressed.bin
python3 main.py -d compressed.bin >> numbers.txt
echo
cat numbers.txt
