// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package restapi

import (
	"log"
	"github.com/ant0ine/go-json-rest/rest"
	"net/http"
	"nelladragon.com/gateway/internal"
	"os"
	"net"
	"time"
)


// Start the REST API as a separate go routine.
// Any start-up errors are returned.
func StartRestApi(settings internal.AppSettings) error {
	// Start the REST API and block until the error code is set.
	errChan := make(chan error, 1)
	go startRestApi(settings, errChan)
	return <- errChan
}


func startRestApi(settings internal.AppSettings, errChannel chan<- error) {
//	rest.ErrorFieldName = dtruthcommon.ErrorMsg

	api := rest.NewApi()
	api.Use(rest.DefaultDevStack...)
	router, err := rest.MakeRouter(
		rest.Get("/version", checkVersion),             // Get code version
		rest.Get("/id", showId),			// Get something to show which service has been connected with.

		rest.Get("/env", getEnv),                       // Get environment variable(s).
								// /env returns all environment variables.
								// /env?name=<env> returns the environment variable env

		rest.Get("/time", getTime),                       // Get system time.

		rest.Get("/ip", getIp),                      	// Get IP address.
		rest.Get("/host", getHostname),                	// Get host name.



//		rest.Get("/config", getConfig),                 // Get the current config.

		rest.Post("/value", setValue),			// Set a value.
		rest.Get("/value", getKeyValues),		// Get a value given a key or a list of all the keys.
	)
	if err != nil {
		log.Fatal(err)
		errChannel <- err
		return
	}

	log.Println("Running REST API on: " + settings.BindRestApi)
	api.SetApp(router)
	errChannel <- nil
	log.Fatal(http.ListenAndServe(":" + settings.BindRestApi, api.MakeHandler()))
}

func checkVersion(w rest.ResponseWriter, r *rest.Request) {
	v := internal.GetVersion()
	w.WriteJson(&v)
}




func getTime(w rest.ResponseWriter, r *rest.Request) {
	w.WriteJson(time.Now())
}


func getHostname(w rest.ResponseWriter, r *rest.Request) {
	name, err := os.Hostname()
	if err != nil {
		log.Printf("Get host name1: %v\n", err)
		return
	}

	w.WriteJson(name)
}


func getIp(w rest.ResponseWriter, r *rest.Request) {
	name, err := os.Hostname()
	if err != nil {
		log.Printf("Get host name2: %v\n", err)
		return
	}

	addrs, err := net.LookupHost(name)
	if err != nil {
		log.Printf("Lookup host: %v\n", err)
		return
	}

	w.WriteJson(addrs)
}

func showId(w rest.ResponseWriter, r *rest.Request) {
	w.WriteJson("GATEWAY")
}





