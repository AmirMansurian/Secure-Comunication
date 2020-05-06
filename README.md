# Client Server Secure comuunication !!!

#### This is a client Server code sending message encrypted with AES 
#### Using java socket programming to connect client to server and use Diffie Hellman to exchange session key between client(Alice) and server(Bob) and then use this key to ecryption/decryption with AES algorithm.

### Steps in both client and server:
  - ##### Create socket to communicate
  - ##### Generate DH key and extract its params(prime, generator) with openssl and set in both client and server code
  - ##### Use P(prime), G(generator) to generate public and private key in each side
  - ##### Choose a random number less than P as private key then public key will be (Publick key = power(G, private key) mod P)
  - ##### Send Public key to other side and recieve a public key from other side and calculate Session key (session key = power(received key, private key) mod P)
  - ##### encrypt messages from user with AES with session 128-bit key using javax.crypto package and send to other side then receive reply from other side and decrypt it and show message
  
## opessl
##### Generate DH key as dhp.pem and extract its components using openssl:
####
```
    openssl dhparam -out dhp.pem 128
    
    openssl pkeyparam -in dhp.pem -text
```

## Server and client code
#### Java socket programming and communicating 
    
        Socket socket = new Socket(ip, port);
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(PubKey.toString());
        in.readUTF();
        socket.close(); 
   #### 
   #### Javax.crypto to encryption and decryption with AES algoritm and encode cipher texts with Base64 to be readable
        Key aesKey = new SecretKeySpec(SessionKey.toByteArray(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(message.getBytes());
        String encodedMessage = Base64.getEncoder().encodeToString(encrypted);
        
        Cipher cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(line2);
        byte[] decValue = cipher.doFinal(decordedValue);
       
         
  
    



