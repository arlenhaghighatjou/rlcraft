package ivorius.reccomplex;

import com.google.common.base.Strings;
import ivorius.ivtoolkit.tools.IvFMLIntercommHandler;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.reccomplex.dimensions.DimensionDictionary;
import ivorius.reccomplex.files.loading.FileLoadContext;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import org.apache.logging.log4j.Logger;

public class RCCommunicationHandler extends IvFMLIntercommHandler {
   public RCCommunicationHandler(Logger logger, String modOwnerID, Object modInstance) {
      super(logger, modOwnerID, modInstance);
   }

   protected boolean handleMessage(IMCMessage message, boolean server, boolean runtime) {
      if (!this.isMessage("loadFile", message, NBTTagCompound.class) && !this.isMessage("loadInventoryGenerator", message, NBTTagCompound.class)) {
         if (this.isMessage("registerDimension", message, NBTTagCompound.class)) {
            NBTTagCompound cmp = message.getNBTValue();
            int dimensionID = cmp.getInteger("dimensionID");
            String[] types = IvNBTHelper.readNBTStrings("types", cmp);
            if (types != null) {
               DimensionDictionary.registerDimensionTypes(dimensionID, Arrays.asList(types));
            } else {
               this.getLogger().warn("Could not handle message with key '" + message.key + "' - missing 'types' key!");
            }

            return true;
         } else if (this.isMessage("unregisterDimension", message, NBTTagCompound.class)) {
            NBTTagCompound cmp = message.getNBTValue();
            int dimensionID = cmp.getInteger("dimensionID");
            String[] types = IvNBTHelper.readNBTStrings("types", cmp);
            if (types != null) {
               DimensionDictionary.unregisterDimensionTypes(dimensionID, Arrays.asList(types));
            } else {
               DimensionDictionary.unregisterDimensionTypes(dimensionID, (Collection)null);
            }

            return true;
         } else if (this.isMessage("registerDimensionType", message, String.class)) {
            DimensionDictionary.registerType(message.getStringValue());
            return true;
         } else if (this.isMessage("registerDimensionSubtypes", message, NBTTagCompound.class)) {
            NBTTagCompound cmp = message.getNBTValue();
            String type = cmp.getString("type");
            String[] subtypes = IvNBTHelper.readNBTStrings("subtypes", cmp);
            if (!Strings.isNullOrEmpty(type)) {
               DimensionDictionary.registerSubtypes(type, Arrays.asList(subtypes));
            } else {
               this.getLogger().warn("Could not handle message with key '" + message.key + "' - missing 'subtypes' key!");
            }

            return true;
         } else if (this.isMessage("registerDimensionSupertypes", message, NBTTagCompound.class)) {
            NBTTagCompound cmp = message.getNBTValue();
            String type = cmp.getString("type");
            String[] subtypes = IvNBTHelper.readNBTStrings("supertypes", cmp);
            if (!Strings.isNullOrEmpty(type)) {
               DimensionDictionary.registerSupertypes(type, Arrays.asList(subtypes));
            } else {
               this.getLogger().warn("Could not handle message with key '" + message.key + "' - missing 'supertypes' key!");
            }

            return true;
         } else {
            if (this.isMessage("registerLegacyBlockIds", message, NBTTagCompound.class)) {
               NBTTagCompound cmp = message.getNBTValue();
               Block block = RecurrentComplex.specialRegistry.blockFromID(new ResourceLocation(cmp.getString("block")));
               if (block != null) {
                  boolean inferItem = cmp.getBoolean("inferItem");
                  String[] legacyIDs = IvNBTHelper.readNBTStrings("legacyIDs", cmp);
                  RecurrentComplex.cremapper.registerLegacyIDs(block, inferItem, legacyIDs);
               } else {
                  this.getLogger().warn("Could not handle message with key '" + message.key + "' - could not find block!");
               }
            } else if (this.isMessage("registerLegacyItemIds", message, NBTTagCompound.class)) {
               NBTTagCompound cmp = message.getNBTValue();
               Item item = RecurrentComplex.specialRegistry.itemFromID(new ResourceLocation(cmp.getString("item")));
               if (item != null) {
                  String[] legacyIDs = IvNBTHelper.readNBTStrings("legacyIDs", cmp);
                  RecurrentComplex.cremapper.registerLegacyIDs(item, legacyIDs);
               } else {
                  this.getLogger().warn("Could not handle message with key '" + message.key + "' - could not find item!");
               }
            }

            return false;
         }
      } else {
         NBTTagCompound cmp = message.getNBTValue();
         String genPath = cmp.getString("genPath");
         String genID = cmp.hasKey("genID", 8) ? cmp.getString("genID") : null;
         boolean generates = cmp.getBoolean("generates");
         ResourceLocation resourceLocation = new ResourceLocation(genPath);
         RecurrentComplex.loader.tryLoad(resourceLocation, genID, new FileLoadContext(resourceLocation.getResourceDomain(), generates, LeveledRegistry.Level.INTERNAL));
         return true;
      }
   }
}
