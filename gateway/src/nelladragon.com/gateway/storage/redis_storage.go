// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package storage

import (
	"github.com/xyproto/simpleredis"
	"log"
)


// Storage for key-value pairs using mongodb.


// Name of mongodb host.
const REDIS_HOSTNAME = "redis:6379"
//const REDIS_HOSTNAME = "redis-master:6379"

const KVNAME = "KV"

// Lazy initialize instance variable.
var redisInstance *RedisStorage

type RedisStorage struct {
	StorageBase
	masterPool *simpleredis.ConnectionPool
	//slavePool  *simpleredis.ConnectionPool
	kv *simpleredis.KeyValue
}



// Create a new instance
func newRedisStorage() *RedisStorage {
	m := RedisStorage{}
	m.masterPool = simpleredis.NewConnectionPoolHost(REDIS_HOSTNAME)
	m.kv = simpleredis.NewKeyValue(m.masterPool, KVNAME)
	m.kv.SelectDatabase(1)
	var _ Storage = &m  // Enforce interface compliance
	return &m
}

// Get the KeyValueStorage.
func GetRedisStorage() *RedisStorage {
	if redisInstance == nil {
		redisInstance =  newRedisStorage()
	}
	return redisInstance
}


func (this *RedisStorage) Type() StorageType {
	return Redis;
}



// Add to the map / replace an existing value.
func (this *RedisStorage) Put(key, value string) {
	err := this.kv.Set(key, value)
	if err != nil {
		log.Fatal(err)
	}
}


// Get a value given a key
func (this *RedisStorage) GetValue(key string) (val string, exists bool) {
	val, err := this.kv.Get(key)
	if err != nil {
		return "", false
	}

	return val, true
}


// Get all of the keys
func (this *RedisStorage) GetKeys() (keys []string) {
	keys = make([]string, 0)   // Even if there are no elements, return something
	keys = append(keys, "Not implemented yet")
	return
}


