package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/aws/credentials"
    "log"
    "time"
    "bytes"
)

// Usage:
//    go run s3_download.go
func main() {
    const bucket = "mybucket"
    const key = "test.go"
    const region = "us-east-1"
    const endpoint = "http://s3.localhost.localstack.cloud:4566"
    const accessKey = "test"
    const secretKey = "test"

    // Initialize a session
	sess, _ := session.NewSession(&aws.Config{
		Region:           aws.String(region),
        Credentials: credentials.NewStaticCredentials(accessKey, secretKey, ""),
		S3ForcePathStyle: aws.Bool(true),
		Endpoint:         aws.String(endpoint),
	})

    // Create S3 service client
    svc := s3.New(sess)

    // Create the S3 Bucket
    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: aws.String(bucket),
    })

    // Put an object into the bucket
    _, err = svc.PutObject(&s3.PutObjectInput{
        Bucket: aws.String(bucket),
        Key:    aws.String(key),
        Body:   aws.ReadSeekCloser(
            bytes.NewReader([]byte("Hello World!")),
        ),
    })

    // Generate Presigned URL
    req, _ := svc.GetObjectRequest(&s3.GetObjectInput{
        Bucket: aws.String(bucket),
        Key:    aws.String(key),
    })
    urlStr, err := req.Presign(15 * time.Minute)

    if err != nil {
        log.Println("Failed to sign request", err)
    }

    log.Println("\n\n--> Presigned URL: ", urlStr)
    log.Println("\n\n--> You can access the object by GET: \ncurl -X GET \"" + urlStr + "\"")
}
