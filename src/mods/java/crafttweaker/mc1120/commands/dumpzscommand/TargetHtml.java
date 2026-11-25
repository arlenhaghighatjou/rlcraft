package crafttweaker.mc1120.commands.dumpzscommand;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import stanhebben.zenscript.dump.TreeNode;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.Pair;

public class TargetHtml extends DumpZsTarget {
   private static final String HTML_HEADER = "<!DOCTYPE html>\n<head><link rel=\"stylesheet\" href=\"tree3.css\"> <title>CraftTweaker: ZenScript Language Dump</title></head>";
   private static final String HTML_BODY_START = "<p>CraftTweaker ZenScript language, refer to <a href=\"http://crafttweaker.readthedocs.io/\" target=\"_blank\">this page</a> for more help.</p>";
   private static final String HTML_DIV_START = "<div class=\"css-treeview\"><ul>";
   private static final String HTML_DIV_END = "</ul></div>";
   private static final String HTML_BODY_END = "<p>This File was created using the dumpzs command.</p>";
   private static final Comparator<TreeNode<String>> STRING_TREE_COMPARATOR = (o1, o2) -> {
      return ((String)o1.data).compareToIgnoreCase((String)o2.data);
   };
   private int itemCounter = 0;

   public TargetHtml() {
      super("html");
   }

   public String getDescription() {
      return "Dumps to a local HTML file";
   }

   public void execute(ICommandSender sender, MinecraftServer Server) {
      TreeNode<String> root = new TreeNode<String>("root");
      File zsDataFolder = new File("crafttweaker_dump");
      if (!zsDataFolder.exists()) {
         zsDataFolder.mkdirs();
      }

      URL inputUrlPNG = this.getClass().getResource("/assets/crafttweaker/icons.png");

      try {
         FileUtils.copyURLToFile(inputUrlPNG, new File(zsDataFolder, "icons.png"));
      } catch (IOException var16) {
         CraftTweaker.LOG.catching(var16);
      }

      URL inputUrlCSS = this.getClass().getResource("/assets/crafttweaker/tree3.css");

      try {
         FileUtils.copyURLToFile(inputUrlCSS, new File(zsDataFolder, "tree3.css"));
      } catch (IOException var15) {
         CraftTweaker.LOG.catching(var15);
      }

      TreeNode<String> bracketNode = root.addChild("Bracket Handlers");

      for(Pair<Integer, IBracketHandler> pair : GlobalRegistry.getPrioritizedBracketHandlers()) {
         bracketNode.addChild(((IBracketHandler)pair.getValue()).getClass().getName() + ", priority: " + pair.getKey());
      }

      TreeNode<String> types = root.addChild("Types");
      GlobalRegistry.getTypes().getTypeMap().forEach((aClass, zenType) -> {
         TreeNode<String> zsType = types.addChild(zenType.getName());
         zsType.addChild(aClass.getName());
      });
      types.children.sort(STRING_TREE_COMPARATOR);
      TreeNode<String> globals = root.addChild("Globals");
      GlobalRegistry.getGlobals().forEach((s, iZenSymbol) -> {
         TreeNode<String> globalName = globals.addChild(s);
         globalName.addChild(iZenSymbol.toString());
      });
      globals.children.sort(STRING_TREE_COMPARATOR);
      TreeNode<String> expansions = root.addChild("Expansions");
      GlobalRegistry.getExpansions().forEach((s, typeExpansion) -> {
         TreeNode<String> exp = expansions.addChild(s);
         exp.addChild(typeExpansion.getClass().getName());
      });
      expansions.children.sort(STRING_TREE_COMPARATOR);
      TreeNode<String> symbols = root.addChild("Root (Symbol Package)");
      GlobalRegistry.getRoot().getPackages().forEach((s, zenSymbol) -> {
         this.printZenSymbol(s, zenSymbol, symbols);
      });
      this.sortTreeNodes(symbols);

      try {
         List<String> lines = Arrays.asList("<!DOCTYPE html>\n<head><link rel=\"stylesheet\" href=\"tree3.css\"> <title>CraftTweaker: ZenScript Language Dump</title></head>", "<p>CraftTweaker ZenScript language, refer to <a href=\"http://crafttweaker.readthedocs.io/\" target=\"_blank\">this page</a> for more help.</p>", "<div class=\"css-treeview\"><ul>", this.convertToFlatHTMLStrings(root), "</ul></div>", "<p>This File was created using the dumpzs command.</p>");
         Path file = Paths.get(zsDataFolder.getAbsolutePath() + "/tree3.html");
         Files.write(file, lines, Charset.forName("UTF-8"));
      } catch (IOException var14) {
         CraftTweaker.LOG.catching(var14);
      }

      sender.sendMessage(SpecialMessagesChat.getFileOpenText("Dumped content of the GlobalRegistry to a html file §r[§6Click here to open§r]", zsDataFolder.getAbsolutePath() + "/tree3.html"));
   }

   private void sortTreeNodes(TreeNode<String> parentNode) {
      if (!parentNode.isLeaf()) {
         parentNode.children.sort(STRING_TREE_COMPARATOR);

         for(TreeNode<String> child : parentNode.children) {
            this.sortTreeNodes(child);
         }

      }
   }

   private String convertToFlatHTMLStrings(TreeNode<String> root) {
      this.itemCounter = 0;
      StringBuilder sb = new StringBuilder();

      for(TreeNode<String> child : root.children) {
         sb.append(this.createListBodyString(child));
         sb.append("\n");
      }

      return sb.toString();
   }

   private String createListBodyString(TreeNode<String> node) {
      if (node.isLeaf()) {
         return this.createLeaveString(node);
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("<li><input type=\"checkbox\" id=\"item-").append(this.itemCounter).append("\" /><label for=\"item-").append(this.itemCounter).append("\">").append((String)node.data).append("</label><ul>");
         ++this.itemCounter;

         for(TreeNode<String> child : node.children) {
            sb.append(this.createListBodyString(child));
         }

         sb.append("</ul></li>");
         return sb.toString();
      }
   }

   private String createLeaveString(TreeNode<String> node) {
      return node.isLeaf() ? "<li>" + (String)node.data + "</li>" : "";
   }

   private void printZenSymbol(String s, IZenSymbol zenSymbol, TreeNode<String> node) {
      if (zenSymbol instanceof SymbolPackage) {
         this.printZenSymbolHelper(zenSymbol, node);
      } else {
         TreeNode<String> n = node.addChild(s);
         n.addChild(zenSymbol.toString());
      }

   }

   private void printZenSymbolHelper(IZenSymbol zenSymbol, TreeNode<String> node) {
      if (zenSymbol instanceof SymbolPackage) {
         SymbolPackage symbolPackage = (SymbolPackage)zenSymbol;
         symbolPackage.getPackages().forEach((s1, symbol) -> {
            TreeNode<String> childNode = this.addToTree(symbol, node);
            this.printZenSymbolHelper(symbol, childNode);
         });
      } else if (zenSymbol instanceof SymbolType || zenSymbol instanceof ZenTypeNative) {
         ZenTypeNative typeNative = null;
         if (zenSymbol instanceof SymbolType) {
            ZenType type = ((SymbolType)zenSymbol).getType();
            if (type instanceof ZenTypeNative) {
               typeNative = (ZenTypeNative)type;
            }
         } else {
            typeNative = (ZenTypeNative)zenSymbol;
         }

         if (typeNative != null) {
            this.addTypeNativeToTree(typeNative, node);
         }
      }

   }

   public void addTypeNativeToTree(ZenTypeNative typeNative, TreeNode<String> node) {
      typeNative.getMembers().forEach((s, zenNativeMember) -> {
         TreeNode<String> chNode = node.addChild(s);

         for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
            TreeNode<String> n = chNode.addChild("Java Method");
            n.addChild(this.methodToString(iJavaMethod));
         }

         if (zenNativeMember.getGetter() != null) {
            TreeNode<String> n = chNode.addChild("Getter");
            n.addChild(this.methodToString(zenNativeMember.getGetter()));
         }

         if (zenNativeMember.getSetter() != null) {
            TreeNode<String> n = chNode.addChild("Setter");
            n.addChild(this.methodToString(zenNativeMember.getSetter()));
         }

      });
      typeNative.getStaticMembers().forEach((s, zenNativeMember) -> {
         TreeNode<String> chNode = node.addChild("[STATIC] " + s);

         for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
            TreeNode<String> n = chNode.addChild("Java Method");
            n.addChild(this.methodToString(iJavaMethod));
         }

         if (zenNativeMember.getGetter() != null) {
            TreeNode<String> n = chNode.addChild("Getter");
            n.addChild(this.methodToString(zenNativeMember.getGetter()));
         }

         if (zenNativeMember.getSetter() != null) {
            TreeNode<String> n = chNode.addChild("Setter");
            n.addChild(this.methodToString(zenNativeMember.getSetter()));
         }

      });
   }

   private String methodToString(IJavaMethod javaMethod) {
      if (javaMethod == null) {
         return "";
      } else if (!(javaMethod instanceof JavaMethod)) {
         return javaMethod.toString();
      } else {
         Method jm = ((JavaMethod)javaMethod).getMethod();
         StringBuilder sb = new StringBuilder();
         ZenDoc[] doc = (ZenDoc[])jm.getAnnotationsByType(ZenDoc.class);
         if (doc.length > 0) {
            sb.append(this.encaplseInSpan("########", "green"));

            for(ZenDoc zenDoc : doc) {
               sb.append("<br/>");
               sb.append(this.encaplseInSpan(zenDoc.value(), "green"));
            }

            sb.append(this.encaplseInSpan("<br/>########<br/>", "green"));
         }

         sb.append(Modifier.toString(jm.getModifiers()));
         sb.append(" ");
         sb.append(this.createHoverText(jm.getReturnType().getName(), jm.getReturnType().getSimpleName()));
         sb.append(" ");
         sb.append(this.encaplseInSpan(jm.getName(), "red"));
         sb.append("(");
         Class<?>[] paras = jm.getParameterTypes();
         Annotation[][] annos = jm.getParameterAnnotations();

         for(int i = 0; i < paras.length; ++i) {
            for(int j = 0; j < annos[i].length; ++j) {
               sb.append(this.encaplseInSpan("@" + annos[i][j].annotationType().getSimpleName() + " ", "gold"));
            }

            sb.append(this.createHoverText(paras[i].getName(), paras[i].getSimpleName()));
            if (i != paras.length - 1) {
               sb.append(", ");
            }
         }

         sb.append(")");
         return sb.toString();
      }
   }

   private String colorClassName(String name) {
      int lastDot = name.lastIndexOf(".");
      return lastDot > 0 ? name.substring(0, lastDot + 1) + this.encaplseInSpan(name.substring(lastDot + 1, name.length()), "DarkOrange") : this.encaplseInSpan(name, "DarkOrange");
   }

   private String encaplseInSpan(String value, String color) {
      return "<span style=\"color:" + color + "\">" + value + "</span>";
   }

   private String createHoverText(String hoverText, String mainText) {
      return "<span title=\"" + hoverText + "\">" + mainText + "</span>";
   }

   private TreeNode<String> addToTree(IZenSymbol symbol, TreeNode<String> node) {
      String name;
      if (symbol instanceof SymbolPackage) {
         name = null;
      } else if (symbol instanceof SymbolType) {
         name = ((SymbolType)symbol).getType().getName();
      } else {
         name = symbol.toString();
      }

      if (name != null) {
         String[] split = name.split("\\.");

         for(String s : split) {
            TreeNode<String> childNode = node.findTreeNode(s);
            if (childNode == null) {
               childNode = node.addChild(s);
            }

            node = childNode;
         }
      }

      return node;
   }
}
