#!/bin/sh

awslocal dynamodb create-table \
    --table-name person \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5


#--attribute-definitions: Define the attributes and their data types used in the table.
#In this example, we create a table with a primary key called "id" of type String.
#--key-schema: Defines the key schema for the table. For a simple primary key, the primary key name is specified.
#In this case, AttributeName=id,KeyType=HASH.
#--provisioned-throughput: specifies the desired read and write capacity units for the table.