#!/bin/bash

# sample code
# if [ $1 == "-m" ]
# then
#     echo "msg here"
# fi

if [ $1 == "-c" ]
then
    rm -r westerncapelabs_work/*.pyc
    echo "cleaned directory"
fi

if [ $1 == "-r" ]
then
    echo "runapp"
    python runapp.py
fi

if [ $1 == "-s" ]
then
    find . -type d -name .svn -exec rm -rf {} +
fi

