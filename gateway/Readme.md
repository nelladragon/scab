# Copyright (C) 2017 Peter Robinson


Smart Contract Application Browser Gateway
==========================================

The golang code is work in progress.

What I am up to is this:

- There is a HTTP REST API (by default on port 8080)
- The main method is in nelladragon.com/gateway/gateway.go


The REST API is:

- GET /version           
  Get the version number of the proxy.            
- GET /value
  Get the list of keys, for the key-value pairs being stored. 
- POST /value
  Add or set a key - value pair.
- GET /value?key=<some key>
  Get the value for a key.

- GET /env
  Get all of the environment variables.
- GET /env?name=<somename>
  Get a specific environment variable.
- GET /time
  Get the container's understanding of current time.
- GET /ip
  Get the container's understanding of its IP address.
- GET /host
  Get the container's undderstanding of its host name.

 
Build
-----
The build uses a split Docker build to create a significantly smaller docker image.