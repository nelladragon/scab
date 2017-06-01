#!/bin/sh
# Exercise the REST API

# PROXY_SERVER="192.168.99.100"
PROXY_SERVER="35.185.244.169"
RESTAPI_PORT="80"
BASEURI="http://"$PROXY_SERVER:$RESTAPI_PORT

# Get the version of the server software.
echo GET /version
curl $BASEURI/version
echo 
echo 

# Get the all environment variables of the worker processing this request.
echo GET /env
curl $BASEURI/env
echo 
echo 

# Get just the PATH environment variable of the worker processing this request.
echo GET /env?name=PATH
curl $BASEURI/env?name=PATH
echo 
echo 

# Get the IP address of the worker processing this request.
echo GET /ip
curl $BASEURI/ip
echo 
echo 

# Get the hostname of the worker processing this request.
echo GET /host
curl $BASEURI/host
echo 
echo 

# Get the keys for all key-value pairs stored in the database.
echo GET /value
curl $BASEURI/value
echo 
echo 

# Set a specific key-value pair.
echo POST/value  with {Key:testkey, Value:"a value"}
curl -H "Content-Type: application/json" -X POST -d '{"Key":"testkey", "Value":"a value"}' $BASEURI/value
echo 
echo 

# Get that key-value pair. Note that the key must be presented as a Base64 encoded string.
echo GET /value?Key=testkey
curl $BASEURI/value?Key=testkey
echo 
echo 

# Get the keys for all key-value pairs stored in the database.
echo GET /value
curl $BASEURI/value
echo 
echo 
