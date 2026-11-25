package ivorius.reccomplex.world.gen.feature.villages;

import ivorius.ivtoolkit.lang.IvClasses;
import ivorius.reccomplex.RecurrentComplex;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;

public class VanillaGenerationClassFactory extends ClassLoader {
   private static final VanillaGenerationClassFactory INSTANCE = new VanillaGenerationClassFactory();
   protected Map<String, Class<?>> loaded = new HashMap();
   protected Set<String> failed = new HashSet();

   public VanillaGenerationClassFactory() {
      super(VanillaGenerationClassFactory.class.getClassLoader());
   }

   public static VanillaGenerationClassFactory instance() {
      return INSTANCE;
   }

   @Nullable
   public GenericVillagePiece create(String structureID, String generationID) {
      Class<? extends GenericVillagePiece> aClass = this.getClass(structureID, generationID);
      return aClass != null ? (GenericVillagePiece)IvClasses.instantiate(aClass) : null;
   }

   @Nullable
   public GenericVillagePiece create(String structureID, String generationID, Start start, int generationDepth) {
      Class<? extends GenericVillagePiece> aClass = this.getClass(structureID, generationID);
      if (aClass != null) {
         try {
            Constructor<? extends GenericVillagePiece> constructor = aClass.getConstructor(Start.class, Integer.TYPE);
            return (GenericVillagePiece)constructor.newInstance(start, generationDepth);
         } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException var7) {
            var7.printStackTrace();
         }
      }

      return null;
   }

   public Class<? extends GenericVillagePiece> getClass(String structureID, String generationID) {
      try {
         return this.findClass(this.classNameForStructure(structureID, generationID));
      } catch (ClassNotFoundException var4) {
         if (!this.failed.contains(structureID)) {
            Class<? extends GenericVillagePiece> created = this.createClass(structureID, generationID);
            if (created == null) {
               this.failed.add(structureID);
               RecurrentComplex.logger.error("Could not create vanilla generation type class for '" + structureID + "'");
            }

            return created;
         } else {
            return null;
         }
      }
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException {
      return this.loaded.containsKey(name) ? (Class)this.loaded.get(name) : super.findClass(name);
   }

   protected Class<?> define(String name, byte[] data) {
      Class<?> aClass = this.defineClass(name, data, 0, data.length);
      this.loaded.put(name, aClass);
      return aClass;
   }

   protected Class<? extends GenericVillagePiece> createClass(String structureID, String generationID) {
      try {
         String className = this.classNameForStructure(structureID, generationID);
         return this.define(className, this.createClassBinary(className.replaceAll("\\.", "/")));
      } catch (Exception var4) {
         RecurrentComplex.logger.error("Can't load dynamic piece class", var4);
         return null;
      }
   }

   protected String classNameForStructure(String structureID, String generationID) {
      return "ivorius.reccomplex.dynamic.vanillagen." + structureID + "_" + generationID;
   }

   protected byte[] createClassBinary(String className) {
      ClassWriter writer = new ClassWriter(0);
      ClassVisitor cw = new CheckClassAdapter(writer);
      cw.visit(49, 1, className, (String)null, Type.getInternalName(GenericVillagePiece.class), (String[])null);
      String descriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]);
      MethodVisitor mv = cw.visitMethod(1, "<init>", descriptor, (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(183, Type.getInternalName(GenericVillagePiece.class), "<init>", descriptor, false);
      mv.visitInsn(177);
      mv.visitMaxs(2, 1);
      mv.visitEnd();
      descriptor = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{Type.getType(Start.class), Type.INT_TYPE});
      mv = cw.visitMethod(1, "<init>", descriptor, (String)null, (String[])null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitVarInsn(25, 1);
      mv.visitVarInsn(21, 2);
      mv.visitMethodInsn(183, Type.getInternalName(GenericVillagePiece.class), "<init>", descriptor, false);
      mv.visitInsn(177);
      mv.visitMaxs(4, 3);
      mv.visitEnd();
      cw.visitEnd();
      return writer.toByteArray();
   }
}
