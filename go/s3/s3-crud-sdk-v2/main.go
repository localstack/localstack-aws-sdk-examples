package main

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"os"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	smithyendpoints "github.com/aws/smithy-go/endpoints"
)

var (
	awsRegion   string
	awsEndpoint string
	bucketName  string

	s3svc *s3.Client
)

func init() {
	awsRegion = os.Getenv("AWS_REGION")
	awsEndpoint = os.Getenv("AWS_ENDPOINT")
	bucketName = os.Getenv("S3_BUCKET")

	awsRegion = "us-east-1"
	awsEndpoint = "http://localhost:4566"
	bucketName = "test"

	awsCfg, err := config.LoadDefaultConfig(context.TODO(),
		config.WithRegion(awsRegion),
	)
	if err != nil {
		log.Fatalf("Cannot load the AWS configs: %s", err)
	}

	s3svc = s3.NewFromConfig(awsCfg, func(o *s3.Options) {
		o.UsePathStyle = true
		o.EndpointResolverV2 = newLocalstackEndpointResolver(awsEndpoint)
	})
}

func main() {

	// Create Bucket
	s3svc.CreateBucket(context.TODO(), &s3.CreateBucketInput{
		Bucket: aws.String(bucketName),
	})

	// Put Keys
	s3Key1 := "key1"
	body1 := []byte(fmt.Sprintf("Hello from localstack 1"))
	s3svc.PutObject(context.TODO(), &s3.PutObjectInput{
		Bucket:             aws.String(bucketName),
		Key:                aws.String(s3Key1),
		Body:               bytes.NewReader(body1),
		ContentType:        aws.String("application/text"),
		ContentDisposition: aws.String("attachment"),
	})
	s3Key2 := "key2"
	body2 := []byte(fmt.Sprintf("Hello from localstack 2"))
	s3svc.PutObject(context.TODO(), &s3.PutObjectInput{
		Bucket:             aws.String(bucketName),
		Key:                aws.String(s3Key2),
		Body:               bytes.NewReader(body2),
		ContentType:        aws.String("application/text"),
		ContentDisposition: aws.String("attachment"),
	})

	// List Buckets
	result, _ := s3svc.ListBuckets(context.TODO(), &s3.ListBucketsInput{})
	fmt.Println("Buckets:")
	for _, bucket := range result.Buckets {
		fmt.Println(*bucket.Name + ": " + bucket.CreationDate.Format("2006-01-02 15:04:05 Monday"))
	}

	// List Objects
	output, err := s3svc.ListObjectsV2(context.TODO(), &s3.ListObjectsV2Input{
		Bucket: aws.String(bucketName),
	})
	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("List of Objects in %s:\n", bucketName)
	for _, object := range output.Contents {
		fmt.Printf("key=%s size=%d\n", aws.ToString(object.Key), object.Size)
	}

	// Delete Keys
	input := s3.DeleteObjectsInput{
		Bucket: aws.String(bucketName),
		Delete: &types.Delete{
			Objects: []types.ObjectIdentifier{
				{Key: aws.String(s3Key1)},
				{Key: aws.String(s3Key2)},
			},
		},
	}
	s3svc.DeleteObjects(context.TODO(), &input)
}

type localstackEndpointResolver struct {
	inner    s3.EndpointResolverV2
	endpoint *string
}

func newLocalstackEndpointResolver(endpoint string) *localstackEndpointResolver {
	return &localstackEndpointResolver{
		inner:    s3.NewDefaultEndpointResolverV2(),
		endpoint: &endpoint,
	}
}

func (l *localstackEndpointResolver) ResolveEndpoint(ctx context.Context, params s3.EndpointParameters) (smithyendpoints.Endpoint, error) {
	params.Endpoint = l.endpoint
	return l.inner.ResolveEndpoint(ctx, params)
}
