cd ..
cd project
mvn clean compile assembly:single && xcopy "target\AntipatternCatalogue-1.0-jar-with-dependencies.jar" "..\AntipatternCatalogue.jar" /Y /I && cd .. && cd build