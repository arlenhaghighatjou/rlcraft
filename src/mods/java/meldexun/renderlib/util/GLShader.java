package meldexun.renderlib.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLShader {
   private static final IntList PROGRAM_STACK = new IntArrayList();
   private int program;
   private final Object2IntMap<String> uniforms = new Object2IntOpenHashMap();
   private final Object2IntMap<String> attributes = new Object2IntOpenHashMap();

   public GLShader(int program) {
      this.program = program;
   }

   public static void push() {
      PROGRAM_STACK.add(GL11.glGetInteger(35725));
   }

   public static void pop() {
      use(PROGRAM_STACK.removeInt(PROGRAM_STACK.size() - 1));
   }

   public static void use(int program) {
      GL20.glUseProgram(program);
   }

   public int getProgram() {
      return this.program;
   }

   public void use() {
      use(this.program);
   }

   public int getUniform(String uniform) {
      return ((Integer)this.uniforms.computeIfAbsent(uniform, (k) -> {
         return GL20.glGetUniformLocation(this.program, k);
      })).intValue();
   }

   public int getAttribute(String attribute) {
      return ((Integer)this.attributes.computeIfAbsent(attribute, (k) -> {
         return GL20.glGetAttribLocation(this.program, k);
      })).intValue();
   }

   public void dispose() {
      GL20.glDeleteProgram(this.program);
      this.program = -1;
   }

   public static class Builder {
      private final Int2ObjectMap<Supplier<String>> shaderMap = new Int2ObjectOpenHashMap();
      private final Object2IntMap<String> attributeMap = new Object2IntOpenHashMap();

      public Builder() {
      }

      public GLShader.Builder addShader(int type, Supplier<String> source) {
         this.shaderMap.put(type, source);
         return this;
      }

      public GLShader.Builder bindAttribute(String attribute, int index) {
         this.attributeMap.put(attribute, index);
         return this;
      }

      public GLShader build() {
         int program = GL20.glCreateProgram();
         IntList shaderList = new IntArrayList();
         ObjectIterator linkStatus = this.shaderMap.int2ObjectEntrySet().iterator();

         while(linkStatus.hasNext()) {
            Entry<Supplier<String>> entry = (Entry)linkStatus.next();
            int shader = GL20.glCreateShader(entry.getIntKey());
            GL20.glShaderSource(shader, (CharSequence)((Supplier)entry.getValue()).get());
            GL20.glCompileShader(shader);
            int compileStatus = GL20.glGetShaderi(shader, 35713);
            if (compileStatus != 1) {
               int logLength = GL20.glGetShaderi(shader, 35716);
               String shaderInfoLog = GL20.glGetShaderInfoLog(shader, logLength);
               throw new RuntimeException(String.format("Failed to compile shader: %d%n%s", compileStatus, shaderInfoLog));
            }

            shaderList.add(shader);
         }

         shaderList.forEach((shader) -> {
            GL20.glAttachShader(program, shader.intValue());
         });
         this.attributeMap.forEach((attribute, index) -> {
            GL20.glBindAttribLocation(program, index.intValue(), attribute);
         });
         GL20.glLinkProgram(program);
         int linkStatus = GL20.glGetProgrami(program, 35714);
         if (linkStatus != 1) {
            int logLength = GL20.glGetProgrami(program, 35716);
            String programInfoLog = GL20.glGetProgramInfoLog(program, logLength);
            throw new RuntimeException(String.format("Failed to link program: %d%n%s", linkStatus, programInfoLog));
         } else {
            shaderList.forEach(GL20::glDeleteShader);
            return new GLShader(program);
         }
      }

      public GLShader build(Consumer<GLShader> callback) {
         GLShader shader = this.build();
         GLShader.push();
         shader.use();
         callback.accept(shader);
         GLShader.pop();
         return shader;
      }
   }
}
