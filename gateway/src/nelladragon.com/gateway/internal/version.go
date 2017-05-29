// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package internal

const ver = "1.0"
const build = "unknown"

// Capture converted to bytes.
type VersionInfo struct {
	Version   string
	BuildDate string
}


func GetVersion() VersionInfo {
	return VersionInfo{ver, build}
}


