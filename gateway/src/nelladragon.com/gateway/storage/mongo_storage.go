// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package storage

import (
	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
	"log"
)


// Storage for key-value pairs using mongodb.


// Name of mongodb host.
const MONGO_HOSTNAME = "mongodb"
const MONGO_URI = "mongodb://" + MONGO_HOSTNAME

const DB_NAME = "kv"
const DB_COLLECTION = "values"

// Lazy initialize instance variable.
var mongoInstance *MongoStorage


type MongoStorage struct {
	StorageBase
	session *mgo.Session
}



// Create a new instance
func newMongoStorage() *MongoStorage {
	m := MongoStorage{}
	m.session = getSession()

	var _ Storage = &m  // Enforce interface compliance

	return &m
}

// Get the KeyValueStorage.
func GetMongoStorage() *MongoStorage {
	if mongoInstance == nil {
		mongoInstance =  newMongoStorage()
	}
	return mongoInstance
}


func (this *MongoStorage) Type() StorageType {
	return Mongo;
}



// Add to the map / replace an existing value.
func (this *MongoStorage) Put(key, value string) {
	session := this.session.Copy()
	defer session.Close()

	kv := CollectionKeyValue{}
	kv.Key = key
	kv.Value = value

	collection := session.DB(DB_NAME).C("values")

	err := collection.Insert(kv)
	if err != nil {
		log.Fatal(err)
	}
}


// Get a value given a key
func (this *MongoStorage) GetValue(key string) (val string, exists bool) {
	session := this.session.Copy()
	defer session.Close()

	kv := CollectionKeyValue{}

	collection := session.DB(DB_NAME).C(DB_COLLECTION)
	err := collection.Find(bson.M{"Key": key}).One(&kv)
	if err != nil {
		return "", false
	}

	return kv.Value, true
}


// Get all of the keys
func (this *MongoStorage) GetKeys() (keys []string) {
	session := this.session.Copy()
	defer session.Close()

	kv := CollectionKeyValue{}

	collection := session.DB(DB_NAME).C(DB_COLLECTION)
	iter := collection.Find(bson.M{}).Iter()

	keys = make([]string, 0)   // Even if there are no elements, return something
	for iter.Next(&kv) {
		keys = append(keys, kv.Key)
	}
	return
}



// Database collection.
type CollectionKeyValue struct {
	Key   	string	`bson:"Key"`
	Value 	string  `bson:"Value"`
}


func getSession() *mgo.Session {
	// Connect to our local mongo
	s, err := mgo.Dial(MONGO_URI)

	// Check if connection error, is mongo running?
	if err != nil {
		panic(err)
	}
	// TODO what does this do?
	// s.SetSafe(&mgo.Safe{})
	return s
}

