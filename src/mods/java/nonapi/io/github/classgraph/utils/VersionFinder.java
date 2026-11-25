package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraph;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

public final class VersionFinder {
   private static final String MAVEN_PACKAGE = "io.github.classgraph";
   private static final String MAVEN_ARTIFACT = "classgraph";
   public static final VersionFinder.OperatingSystem OS;
   public static final String JAVA_VERSION = getProperty("java.version");
   public static final int JAVA_MAJOR_VERSION;
   public static final int JAVA_MINOR_VERSION;
   public static final int JAVA_SUB_VERSION;
   public static final boolean JAVA_IS_EA_VERSION;

   private VersionFinder() {
   }

   public static String getProperty(String propName) {
      try {
         return System.getProperty(propName);
      } catch (SecurityException var2) {
         return null;
      }
   }

   public static String getProperty(String propName, String defaultVal) {
      try {
         return System.getProperty(propName, defaultVal);
      } catch (SecurityException var3) {
         return null;
      }
   }

   public static synchronized String getVersion() {
      Class<?> cls = ClassGraph.class;

      try {
         String className = cls.getName();
         URL classpathResource = cls.getResource("/" + JarUtils.classNameToClassfilePath(className));
         if (classpathResource != null) {
            Path absolutePackagePath = Paths.get(classpathResource.toURI()).getParent();
            int packagePathSegments = className.length() - className.replace(".", "").length();
            Path path = absolutePackagePath;

            for(int i = 0; i < packagePathSegments && path != null; ++i) {
               path = path.getParent();
            }

            for(int i = 0; i < 3 && path != null; path = path.getParent()) {
               Path pom = path.resolve("pom.xml");

               try {
                  label150: {
                     InputStream is = Files.newInputStream(pom);

                     String var11;
                     label127: {
                        try {
                           Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                           doc.getDocumentElement().normalize();
                           String version = (String)XPathFactory.newInstance().newXPath().compile("/project/version").evaluate(doc, XPathConstants.STRING);
                           if (version != null) {
                              version = version.trim();
                              if (!version.isEmpty()) {
                                 var11 = version;
                                 break label127;
                              }
                           }
                        } catch (Throwable var16) {
                           if (is != null) {
                              try {
                                 is.close();
                              } catch (Throwable var13) {
                                 var16.addSuppressed(var13);
                              }
                           }

                           throw var16;
                        }

                        if (is != null) {
                           is.close();
                        }
                        break label150;
                     }

                     if (is != null) {
                        is.close();
                     }

                     return var11;
                  }
               } catch (IOException var17) {
                  ;
               }

               ++i;
            }
         }
      } catch (Exception var18) {
         ;
      }

      try {
         label151: {
            InputStream is = cls.getResourceAsStream("/META-INF/maven/io.github.classgraph/classgraph/pom.properties");

            String var26;
            label100: {
               try {
                  if (is != null) {
                     Properties p = new Properties();
                     p.load(is);
                     String version = p.getProperty("version", "").trim();
                     if (!version.isEmpty()) {
                        var26 = version;
                        break label100;
                     }
                  }
               } catch (Throwable var14) {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (Throwable var12) {
                        var14.addSuppressed(var12);
                     }
                  }

                  throw var14;
               }

               if (is != null) {
                  is.close();
               }
               break label151;
            }

            if (is != null) {
               is.close();
            }

            return var26;
         }
      } catch (IOException var15) {
         ;
      }

      Package pkg = cls.getPackage();
      if (pkg != null) {
         String version = pkg.getImplementationVersion();
         if (version == null) {
            version = "";
         }

         version = version.trim();
         if (version.isEmpty()) {
            version = pkg.getSpecificationVersion();
            if (version == null) {
               version = "";
            }

            version = version.trim();
         }

         if (!version.isEmpty()) {
            return version;
         }
      }

      return "unknown";
   }

   static {
      int javaMajorVersion = 0;
      int javaMinorVersion = 0;
      int javaSubVersion = 0;
      List<Integer> versionParts = new ArrayList();
      if (JAVA_VERSION != null) {
         for(String versionPart : JAVA_VERSION.split("[^0-9]+")) {
            try {
               versionParts.add(Integer.valueOf(Integer.parseInt(versionPart)));
            } catch (NumberFormatException var9) {
               ;
            }
         }

         if (!versionParts.isEmpty() && ((Integer)versionParts.get(0)).intValue() == 1) {
            versionParts.remove(0);
         }

         if (versionParts.isEmpty()) {
            throw new RuntimeException("Could not determine Java version: " + JAVA_VERSION);
         }

         javaMajorVersion = ((Integer)versionParts.get(0)).intValue();
         if (versionParts.size() > 1) {
            javaMinorVersion = ((Integer)versionParts.get(1)).intValue();
         }

         if (versionParts.size() > 2) {
            javaSubVersion = ((Integer)versionParts.get(2)).intValue();
         }
      }

      JAVA_MAJOR_VERSION = javaMajorVersion;
      JAVA_MINOR_VERSION = javaMinorVersion;
      JAVA_SUB_VERSION = javaSubVersion;
      JAVA_IS_EA_VERSION = JAVA_VERSION != null && JAVA_VERSION.endsWith("-ea");
      String osName = getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
      if (osName == null) {
         OS = VersionFinder.OperatingSystem.Unknown;
      } else if (!osName.contains("mac") && !osName.contains("darwin")) {
         if (osName.contains("win")) {
            OS = VersionFinder.OperatingSystem.Windows;
         } else if (osName.contains("nux")) {
            OS = VersionFinder.OperatingSystem.Linux;
         } else if (!osName.contains("sunos") && !osName.contains("solaris")) {
            if (osName.contains("bsd")) {
               OS = VersionFinder.OperatingSystem.Unix;
            } else if (!osName.contains("nix") && !osName.contains("aix")) {
               OS = VersionFinder.OperatingSystem.Unknown;
            } else {
               OS = VersionFinder.OperatingSystem.Unix;
            }
         } else {
            OS = VersionFinder.OperatingSystem.Solaris;
         }
      } else {
         OS = VersionFinder.OperatingSystem.MacOSX;
      }

   }

   public static enum OperatingSystem {
      Windows,
      MacOSX,
      Linux,
      Solaris,
      BSD,
      Unix,
      Unknown;

      private OperatingSystem() {
      }
   }
}
