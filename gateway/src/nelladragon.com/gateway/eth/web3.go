package eth



import (
	"github.com/bitly/go-simplejson"
	"github.com/parnurzeal/gorequest"
	"log"
        "fmt"
)

const (
	apiUrl string = "http://ethereum:8545" // 8545 (default port)
)

func call(args ...string) (*simplejson.Json) {
	method := args[0]
	params := "[]"
	if len(args) > 1 {
		params = args[1]
	}

	postBody := `{"jsonrpc":"2.0","method":"`+method+`","params":`+params+`,"id":1}`
	fmt.Println("postBody: " + postBody)

	_, body, errs := gorequest.New().Post(apiUrl).
		Send(postBody).
		End()

	if errs != nil {
		panic(errs)
	}
	fmt.Println("response body: " + body)


	js, err := simplejson.NewJson([]byte(body))
	if err != nil {
		log.Fatalln(err)
	}

	return js
}
