package pl.bartlomiejstepien.ovhdynhostupdater.ovh.client;

public class DynHostUpdateResponse
{
    private final int statusCode;
    private final String message;

    public DynHostUpdateResponse(int statusCode, String message)
    {
        this.statusCode = statusCode;
        this.message = message;
    }

    public boolean isOk()
    {
        //TODO: Dodać akceptowalne statusy: 200, 404
        return this.statusCode == 200;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getMessage()
    {
        return message;
    }
}
