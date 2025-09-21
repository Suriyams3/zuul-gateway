FROM openjdk:11-jdk-slim

# Install Jetty
ENV JETTY_HOME /opt/jetty
RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl -O https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.53.v20231009/jetty-distribution-9.4.53.v20231009.zip && \
    unzip jetty-distribution-9.4.53.v20231009.zip -d /opt && \
    mv /opt/jetty-distribution-* $JETTY_HOME

# Copy app and build
WORKDIR /app
COPY . /app
RUN ./gradlew build

# Deploy your WAR or class files to Jetty
COPY build/libs/zuul-gateway.war $JETTY_HOME/webapps/

EXPOSE 8080
CMD ["sh", "-c", "$JETTY_HOME/bin/jetty.sh run"]
