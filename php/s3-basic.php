<?php
require './vendor/autoload.php';

use Aws\S3\S3Client;  
use Aws\Exception\AwsException;

// Configuration Variables
$bucketName = 'bucket1';
$body = "Hello from localstack";
$key = 'key';

// Configuring S3 Client
$s3 = new Aws\S3\S3Client([
    'version' => '2006-03-01',
    'region' => 'us-east-1',
    // Enable 'use_path_style_endpoint' => true, if bucket name is non dns complient
    'use_path_style_endpoint' => true,
    'endpoint' => 'http://s3.localhost.localstack.cloud:4566',
]);

// Create Bucket
try {
    $result = $s3->createBucket([
        'Bucket' => $bucketName,
    ]);
    return 'The bucket\'s location is: ' .
        $result['Location'] . '. ' .
        'The bucket\'s effective URI is: ' . 
        $result['@metadata']['effectiveUri'];
} catch (AwsException $e) {
    return 'Error: ' . $e->getAwsErrorMessage();
}

//Listing all S3 Bucket
$buckets = $s3->listBuckets();
foreach ($buckets['Buckets'] as $bucket) {
    echo $bucket['Name'] . "\n";
}

try {
    $result = $s3->putObject([
        'Bucket' => $bucketName,
        'Key' => $key,
        'Body' => $body,
    ]);
} catch (S3Exception $e) {
    echo $e->getMessage() . "\n";
}
