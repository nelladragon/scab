# Copyright (C) 2017 Peter Robinson

FROM golang
MAINTAINER Peter Robinson <peter@nelladragon.com>

# Add code to be run.
#ADD src/nelladragon.com/gateway /go/src/nelladragon.com/gateway
# Hack to improve build speed by grabbing dependancies from source directory.
ADD src /go/src/

# Add the runtime dockerfile into the context as Dockerfile
COPY Dockerfile.run /go/bin/Dockerfile

# Add the settings file needed at runtime to the bin directory so it
# can be accessed by the runtime Dockerfile.
COPY settings.json /go/bin/settings.json


# Get all dependancies, but don't install.
RUN go get -d -v nelladragon.com/gateway
# Do a full compile of app and dependancies, forcing static linking.
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o /go/bin/gateway nelladragon.com/gateway
# Don't do install, as build now done. 
#RUN go install nelladragon.com/gateway


# Set the workdir to be /go/bin which is where the binaries are built
WORKDIR /go/bin
# Export the WORKDIR as a tar stream
CMD tar -cf - .
