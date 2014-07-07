#!/bin/bash

# go inside BS folder
cd _bootstrap

# compile with grunt
grunt dist

# back to root
cd ..

# copy CSS files
cp _bootstrap/dist/css/*.min.css css
