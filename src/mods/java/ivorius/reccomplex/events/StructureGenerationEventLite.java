package ivorius.reccomplex.events;

import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class StructureGenerationEventLite extends WorldEvent {
   @Nonnull
   protected final String structureName;
   @Nonnull
   protected final StructureBoundingBox boundingBox;
   protected final int generationLayer;
   protected final boolean firstTime;

   public StructureGenerationEventLite(World world, @Nonnull String structureName, @Nonnull StructureBoundingBox boundingBox, int generationLayer, boolean firstTime) {
      super(world);
      this.structureName = structureName;
      this.boundingBox = boundingBox;
      this.generationLayer = generationLayer;
      this.firstTime = firstTime;
   }

   @Nonnull
   public String getStructureName() {
      return this.structureName;
   }

   @Nonnull
   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getGenerationLayer() {
      return this.generationLayer;
   }

   public boolean isFirstTime() {
      return this.firstTime;
   }

   public static class Post extends StructureGenerationEventLite {
      public Post(World world, @Nonnull String structureName, @Nonnull StructureBoundingBox boundingBox, int generationLayer, boolean firstTime) {
         super(world, structureName, boundingBox, generationLayer, firstTime);
      }
   }

   public static class Pre extends StructureGenerationEventLite {
      public Pre(World world, @Nonnull String structureName, @Nonnull StructureBoundingBox boundingBox, int generationLayer, boolean firstTime) {
         super(world, structureName, boundingBox, generationLayer, firstTime);
      }
   }

   @Cancelable
   public static class Suggest extends StructureGenerationEventLite {
      public Suggest(World world, @Nonnull String structureName, @Nonnull StructureBoundingBox boundingBox, int generationLayer, boolean firstTime) {
         super(world, structureName, boundingBox, generationLayer, firstTime);
      }
   }
}
