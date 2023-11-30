<?php
require './vendor/autoload.php';

use Aws\Middleware;
use Aws\S3\S3Client;
use Aws\CommandInterface;
use Aws\Exception\AwsException;
use Psr\Http\Message\RequestInterface;

use Monolog\Logger;
use Monolog\Handler\StreamHandler;

// Presign url function
function generatePresignedURL($s3, $bucketName, $key) {
    $cmd = $s3->getCommand('GetObject', [
        'Bucket' => $bucketName,
        'Key' => $key,
    ]);
    $cmd->getHandlerList()->appendBuild(
    Middleware::mapRequest(function (RequestInterface $request) {
    // Return a new request with the added header
        return $request->withUri(
            $request->getUri()->withQuery(
                $request->getUri()->getQuery() . "test=val&itok=abc123&atest=atestval"
            )
        );
    }),
    'add-custom-param'
    );

    $request = $s3->createPresignedRequest($cmd, '+3 days');
    return (string)$request->getUri();
}


// Configuration Variables
$bucketName = 'bucket1';
$body = "Hello from localstack";
$key = 'key';

// Configuring S3 Client
$s3 = new Aws\S3\S3Client([
    'credentials' => [
		'key'    => 'test',
		'secret' => 'test',
	],
    'version' => '2006-03-01',
    'region' => 'us-east-1',
    // Enable 'use_path_style_endpoint' => true, if bucket name is non dns complient
    'use_path_style_endpoint' => true,
    'endpoint' => 'http://s3.localhost.localstack.cloud:4566',
]);

// Create Bucket
echo "\n\nCreating Bucket: " . $bucketName;
try {
    $result = $s3->createBucket([
        'Bucket' => $bucketName,
    ]);
    echo $result['Location'];
} catch (AwsException $e) {
    echo $e->getAwsErrorMessage();
}

//Listing all S3 Bucket
echo "\n\nList of buckets:";
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

// Generate Presigned URL
$presignedUrl = generatePresignedURL($s3, $bucketName, $key);
echo $presignedUrl;
echo "\n";