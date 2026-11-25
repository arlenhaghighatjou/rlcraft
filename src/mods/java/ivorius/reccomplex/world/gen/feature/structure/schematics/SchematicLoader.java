package ivorius.reccomplex.world.gen.feature.structure.schematics;

import ivorius.ivtoolkit.tools.IvFileHelper;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.utils.accessor.RCAccessorNBT;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class SchematicLoader {
   public SchematicLoader() {
   }

   public static void initializeFolder() {
      getValidatedSchematicsFile();
   }

   public static SchematicFile loadSchematicByName(String name) throws SchematicFile.UnsupportedSchematicFormatException {
      if (FilenameUtils.getExtension(name).length() == 0) {
         name = name + ".schematic";
      }

      for(File file : currentSchematicFiles()) {
         if (file.getPath().endsWith(name) && name.endsWith(file.getName())) {
            return loadSchematicFromFile(file);
         }
      }

      return null;
   }

   public static SchematicFile loadSchematicFromFile(File file) throws SchematicFile.UnsupportedSchematicFormatException {
      NBTTagCompound compound = null;
      Exception exception = null;

      try {
         FileInputStream fileInputStream = new FileInputStream(file);
         Throwable var4 = null;

         try {
            compound = CompressedStreamTools.readCompressed(fileInputStream);
         } catch (Throwable var32) {
            var4 = var32;
            throw var32;
         } finally {
            if (fileInputStream != null) {
               if (var4 != null) {
                  try {
                     fileInputStream.close();
                  } catch (Throwable var31) {
                     var4.addSuppressed(var31);
                  }
               } else {
                  fileInputStream.close();
               }
            }

         }
      } catch (Exception var36) {
         exception = var36;
      }

      if (compound == null) {
         try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable var38 = null;

            try {
               DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(fileInputStream));
               compound = CompressedStreamTools.read(datainputstream);
            } catch (Throwable var30) {
               var38 = var30;
               throw var30;
            } finally {
               if (fileInputStream != null) {
                  if (var38 != null) {
                     try {
                        fileInputStream.close();
                     } catch (Throwable var29) {
                        var38.addSuppressed(var29);
                     }
                  } else {
                     fileInputStream.close();
                  }
               }

            }
         } catch (Exception var34) {
            throw new SchematicFile.UnsupportedSchematicFormatException(exception, "Not a compressed NBT file");
         }
      }

      return compound != null ? new SchematicFile(compound) : null;
   }

   public static void writeSchematicByName(SchematicFile schematic, String name) {
      writeSchematicToFile(schematic, new File(getValidatedSchematicsFile(), name + ".schematic"));
   }

   public static void writeSchematicToFile(SchematicFile schematic, File file) {
      NBTTagCompound compound = new NBTTagCompound();
      schematic.writeToNBT(compound);

      try {
         DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
         Throwable var4 = null;

         try {
            RCAccessorNBT.writeEntry("Schematic", compound, dataOutputStream);
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (dataOutputStream != null) {
               if (var4 != null) {
                  try {
                     dataOutputStream.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  dataOutputStream.close();
               }
            }

         }
      } catch (IOException var16) {
         var16.printStackTrace();
      }

   }

   public static List<String> currentSchematicFileNames() {
      return (List)currentSchematicFiles().stream().map((file) -> {
         return FilenameUtils.getBaseName(file.getName());
      }).collect(Collectors.toList());
   }

   public static Collection<File> currentSchematicFiles() {
      File schematicsFile = getValidatedSchematicsFile();
      return (Collection<File>)(schematicsFile != null ? FileUtils.listFiles(schematicsFile, new String[]{"schematic"}, true) : Collections.emptyList());
   }

   public static String getLookupFolderName() {
      return "structures/schematics";
   }

   public static File getValidatedSchematicsFile() {
      File structuresFile = IvFileHelper.getValidatedFolder(new File(RecurrentComplex.proxy.getDataDirectory(), "structures"));
      return structuresFile != null ? IvFileHelper.getValidatedFolder(structuresFile, "schematics") : null;
   }
}
