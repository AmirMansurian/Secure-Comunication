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
import java.util.Random;
import javax.crypto.spec.DHParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;



public class Client {
    
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out =null;
    private BigInteger prime = new BigInteger("f420349ab7fe23784da35da56eca06e3", 16);
    private BigInteger generator = new BigInteger("2");
    private BigInteger PrKey;
    private BigInteger PubKey;
    private BigInteger PublicKey;
    private BigInteger SessionKey;
    
    
    public Client(String ip, int port) throws NoSuchAlgorithmException, InvalidParameterSpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        try
        {
            socket = new Socket(ip, port);
            System.out.println("Connected");
            
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            
        }
        
         catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        
        catch(IOException i)
        {
            System.out.println(i);
        }
        
        
        PrKey = new BigInteger(128, new SecureRandom());
        PrKey = PrKey.mod(prime); 
        PubKey = generator.modPow(PrKey, prime); // calculated public client key (A=g^a(modp))
        
          System.out.println("my Public Key is : "+ PubKey.toString());
          System.out.println("private Key is: "+ PrKey.toString());
        
        
            String line = "";
        
       
            try
            {  
                out.writeUTF(PubKey.toString());
                
                line = in.readUTF();
                PublicKey= new BigInteger(line);
                System.out.println("Public Key from client: "+line);
                
                SessionKey = PublicKey.modPow(PrKey, prime);
                System.out.println("session key is : " + SessionKey.toString());
                
                String str ="";
               

                
                for (int i = 0; i < 128 - SessionKey.toString(2).length(); i++) {
                            str += "0";
                    }
                
                SessionKey = new BigInteger((str + SessionKey.toString(2)), 2);
                
                
               // System.out.println(str);
                
                String message = "";
                Scanner sc = new Scanner(System.in);
                message = sc.nextLine();
                
               // key = (str+(SessionKey.toString(2))).getBytes();
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
                                               
         
                //SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                
                byte[] encrypted = cipher.doFinal(message.getBytes());
                String encodedMessage = Base64.getEncoder().encodeToString(encrypted);
                
                System.out.println("message to server : " + message);
                System.out.println("encrypted message to server : " + encodedMessage);
                
                out.writeUTF(encodedMessage);
                
                
                String line2 = "";
                line2 = in.readUTF();
                
                Cipher cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                
                
                 byte[] decordedValue = new BASE64Decoder().decodeBuffer(line2);
                 byte[] decValue = cipher.doFinal(decordedValue);
                 
                 String str2 = new String(decValue, "UTF-8");

                System.out.println("encrypted message from server : " + line2);
                System.out.println("decrypted message from server : " + str2);
                
            } 
            catch(IOException i) 
            { 
                System.out.println(i); 
            } 
        
  
        // close the connection 
        try
        { 
            in.close(); 
            out.close(); 
            socket.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
    }

 
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidParameterSpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
        Client client= new Client("127.0.0.1", 5000);
    }

    private byte[] getSliceOfArray(byte[] toByteArray, int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
}
