/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * RuntimeException that gives subclasses the simple opportunity to write extra data when printing the stack trace.
 * Mainly a helper class as printsStackTrace has multiple signatures.
 */
public abstract class EnhancedRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public EnhancedRuntimeException() { super(); }
    public EnhancedRuntimeException(String message) { super(message); }
    public EnhancedRuntimeException(String message, Throwable cause) { super(message, cause); }
    public EnhancedRuntimeException(Throwable cause) { super(cause); }

    @Override
    public void printStackTrace(final PrintWriter s)
    {
        printStackTrace(new WrappedPrintStream()
        {
            @Override
            public void println(String line)
            {
                s.println(line);
            }
        });
        super.printStackTrace(s);
    }
    @Override
    public void printStackTrace(final PrintStream s)
    {
        printStackTrace(new WrappedPrintStream()
        {
            @Override
            public void println(String line)
            {
                s.println(line);
            }
        });
        super.printStackTrace(s);
    }

    protected abstract void printStackTrace(WrappedPrintStream stream);

    public static abstract class WrappedPrintStream
    {
        public abstract void println(String line);
    }
}
