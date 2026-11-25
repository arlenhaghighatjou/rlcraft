package invtweaks;

import invtweaks.api.IItemTreeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class InvTweaksItemTreeLoader extends DefaultHandler {
   public static final String ATTR_ID = "id";
   public static final String ATTR_DAMAGE = "damage";
   public static final String ATTR_RANGE_DMIN = "dmin";
   public static final String ATTR_RANGE_DMAX = "dmax";
   public static final String ATTR_OREDICT_NAME = "oreDictName";
   public static final String ATTR_DATA = "data";
   public static final String ATTR_CLASS = "class";
   public static final String ATTR_LAST_ORDER = "mergePrevious";
   public static final String ATTR_MERGE_CHILDREN = "mergeChildren";
   public static final String ATTR_TREE_VERSION = "treeVersion";
   public static final String ATTR_TREE_ORDER = "treeOrder";
   private static final List<IItemTreeListener> onLoadListeners = new ArrayList();
   private static InvTweaksItemTree tree;
   @Nullable
   private static String treeVersion;
   private static int itemOrder;
   private static int mergeChildren;
   private static LinkedList<String> categoryStack;
   private static LinkedList<Boolean> mergeStack;
   private static boolean treeLoaded = false;

   public InvTweaksItemTreeLoader() {
   }

   private static void init() {
      treeVersion = null;
      tree = new InvTweaksItemTree();
      itemOrder = 0;
      mergeChildren = 0;
      categoryStack = new LinkedList();
      mergeStack = new LinkedList();
   }

   public static synchronized InvTweaksItemTree load(@NotNull File file) throws Exception {
      init();
      SAXParserFactory parserFactory = SAXParserFactory.newInstance();
      SAXParser parser = parserFactory.newSAXParser();
      parser.parse(file, new InvTweaksItemTreeLoader());
      synchronized(onLoadListeners) {
         treeLoaded = true;

         for(IItemTreeListener onLoadListener : onLoadListeners) {
            onLoadListener.onTreeLoaded(tree);
         }
      }

      MinecraftForge.EVENT_BUS.register(tree);
      return tree;
   }

   public static synchronized boolean isValidVersion(@NotNull File file) throws Exception {
      init();
      if (file.exists()) {
         treeVersion = null;
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         SAXParser parser = parserFactory.newSAXParser();
         InvTweaksItemTreeLoader.VersionLoader loader = new InvTweaksItemTreeLoader.VersionLoader();
         parser.parse(file, loader);
         return "1.12.2".equals(loader.version);
      } else {
         return false;
      }
   }

   public static synchronized void addOnLoadListener(@NotNull IItemTreeListener listener) {
      onLoadListeners.add(listener);
      if (treeLoaded) {
         listener.onTreeLoaded(tree);
      }

   }

   public static synchronized boolean removeOnLoadListener(IItemTreeListener listener) {
      return onLoadListeners.remove(listener);
   }

   private int getNextItemOrder(boolean lastOrder) {
      return (lastOrder || mergeChildren > 0) && itemOrder > 0 ? itemOrder - 1 : itemOrder++;
   }

   public synchronized void startElement(String uri, String localName, String name, @NotNull Attributes attributes) throws SAXException {
      String rangeDMinAttr = attributes.getValue("dmin");
      String newTreeVersion = attributes.getValue("treeVersion");
      String oreDictNameAttr = attributes.getValue("oreDictName");
      String id = attributes.getValue("id");
      String className = attributes.getValue("class");
      String lastOrderValue = attributes.getValue("mergePrevious");
      String mergeChildrenValue = attributes.getValue("mergeChildren");
      lastOrderValue = lastOrderValue == null ? "" : lastOrderValue.toLowerCase();
      boolean lastOrder = lastOrderValue.equals("1") || lastOrderValue.equals("true") || lastOrderValue.equals("yes") || lastOrderValue.equals("t") || lastOrderValue.equals("y");
      mergeChildrenValue = mergeChildrenValue == null ? "" : mergeChildrenValue.toLowerCase();
      boolean willMergeChildren = mergeChildrenValue.equals("1") || mergeChildrenValue.equals("true") || mergeChildrenValue.equals("yes") || mergeChildrenValue.equals("t") || mergeChildrenValue.equals("y");
      mergeStack.add(Boolean.valueOf(willMergeChildren));
      if (treeVersion == null) {
         treeVersion = newTreeVersion;
      }

      if (id != null) {
         int damage = 32767;
         String extraDataAttr = attributes.getValue("data");
         NBTTagCompound extraData = null;
         if (extraDataAttr != null) {
            try {
               extraData = JsonToNBT.getTagFromJson(extraDataAttr);
            } catch (NBTException var19) {
               throw new RuntimeException("Data attribute failed for tree entry '" + name + "'", var19);
            }
         }

         if (attributes.getValue("damage") != null) {
            damage = Integer.parseInt(attributes.getValue("damage"));
         }

         tree.addItem((String)categoryStack.getLast(), new InvTweaksItemTreeItem(name, id, damage, extraData, this.getNextItemOrder(lastOrder), String.join("\\", categoryStack) + "\\" + name));
      } else if (oreDictNameAttr != null) {
         tree.registerOre((String)categoryStack.getLast(), name, oreDictNameAttr, this.getNextItemOrder(lastOrder), String.join("\\", categoryStack) + "\\" + name);
      } else if (className != null) {
         String extraDataAttr = attributes.getValue("data");
         NBTTagCompound extraData = null;
         if (extraDataAttr != null) {
            try {
               extraData = JsonToNBT.getTagFromJson(extraDataAttr.toLowerCase());
            } catch (NBTException var18) {
               throw new RuntimeException("Data attribute failed for tree entry '" + name + "'", var18);
            }
         }

         tree.registerClass((String)categoryStack.getLast(), name, className.toLowerCase(), extraData, this.getNextItemOrder(lastOrder), String.join("\\", categoryStack) + "\\" + name);
      } else {
         if (categoryStack.isEmpty()) {
            tree.setRootCategory(new InvTweaksItemTreeCategory(name));
         } else {
            tree.addCategory((String)categoryStack.getLast(), new InvTweaksItemTreeCategory(name));
         }

         if (rangeDMinAttr != null) {
            int rangeDMin = Integer.parseInt(rangeDMinAttr);
            int rangeDMax = Integer.parseInt(attributes.getValue("dmax"));

            for(int damage = rangeDMin; damage <= rangeDMax; ++damage) {
               tree.addItem(name, new InvTweaksItemTreeItem(name + id + "-" + damage, id, damage, (NBTTagCompound)null, this.getNextItemOrder(lastOrder), String.join("\\", categoryStack) + "\\" + name));
            }
         } else if (willMergeChildren) {
            this.getNextItemOrder(lastOrder);
         }

         categoryStack.add(name);
      }

      if (willMergeChildren) {
         ++mergeChildren;
      }

   }

   public synchronized void endElement(String uri, String localName, @NotNull String name) throws SAXException {
      if (!categoryStack.isEmpty() && name.equals(categoryStack.getLast())) {
         categoryStack.removeLast();
      }

      if (!mergeStack.isEmpty()) {
         if (((Boolean)mergeStack.getLast()).booleanValue() && mergeChildren > 0) {
            --mergeChildren;
         }

         mergeStack.removeLast();
      }

   }

   public void endDocument() throws SAXException {
      tree.endFileRead();
   }

   public void warning(SAXParseException e) throws SAXException {
      InvTweaks.log.warn("Tree XML Warning: ", e);
   }

   public void error(SAXParseException e) throws SAXException {
      InvTweaks.log.error("Tree XML Error: ", e);
   }

   public void fatalError(SAXParseException e) throws SAXException {
      InvTweaks.log.fatal("Tree XML Fatal Error: ", e);
   }

   private static class VersionLoader extends DefaultHandler {
      @Nullable
      String version;

      private VersionLoader() {
      }

      public synchronized void startElement(String uri, String localName, String name, @NotNull Attributes attributes) throws SAXException {
         if (this.version == null) {
            this.version = attributes.getValue("treeVersion");
         }

      }
   }
}
