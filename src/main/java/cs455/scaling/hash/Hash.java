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

        //Returns the hash with 0's padded to the left
        //TODO: Check to make sure padding should be to the left.
        return String.format("%40s", hashInt.toString(16)).replace(' ', '0');


    }

    // Above method from CS455-LabSession-5 (Slide 14)
//    public static String padHash(String hash, int n)
//    {
//        return String.format("%" + n + );
//    }

}
