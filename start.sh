mvn clean package

java -Dport=$PORT -DsecondsPerWeek=$EQUIVALENCE_OF_ONE_WEEK_IN_SECONDS -DvehicleProductionLineMode=$VEHICULE_PRODUCTION_LINE_MODE -jar target/jersey-jetty.jar
