package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
    private String dataHash;
    //TODO: Re-implement with default constructor and only hash function. Not using Hash as a data structure?
    public Hash(byte[] data)
    {
        setHash(data);
    }


    public String SHA1FromBytes(byte[] data)
    {
        MessageDigest digest = null;
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


    private void setHash(byte[] data)
    {
        dataHash = SHA1FromBytes(data);
    }

    public String getHash()
    {
        return dataHash;
    }
    public String computeHash(byte[] data)
    {
        return SHA1FromBytes(data);
    }

    public synchronized boolean compareHash(Hash compareHash)
    {
        return dataHash == compareHash.dataHash;
    }
}
