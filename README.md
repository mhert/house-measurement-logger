# house-measurement-logger

This collects date from a bunch of sensors in my house and stores it in a mongodb. It runs in docker container on a Raspberry Pi 4.

## Usage 
The image is available for amd64 and arm64v8 here: https://github.com/users/mhert/packages/container/package/house-measurement-logger
```
docker run ghcr.io/mhert/house-measurement-logger:latest
```
You can configure it via env vars. To see whats available check the .env.dist

## Data sources
For now we have:
 - KNX(you can pass a list of sensors as a csv-file)
 - Fronius Inverter(Pulls values every 30 seconds via json-api)
 - IDM heat pump(Pulls values every 30 seconds via modbus)
