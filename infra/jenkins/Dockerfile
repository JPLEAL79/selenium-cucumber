FROM jenkins/jenkins:lts-jdk17

USER root

# Instala Maven
RUN apt-get update \
 && apt-get install -y maven \
 && apt-get clean

USER jenkins
