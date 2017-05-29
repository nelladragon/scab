// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package storage

import (
//	"log"
	"sync"
)


// Storage for key-value pairs.


// Map of key value pairs.
var keyValuePairsInstance = newKeyValueStorage()


type KeyValueStorage struct {
	StorageBase
	keyValueMap     map[string]string
	keyValueMapLock sync.RWMutex
}

// Create a new instance
func newKeyValueStorage() *KeyValueStorage {
	aMap := make(map[string]string)
	aLock := sync.RWMutex{}
	var kv = KeyValueStorage{}
	kv.keyValueMap = aMap
	kv.keyValueMapLock = aLock

	var _ Storage = &kv  // Enforce interface compliance
	return &kv
}

// Get the KeyValueStorage.
func GetKeyValueStorage() *KeyValueStorage {
	return keyValuePairsInstance
}



func (this *KeyValueStorage) Type() StorageType {
	return KeyValue;
}


// Add to the map / replace an existing value.
func (this *KeyValueStorage) Put(key, value string) {
	this.keyValueMapLock.Lock()
	this.keyValueMap[key] = value
	this.keyValueMapLock.Unlock()
}



// Get a value given a key
func (this *KeyValueStorage) GetValue(key string) (val string, exists bool) {
	val, exists = this.keyValueMap[key]
	return
}


// Get all of the keys
func (this *KeyValueStorage) GetKeys() (keys []string) {
	keys = make([]string, len(this.keyValueMap))

	i := 0
	for k := range this.keyValueMap {
		keys[i] = k
		i++
	}
	return keys
}
