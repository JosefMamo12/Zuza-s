import express, { json } from 'express'
import bodyParser from 'body-parser'
import firebase from 'firebase/compat/app'
import 'firebase/compat/auth';
import { getDatabase , ref, set} from "firebase/database";

import firebaseAdmin from 'firebase-admin'




const app = express();
app.use(bodyParser.json());

// const express = require('express')
// const app = express()
// app.use(express.json())

// const jwt = require('jsonwebtoken')

// const firebase = require('firebase/app')

import firebaseAccountCredentials from "./serviceAccountCredentials.json" assert {type:'json'};

firebaseAdmin.initializeApp({
  credential: firebaseAdmin.credential.cert(firebaseAccountCredentials),
  databaseURL: "https://myapplicaition-default-rtdb.firebaseio.com"
});

//For web applications
import firebaseConfig from './firebaseConfig.json'assert{type:'json'};

firebase.initializeApp(firebaseConfig);


// Set up an Express.js route to handle the login request
app.post('/login', async (req, res) => {
  console.log("login request");
    const { email, password } = req.body;
    
    try {
        // Verify the email and password using the Firebase Web SDK
        const userCred = await firebase.auth().signInWithEmailAndPassword(email,password);
        // Check if the user is authenticated
             console.log("user is authenticated"); 
            // If the user is authenticated, generate a JWT and send it back to the client
            let token = firebaseAdmin.auth().createCustomToken(userCred.user.uid)
            token.then(function(result){
              res.status(200).send({token: result});
            });      
          
    } catch (error) {
      console.log("error: " + error); 
        res.status(401).send({ error: 'Invalid email or password' });
    }
});

app.post('/register', async (req, res) =>{  
  console.log("register request");
  const {email, age, fullName, password, isAdmin} = req.body;
  try {
    console.log(isAdmin);
    const userCred = await firebase.auth().createUserWithEmailAndPassword(email,password);
    writeUserData(userCred.user.uid,email,age,fullName);
    // Check if the user is authenticated
    console.log("User registered successfully"); 
  }catch(error){
    console.log("error: " + error);
    res.status(401).send({ error: 'Register failed' });
  }
});

function writeUserData (userId, email, age, fullName){
  const database = getDatabase();
  set(ref(database, 'Users/' + userId), {
    email: email,
    age: age,
    fullName: fullName,
    admin: false,
    uid: userId
  });
}

// // Set up an Express.js route to handle requests to a protected route or resource
// app.get('/protected', async (req, res) => {
//     console.log("Requested protected route")
//     // Get the JWT from the request header
//     const authorization = req.headers['authorization'];
//     console.log("Authorization: " + authorization)
//     if (authorization) {
//       try {
//         console.log("Requested authorization: " + authorization);
//         // Verify the JWT using the Firebase Admin SDK
//         const decodedToken = await admin.auth().verifyIdToken(authorization);
//         console.log("Decoded token: " + JSON.stringify(decodedToken));
//         // If the JWT is valid, allow access to the protected route or resource
//         res.send({ data: 'Access granted' });
//       } catch (error) {
//         // If the JWT is invalid, return an error
//       }
//     } else {
//       // If there is no authorization
//       // If there is no authorization header, return an error
//       res.status(401).send({ error: 'No token provided' });
//     }
// });

const port = 8080;
app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});