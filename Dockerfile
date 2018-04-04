FROM openjdk:8-jre-slim

RUN apt-get update && apt-get install wget -y

RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.4/wkhtmltox-0.12.4_linux-generic-amd64.tar.xz && xz -d wkhtmltox-0.12.4_linux-generic-amd64.tar.xz && tar xvf wkhtmltox-0.12.4_linux-generic-amd64.tar -C /usr/local/ && mkdir -p /opt/local/glicosebot

ENV GLICOSE_BOT_VERSION "1.0-SNAPSHOT"

RUN mkdir -p /usr/local/glicosebot/

COPY ./target/universal/glicose-bot-$GLICOSE_BOT_VERSION.zip /usr/local/glicosebot/

RUN cd /usr/local/glicosebot/ && unzip glicose-bot-$GLICOSE_BOT_VERSION.zip

EXPOSE 9000

