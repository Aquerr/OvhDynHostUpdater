package pl.bartlomiejstepien.ovhdynhostupdater.config;

public class DynHost
{
    private String username;
    private String password;
    private String hostName;

    public DynHost(String username, String password, String hostName)
    {
        this.username = username;
        this.password = password;
        this.hostName = hostName;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
}
