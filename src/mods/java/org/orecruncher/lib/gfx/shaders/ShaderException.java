package org.orecruncher.lib.gfx.shaders;

public class ShaderException extends Exception {
   private static final long serialVersionUID = -1543373731942335193L;

   ShaderException(String txt) {
      super(txt);
   }

   ShaderException(String shaderName, String txt) {
      super(shaderName + ": " + txt);
   }
}
