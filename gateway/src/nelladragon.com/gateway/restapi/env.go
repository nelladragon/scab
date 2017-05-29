// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package restapi

import (
	"github.com/ant0ine/go-json-rest/rest"
	"syscall"
	"net/http"
	"strings"
)

func getEnv(w rest.ResponseWriter, r *rest.Request) {
	env := syscall.Environ();

	queryValues := r.URL.Query()
	numQueryKeyValuePairs := len(queryValues)

	name := queryValues.Get("name")



	if len(name) != 0 {
		// "name" key-value pair exists.
		if numQueryKeyValuePairs != 1 {
			rest.Error(w, "Invalid parameter. Parameter values: name or no parameter", http.StatusBadRequest)
			return
		}

		if !isB64OrSimpleAscii(name) {
			rest.Error(w, "name not Base64Uri encoded or ASCII", http.StatusBadRequest)
			return
		}

		// Get a single environment variable.
		for _, s := range env {
			nameVal := strings.Split(s, "=")
			if nameVal[0] == name {
				value := nameVal[1];
				w.WriteJson(&value)
				return
			}

		}

		errMsg := name + " not found";
		w.WriteJson(&errMsg)
		return
	}
	// return all environment variables.

	if numQueryKeyValuePairs != 0 {
		rest.Error(w, "Invalid parameter. Parameter values: name or no parameter", http.StatusBadRequest)
		return
	}


	w.WriteJson(&env)
}

