package com.google.common.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class Files
{
    private Files()
    {
    }

    public static String toString(File file, Charset charset) throws IOException
    {
        return new String(java.nio.file.Files.readAllBytes(file.toPath()), charset);
    }

    public static void write(CharSequence data, File file, Charset charset) throws IOException
    {
        java.nio.file.Files.write(file.toPath(), data.toString().getBytes(charset));
    }

    public static List<String> readLines(File file, Charset charset) throws IOException
    {
        return java.nio.file.Files.readAllLines(file.toPath(), charset);
    }

    public static void copy(File from, File to) throws IOException
    {
        java.nio.file.Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
