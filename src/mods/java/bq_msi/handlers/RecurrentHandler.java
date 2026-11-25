package bq_msi.handlers;

import ivorius.reccomplex.events.StructureGenerationEventLite.Post;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecurrentHandler {
   public RecurrentHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onStructureGenerationLitePost(Post event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         StructureBoundingBox boundingBox = event.getBoundingBox();
         String structureName = event.getStructureName();
         String regionName = this.convertBoxToRegion(boundingBox);
         int minX = boundingBox.minX;
         int minZ = boundingBox.minZ;
         int maxX = boundingBox.maxX;
         int maxZ = boundingBox.maxZ;
         int dimension = world.provider.getDimension();
         DataWrite(regionName, dimension, structureName, minX, maxX, minZ, maxZ);
      }
   }

   public String convertBoxToRegion(StructureBoundingBox boundingBox) {
      int regionX = Math.round((float)((boundingBox.minX + boundingBox.maxX) / 1024));
      int regionZ = Math.round((float)((boundingBox.minZ + boundingBox.maxZ) / 1024));
      String regionName = "r." + Integer.toString(regionX) + "." + Integer.toString(regionZ);
      return regionName;
   }

   public static void DataWrite(String regionName, int dimension, String structureName, int minX, int maxX, int minZ, int maxZ) {
      File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "data" + File.separator + "QuestStructure" + File.separator + dimension + File.separator + regionName + ".txt");
      fileName.getParentFile().mkdirs();

      try {
         FileWriter fileWriter = new FileWriter(fileName, true);
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
         bufferedWriter.write(structureName + "," + minX + "," + maxX + "," + minZ + "," + maxZ);
         bufferedWriter.newLine();
         bufferedWriter.close();
      } catch (IOException var10) {
         var10.printStackTrace();
      }

   }
}
