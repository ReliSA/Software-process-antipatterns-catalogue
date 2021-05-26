cd ..
cd project
mvn clean compile assembly:single && xcopy "target\AntipatternCatalogue.jar" "..\AntipatternCatalogue.jar" /Y /I && cd .. && cd build