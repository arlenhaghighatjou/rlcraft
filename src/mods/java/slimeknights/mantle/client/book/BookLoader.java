package slimeknights.mantle.client.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.action.StringActionProcessor;
import slimeknights.mantle.client.book.action.protocol.ProtocolGoToPage;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.ContentBlank;
import slimeknights.mantle.client.book.data.content.ContentBlockInteraction;
import slimeknights.mantle.client.book.data.content.ContentCrafting;
import slimeknights.mantle.client.book.data.content.ContentImage;
import slimeknights.mantle.client.book.data.content.ContentImageText;
import slimeknights.mantle.client.book.data.content.ContentSmelting;
import slimeknights.mantle.client.book.data.content.ContentSmithing;
import slimeknights.mantle.client.book.data.content.ContentStructure;
import slimeknights.mantle.client.book.data.content.ContentText;
import slimeknights.mantle.client.book.data.content.ContentTextImage;
import slimeknights.mantle.client.book.data.content.ContentTextLeftImage;
import slimeknights.mantle.client.book.data.content.ContentTextRightImage;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.deserializer.HexStringDeserializer;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.mantle.network.book.PacketUpdateSavedPage;

@SideOnly(Side.CLIENT)
public class BookLoader implements IResourceManagerReloadListener {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Integer.TYPE, new HexStringDeserializer()).create();
   private static final HashMap<String, Class<? extends PageContent>> typeToContentMap = new HashMap();
   private static final HashMap<String, BookData> books = new HashMap();
   private static final NetworkWrapper wrapper = new NetworkWrapper("mantle:books");

   public BookLoader() {
      wrapper.registerPacketServer(PacketUpdateSavedPage.class);
      registerPageType("blank", ContentBlank.class);
      registerPageType("text", ContentText.class);
      registerPageType("image", ContentImage.class);
      registerPageType("image with text below", ContentImageText.class);
      registerPageType("text with image below", ContentTextImage.class);
      registerPageType("text with left image etch", ContentTextLeftImage.class);
      registerPageType("text with right image etch", ContentTextRightImage.class);
      registerPageType("crafting", ContentCrafting.class);
      registerPageType("smelting", ContentSmelting.class);
      registerPageType("smithing", ContentSmithing.class);
      registerPageType("block interaction", ContentBlockInteraction.class);
      registerPageType("structure", ContentStructure.class);
      StringActionProcessor.registerProtocol(new ProtocolGoToPage());
      StringActionProcessor.registerProtocol(new ProtocolGoToPage(true, "go-to-page-rtn"));
   }

   public static void registerPageType(String name, Class<? extends PageContent> clazz) {
      if (typeToContentMap.containsKey(name)) {
         throw new IllegalArgumentException("Page type " + name + " already in use.");
      } else {
         typeToContentMap.put(name, clazz);
      }
   }

   public static Class<? extends PageContent> getPageType(String name) {
      return (Class)typeToContentMap.get(name);
   }

   public static BookData registerBook(String name, BookRepository... repositories) {
      return registerBook(name, true, true, repositories);
   }

   public static BookData registerBook(String name, boolean appendIndex, boolean appendContentTable, BookRepository... repositories) {
      BookData info = new BookData(repositories);
      books.put(name.contains(":") ? name : Loader.instance().activeModContainer().getModId() + ":" + name, info);
      if (appendIndex) {
         info.addTransformer(BookTransformer.IndexTranformer());
      }

      if (appendContentTable) {
         info.addTransformer(BookTransformer.contentTableTransformer());
      }

      return info;
   }

   public static void updateSavedPage(EntityPlayer player, ItemStack item, String page) {
      if (player != null) {
         if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            BookHelper.writeSavedPage(item, page);
            wrapper.network.sendToServer(new PacketUpdateSavedPage(page));
         }
      }
   }

   public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
      books.forEach((s, bookData) -> {
         bookData.reset();
      });
   }
}
