package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/aws/credentials"
    "log"
    "time"
)

// Usage:
//    go run s3_download.go
func main() {
    // Initialize a session
	sess, _ := session.NewSession(&aws.Config{
		Region:           aws.String("us-east-1"),
        Credentials: credentials.NewStaticCredentials("test", "test", ""),
		S3ForcePathStyle: aws.Bool(true),
		Endpoint:         aws.String("http://localhost:4566"),
	})

    // Create S3 service client
    svc := s3.New(sess)

    // Generate Presigned URL
    req, _ := svc.GetObjectRequest(&s3.GetObjectInput{
        Bucket: aws.String("mybucket"),
        Key:    aws.String("test.go"),
    })
    urlStr, err := req.Presign(15 * time.Minute)

    if err != nil {
        log.Println("Failed to sign request", err)
    }

    log.Println("The URL is:", urlStr)
}
