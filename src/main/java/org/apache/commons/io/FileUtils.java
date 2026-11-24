package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public final class FileUtils
{
    private FileUtils()
    {
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive)
    {
        ArrayList<File> result = new ArrayList<File>();
        collect(directory, extensions, recursive, result);
        return result;
    }

    private static void collect(File directory, String[] extensions, boolean recursive, ArrayList<File> result)
    {
        File[] files = directory.listFiles();
        if(files == null)
        {
            return;
        }
        for(int i = 0; i < files.length; ++i)
        {
            File file = files[i];
            if(file.isDirectory())
            {
                if(recursive)
                {
                    collect(file, extensions, true, result);
                }
            }
            else
            {
                String extension = FilenameUtils.getExtension(file.getName());
                for(int j = 0; j < extensions.length; ++j)
                {
                    if(extensions[j].equals(extension))
                    {
                        result.add(file);
                        break;
                    }
                }
            }
        }
    }

    public static String readFileToString(File file) throws IOException
    {
        return readFileToString(file, StandardCharsets.UTF_8);
    }

    public static String readFileToString(File file, Charset charset) throws IOException
    {
        return new String(java.nio.file.Files.readAllBytes(file.toPath()), charset);
    }

    public static void writeStringToFile(File file, String data) throws IOException
    {
        java.nio.file.Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8));
    }
}
