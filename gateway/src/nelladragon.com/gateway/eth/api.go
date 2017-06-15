package eth

// import "github.com/bitly/go-simplejson"

func GetBalance(address string) (string) {
	res := call("eth_getBalance", "[\""+address+"\", \"latest\"]").Get("result").MustString()
	return res
}
