mvn clean package

java -Dport=$PORT -DsecondsPerWeek=$EQUIVALENCE_OF_ONE_WEEK_IN_SECONDS -jar target/jersey-jetty.jar