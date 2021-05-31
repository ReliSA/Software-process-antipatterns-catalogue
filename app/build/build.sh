#!/bin/bash
cd ..
cd project
mvn clean compile assembly:single
cp -rf "target/AntipatternCatalogue.jar" "../AntipatternCatalogue.jar"
cd ..
cd build
