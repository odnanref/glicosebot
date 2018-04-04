FROM openjdk:8-jre-slim

RUN apt-get update && apt-get install wget -y

RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.4/wkhtmltox-0.12.4_linux-generic-amd64.tar.xz && xz -d wkhtmltox-0.12.4_linux-generic-amd64.tar.xz && tar xvf wkhtmltox-0.12.4_linux-generic-amd64.tar -C /usr/local/ && mkdir -p /opt/local/glicosebot

#
# TODO TOMORROW
#
# Set a VAR for the latest version of the build, do a copy from the bot build + VAR
# to a location on the image

EXPOSE 9000

