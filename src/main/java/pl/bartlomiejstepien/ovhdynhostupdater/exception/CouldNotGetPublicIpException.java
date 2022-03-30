package pl.bartlomiejstepien.ovhdynhostupdater.exception;

public class CouldNotGetPublicIpException extends Exception
{
    public CouldNotGetPublicIpException(String message)
    {
        super(message);
    }

    public CouldNotGetPublicIpException(Throwable cause)
    {
        super(cause);
    }
}
