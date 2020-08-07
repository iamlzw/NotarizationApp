package main

import (
	"encoding/json"
	"fmt"
	"time"
	
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

type SmartContract struct {
	contractapi.Contract
}

// Define the DocRecord Structure, which holds the signature of the document
// signed by issuer, and the time when this record is created
type DocRecord struct {
	Signature   string `json:"signature"`
	Time  string `json:"time"`
}

// e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
// InitLedger adds a base set of cars to the ledger
func (s *SmartContract) InitLedger(ctx contractapi.TransactionContextInterface) error {
	
	doc := DocRecord {Signature: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",Time:time.Now().Format(time.RFC3339)}

	docrecordAsBytes, _ := json.Marshal(doc)

	err := ctx.GetStub().PutState("1001", docrecordAsBytes)

	if err != nil {
		return fmt.Errorf("Failed to put to world state. %s", err.Error())
	}
	return nil
}
func (s *SmartContract) QueryDocRecord(ctx contractapi.TransactionContextInterface, docId string) (*DocRecord, error) {
	docrecordAsBytes, err := ctx.GetStub().GetState(docId)

	if err != nil {
		return nil, fmt.Errorf("Failed to read from world state . %s", err.Error())
	}
	if docrecordAsBytes == nil {
		return nil, fmt.Errorf("%s does not exist", docId)
	}

	doc := new(DocRecord)
	_ = json.Unmarshal(docrecordAsBytes, doc)

	return doc, nil
}

func (s *SmartContract) CreateDocRecord(ctx contractapi.TransactionContextInterface, docId string, signature string) error{

	doc := DocRecord {Signature: signature,Time:time.Now().Format(time.RFC3339)}

	docrecordAsBytes, _ := json.Marshal(doc)

	return ctx.GetStub().PutState(docId, docrecordAsBytes)
}

func main() {

	chaincode, err := contractapi.NewChaincode(new(SmartContract))

	if err != nil {
		fmt.Printf("Error create fabcar chaincode: %s", err.Error())
		return
	}

	if err := chaincode.Start(); err != nil {
		fmt.Printf("Error starting fabcar chaincode: %s", err.Error())
	}
}