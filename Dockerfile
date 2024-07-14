FROM amazoncorretto:21.0.3 as builder-cache
RUN mkdir /gradle-user-home
RUN mkdir /house-measurement-logger
WORKDIR /house-measurement-logger
ENV GRADLE_USER_HOME /gradle-user-home
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY gradle.properties ./
RUN uname -m
# starting with Java 13 posix_spawn is used in linux, instead of vfork. Unfortunatley this is not
# working with arm64. So we're using vfork
RUN ./gradlew -Djdk.lang.Process.launchMechanism=vfork clean build -i --stacktrace --no-daemon

FROM amazoncorretto:21.0.3 as builder
RUN mkdir /gradle-user-home
COPY --from=builder-cache /gradle-user-home/caches /gradle-user-home/caches
RUN mkdir /house-measurement-logger
WORKDIR /house-measurement-logger
ENV GRADLE_USER_HOME /gradle-user-home
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY gradle.properties ./
COPY src ./src
# starting with Java 13 posix_spawn is used in linux, instead of vfork. Unfortunatley this is not
# working with arm64. So we're using vfork
RUN ./gradlew -Djdk.lang.Process.launchMechanism=vfork clean build -i --stacktrace --no-daemon

FROM amazoncorretto:21.0.3
LABEL org.opencontainers.image.source=https://github.com/mhert/house-measurement-logger
COPY --from=builder /house-measurement-logger/build/libs/house-measurement-logger-1.0-SNAPSHOT.jar /house-measurement-logger.jar
CMD [ "java", "-jar", "/house-measurement-logger.jar"]
