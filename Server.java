import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
        


public class Server 
{ 
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream input = null;
    private DataOutputStream output =null;
    private BigInteger prime = new BigInteger("f420349ab7fe23784da35da56eca06e3", 16);
    private BigInteger generator = new BigInteger("2");
    private BigInteger PrKey;
    private BigInteger PubKey;
    private BigInteger PublicKey;
    private BigInteger SessionKey;
    
   
    public Server(int port) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {        
        
        
        try
        {
            server = new ServerSocket(port);
            
            System.out.println("Server started"); 
  
            System.out.println("Waiting for a client ...");
            
            socket = server.accept();
            System.out.println("Client accepted"); 
            
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());
            
           PrKey = new BigInteger(128, new SecureRandom());
           PrKey = PrKey.mod(prime);
           PubKey = generator.modPow(PrKey, prime); // calculated public client key (A=g^a(modp))
           
           System.out.println("my Public Key is : "+ PubKey.toString());
           System.out.println("private Key is: "+ PrKey.toString());
           
           
                String line = "";
                String message = "";
            
                try
                {
                    line = input.readUTF();
                    PublicKey = new BigInteger(line);
                    System.out.println("Public Key from client: "+PublicKey.toString());
                    output.writeUTF(PubKey.toString());
                    
                    SessionKey = PublicKey.modPow(PrKey, prime);
                    System.out.println("session key is : " + SessionKey.toString());
                    
                    
                     String str ="";
               

                
                for (int i = 0; i < 128 - SessionKey.toString(2).length(); i++) {
                            str += "0";
                    }
                
                SessionKey = new BigInteger((str + SessionKey.toString(2)), 2);
                
                    
                    message = input.readUTF();
                    System.out.println("encrypted message from client : " + message);
                    
                    
                    
               Key aesKey = null;
               byte[] key = new byte[16];
               
               if (SessionKey.toByteArray().length != 16)
               {                  
                    
                  for (int i=0; i< SessionKey.toByteArray().length -1 ; i++)
                     key[i] = SessionKey.toByteArray()[i+1];
                  aesKey = new SecretKeySpec(key, "AES");
               }
               else
                   aesKey = new SecretKeySpec(SessionKey.toByteArray(), "AES");
                    
                    
                    
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                
                
                 byte[] decordedValue = new BASE64Decoder().decodeBuffer(message);
                 byte[] decValue = cipher.doFinal(decordedValue);
                 
                 String str2 = new String(decValue, "UTF-8");
                 
                 System.out.println("decrypted message from client : " + str2);
                 
                 
                 String message2 = "";
                Scanner sc = new Scanner(System.in);
                message2 = sc.nextLine();
                
                Cipher cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher2.init(Cipher.ENCRYPT_MODE, aesKey);
                
                byte[] encrypted2 = cipher2.doFinal(message2.getBytes());
                String encodedMessage2 = Base64.getEncoder().encodeToString(encrypted2);
                
                System.out.println("message to client : " + message2);
                System.out.println("encrypted message to client : " + encodedMessage2);
                
                output.writeUTF(encodedMessage2);
                    
                } 
                
                catch(IOException i)
                {
                    System.out.println(i);
                }
            
            
             System.out.println("Closing connection"); 
             socket.close();
             input.close();
        }
        
        catch(IOException i)
        {
            System.out.println(i);
        }
        
    }
    
    
 
    
    
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidParameterSpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
         
       Server server = new Server(5000);
        
       
    }
       
}