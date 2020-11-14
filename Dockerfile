FROM alpine:latest

ENV SPRING_PROFILES_ACTIVE production

RUN apk --no-cache add openjdk11

WORKDIR /usr/bin/app

COPY build/libs/twitch_sub_steam.jar .

CMD ["java", "-Xms32m", "-Xmx64m","-jar", "./twitch_sub_steam.jar"]
