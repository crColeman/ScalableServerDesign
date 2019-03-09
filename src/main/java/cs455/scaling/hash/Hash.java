package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
    private String dataHash;

    public Hash(byte[] data)
    {
        setHash(data);
    }
    private String SHA1FromBytes(byte[] data)
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

    private void setHash(byte[] data)
    {
        dataHash = SHA1FromBytes(data);
    }

    public synchronized boolean compareHash(Hash compareHash)
    {
        return dataHash == compareHash.dataHash;
    }
}
