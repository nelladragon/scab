// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package restapi

import "regexp"

func isB64OrSimpleAscii(s string) bool {
	regex := regexp.MustCompile("^*([A-Za-z0-9\\-_])$");
	return regex.MatchString(s)
}
