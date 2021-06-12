# Instruction

### Initialize go project
```
go mod init example.com/m/v2
```

### Install required dependancies (Example)
```
go get "github.com/aws/aws-sdk-go-v2/aws"
go get "github.com/aws/aws-sdk-go-v2/config"
go get "github.com/aws/aws-sdk-go-v2/service/s3"
go get "github.com/aws/aws-sdk-go-v2/service/s3/types"
```

### Run the code
Copy the required file and run the command as below.
```
go run s3.go
```