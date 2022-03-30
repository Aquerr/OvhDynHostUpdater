package pl.bartlomiejstepien.ovhdynhostupdater.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileHandler
{
    private static final File DYN_HOST_FILE = new File("last_public_ip.txt");

    public String getLastPublicIp()
    {
        createFileIfNotExists(DYN_HOST_FILE);

        try
        {
            return Files.readString(DYN_HOST_FILE.toPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    private void createFileIfNotExists(File file)
    {
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setLastPublicIp(String publicIp)
    {
        try
        {
            Files.writeString(DYN_HOST_FILE.toPath(), publicIp, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
