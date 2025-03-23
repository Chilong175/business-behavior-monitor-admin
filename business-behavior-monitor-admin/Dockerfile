FROM openjdk:8-jre-slim
MAINTAINER Chilong
ENV PARAMS=""
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD target/business-behavior-monitor-app.jar /business-behavior-monitor-app.jar
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /business-behavior-monitor-app.jar $PARAMS"]
