// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package internal

import (
"log"
"io/ioutil"
"encoding/json"
)

const SETTINGS_FILE_NAME = "settings.json"
const SETTINGS_LOC_DEV = SETTINGS_FILE_NAME
const SETTINGS_LOC_CONTAINER = "/etc/gateway/" + SETTINGS_FILE_NAME


// Defaults
const SETTINGS_DEFAULT_BIND_REST_API = "8080"
const SETTINGS_DEFAULT_VERBOSE = true

type AppSettings struct {
	BindRestApi string // Port number to bind to for REST API.
	Verbose bool       // If true, then more logging is shown.
}

var defaults = AppSettings{SETTINGS_DEFAULT_BIND_REST_API, SETTINGS_DEFAULT_VERBOSE}

var settings = defaults

func LoadSettings() (set AppSettings, err error) {
	// Load settings.
	settingsBytes, err := ioutil.ReadFile(SETTINGS_LOC_CONTAINER)
	if err != nil {
		settingsBytes, err = ioutil.ReadFile(SETTINGS_LOC_DEV)
		if err != nil {
			log.Println("Failed to read settings.json" + err.Error())
			return
		}
	}

	err = json.Unmarshal(settingsBytes, &settings)
	if err != nil {
		log.Println("Failed to parse settings.json: " + err.Error())
	}

	if settings.Verbose {
		log.Printf("Settings: (%+v)\n", settings)
	}

	set = settings
	return
}

