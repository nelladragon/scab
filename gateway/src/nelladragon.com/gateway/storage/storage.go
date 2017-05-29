// Copyright (C) 2017 Peter Robinson (peter@nelladragon.com)
package storage


// Interface which all storage providers implement.
type Storage interface {
	Type() StorageType
	Put(key, value string)
	GetValue(key string) (val string, exists bool)
	GetKeys() (keys []string)
}


// Base class for all storage providers.
type StorageBase struct {
	// Currently, nothing in base class.
}




// Enum to define type of storage.
type StorageType int

const (
	KeyValue StorageType = iota //1
	Mongo // 2
)

func (t StorageType) String() string {
	switch t {
	case KeyValue:
		return "Key Value"
	case Mongo:
		return "Mongo"
	default:
		panic("unrecognized storage type")
	}
}


// Factory method.
func GetSingleInstance(t StorageType) *Storage {
	var s Storage
	switch t {
	case KeyValue:
		s = GetKeyValueStorage()
	case Mongo:
		s = GetMongoStorage()
	default:
		panic("unrecognized storage type")
	}
	return &s
}



