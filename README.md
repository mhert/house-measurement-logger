# house-measurement-logger

This application collects date from a bunch of sensors in my house and stores it in a mongodb. It runs in docker container on a Raspberry Pi 4.

## Build
### Docker image
```shell
docker buildx build --no-cache --progress plain --tag `basename $PWD` --file Dockerfile .
```

### Executable jar
```shell
./gradlew -Djdk.lang.Process.launchMechanism=vfork clean build -i --stacktrace
```
## Usage

### Own Docker image
```shell
docker run `basename $PWD`
```

### Hosted Docker image
```shell
docker run mhert/house-measurement-logger:latest
```

### Executable jar
```shell
java -jar build/libs/house_data_logger-1.0-SNAPSHOT.jar
```

## Configuration

You can control the app via environment variables. To see whats available check the .env.dist

## Data sources
For now we have:
 - KNX(you can pass a list of sensors as a csv-file)
 - Fronius Inverter(Pulls values every 30 seconds via json-api)
 - IDM heat pump(Pulls values every 30 seconds via modbus)
