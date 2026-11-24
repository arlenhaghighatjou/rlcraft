package org.apache.commons.io;

public final class FilenameUtils
{
    private FilenameUtils()
    {
    }

    public static String getExtension(String name)
    {
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        int dot = name.lastIndexOf('.');
        return dot > slash ? name.substring(dot + 1) : "";
    }

    public static String removeExtension(String name)
    {
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        int dot = name.lastIndexOf('.');
        return dot > slash ? name.substring(0, dot) : name;
    }
}
