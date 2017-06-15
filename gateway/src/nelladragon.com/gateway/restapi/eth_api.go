// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package restapi

// This file takes a REST call on the /eth API, error checks the parameters,
// and does the call to the eth code.

import (
	"github.com/ant0ine/go-json-rest/rest"
	"log"
	"net/http"
	"nelladragon.com/gateway/eth"
)



func getEthBalance(w rest.ResponseWriter, r *rest.Request) {
	queryValues := r.URL.Query()
	account := queryValues.Get("Account")
	log.Printf("Account: %s\n", account)
	if len(account) == 0 {
		rest.Error(w, "Invalid parameters. Parameter values: Account=account", http.StatusBadRequest)
		return
	}
	if !isB64OrSimpleAscii(account) {
		rest.Error(w, "Account not Base64Url or ASCII encoded", http.StatusBadRequest)
		return
	}

	balance := eth.GetBalance(account);
	log.Printf("Balance: %s\n", balance)
	w.WriteJson(&balance)
}

