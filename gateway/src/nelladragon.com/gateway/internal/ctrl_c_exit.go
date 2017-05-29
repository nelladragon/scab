// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package internal

import (
	"os"
	"os/signal"
)


// Configure the program such that when Control-C is hit, gracefulExit is called, followed by program exit.
func SetUpCtrlCExit(gracefulExit func()) {
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt)
	go func() {
		<-sig
		gracefulExit()
		os.Exit(0)
	}()
}
