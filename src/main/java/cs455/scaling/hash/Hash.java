package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
    public static String SHA1FromBytes(byte[] data)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e)
        {
            System.out.println("Could not find algorithm SHA1: " + e);
            return null;
        }
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }
    // Above method from CS455-LabSession-5 (Slide 14)


}
