// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
// Smart Contract Application Browser Gateway: Main Function.


package main

import (
	"log"
	"nelladragon.com/gateway/internal"
	"nelladragon.com/gateway/restapi"
)


func main() {
	log.Println("SCAB Gateway Start-up Start: " + internal.GetTimeNowString())

	// Load settings.
	settings, err := internal.LoadSettings()
	if err != nil {
		log.Println("Error starting server: Settings: " + err.Error())
		return
	}

	// Set-up the REST API
	err = restapi.StartRestApi(settings)
	if err != nil {
		log.Println("Error starting server: REST API: " + err.Error())
		return
	}

	// Configure what should be called if Control-C is hit.
	internal.SetUpCtrlCExit(gracefulExit)
	log.Println("Server Start-up Done: " + internal.GetTimeNowString())

	// Wait forever.
	select{}
}

func gracefulExit() {
	log.Println("Server Shutdown Start: " + internal.GetTimeNowString())

	// TODO

	log.Println("Server Shutdown End: " + internal.GetTimeNowString())
}
