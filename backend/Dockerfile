FROM openjdk:17-jdk
MAINTAINER paccounts.org
#COPY target/paccounts-backend-executable.jar paccounts-backend.jar
COPY target/paccounts-backend-jar-with-dependencies.jar paccounts-backend.jar
ENTRYPOINT ["java","-jar","/paccounts-backend.jar"]
