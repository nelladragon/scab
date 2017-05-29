// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package internal

import (
	"time"
)

func GetTimeNowString() string {
	t := time.Now()
	return t.Format("2006-01-02 15:04:05")
}
