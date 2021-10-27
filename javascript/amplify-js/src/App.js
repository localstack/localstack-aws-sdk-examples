import React, { useEffect } from 'react';
import { Container } from 'reactstrap';

import Amplify, { Auth } from 'aws-amplify';
import AWS from 'aws-sdk';
import applyPatches from 'amplify-js-local/lib/es6';

const region = "eu-central-1";
const endpoint = "http://localhost:4566";
AWS.config.update({
  endpoint, region,
  accessKeyId: "test", secretAccessKey: "test"}
);
applyPatches();

function App() {

  const testAmplifyFlow = async () => {
    const client = new AWS.CognitoIdentityServiceProvider();

    const poolName = 'pool1';
    const clientName = 'client1';
    const username = 'amplify1';
    const password = 'pass123';
    const email = 'test@example.com';

    // create pool and client
    const pools = await client.listUserPools({MaxResults: 100}).promise();
    let pool = pools[0];
    if (!pool) {
      pool = (await client.createUserPool({PoolName: poolName}).promise()).UserPool;
    }
    const poolClients = (await client.listUserPoolClients({UserPoolId: pool.Id}).promise()).UserPoolClients;
    let poolClient = poolClients[0];
    if (!poolClient) {
      poolClient = (await client.createUserPoolClient({UserPoolId: pool.Id, ClientName: clientName}).promise()).UserPoolClient;
    }

    // configure Amplify
    const amplifyConfig = {
      Auth: {
        region,
        endpoint,
        userPoolId: pool.Id,
        userPoolWebClientId: poolClient.ClientId
      },
      API: {
        endpoints: [{endpoint, region}]
      }
    }
    Amplify.configure(amplifyConfig);

    // sign up / create user
    try {
      await client.adminGetUser({UserPoolId: pool.Id, Username: username}).promise();
    } catch (e) {
      const details = {
        username, password, attributes: {given_name: "test", family_name: "test1", email },
      }
      await Auth.signUp(details);
    }

    // confirm signup
    try {
      // TODO: expose authentication code via backdoor API and fetch from there
      const code = "737898";
      await Auth.confirmSignUp(username, code);
    } catch (e) {
      console.log(e);
    }
    const result = await Auth.signIn(username, password)
    console.log(result);
  }

  useEffect(() => {
    testAmplifyFlow();
  }, []);

  return (
    <div className="App">
      <Container>
        Sample app - please see source code (and browser console) for more details ...
      </Container>
    </div >
  );
}

export default App;
