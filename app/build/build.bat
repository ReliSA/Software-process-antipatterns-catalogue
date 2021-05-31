cd ..
cd project
mvn clean compile assembly:single && xcopy /Q /Y "target\AntipatternCatalogue.jar" "..\AntipatternCatalogue.jar"* && cd .. && cd build