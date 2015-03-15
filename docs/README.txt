http://www.oocities.org/innigo.geo/jexplorer_screen.htm

http://sourceforge.net/p/fileexplorer/code/HEAD/tree/trunk/

TODO:
Replace directory access with classpath access:

String xmlFile = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "fileTypes.xml";


J-Explorer:
- http://docs.oracle.com/javase/tutorial/uiswing/


Window
======
- http://docs.oracle.com/javase/tutorial/uiswing/components/frame.html
- http://docs.oracle.com/javase/tutorial/uiswing/examples/start/HelloWorldSwingProject/src/start/HelloWorldSwing.java

public class HelloWorldSwing {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

- center window
	// display window in center of screen instead of top left corner (tested
	// under Linux)
	frame.setLocationByPlatform(true);
	
- window icon
  http://docs.oracle.com/javase/tutorial/uiswing/components/frame.html
  
	// Ask for window decorations provided by the look and feel.
	JFrame.setDefaultLookAndFeelDecorated(true);
	
	// AFTER above line: Create and set up the window.
	JFrame frame = new JFrame(APP_TITLE);
		
	//Set the frame icon to an image loaded from a file.
	frame.setIconImage(createFrameIcon());
	
	problem: no transparency: is transparent, but metal look and feel shows a special background; gtk look and feel brings desired look

- Menu
  http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
  http://docs.oracle.com/javase/tutorial/uiswing/examples/components/MenuLookDemoProject/src/components/MenuLookDemo.java
  
  - Datei ... (see docs/menu_windows_explorer_xp.txt)

- Toolbar
  http://docs.oracle.com/javase/tutorial/uiswing/components/toolbar.html

- SplitPane
  http://docs.oracle.com/javase/tutorial/uiswing/components/splitpane.html
  http://docs.oracle.com/javase/tutorial/uiswing/examples/components/SplitPaneDemoProject/src/components/SplitPaneDemo.java  
  
- Status bar
  http://stackoverflow.com/questions/3035880/how-can-i-create-a-bar-in-the-bottom-of-a-java-app-like-a-status-bar
  
  I  am in the process of creating a Java app and would like to have a bar on the bottom of the app, in which I display a text bar and a status (progress) bar.

Only I can't seem to find the control in NetBeans neither do I know the code to create in manually.

Create a JFrame or JPanel with a BorderLayout, give it something like a BevelBorder or line border so it is seperated off from the rest of the content and then add the status panel at BorderLayout.SOUTH.

JFrame frame = new JFrame();
frame.setLayout(new BorderLayout());
frame.setSize(200, 200);

// create the status bar panel and shove it down the bottom of the frame
JPanel statusPanel = new JPanel();
statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
frame.add(statusPanel, BorderLayout.SOUTH);
statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
JLabel statusLabel = new JLabel("status");
statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
statusPanel.add(statusLabel);

frame.setVisible(true);


- center window
  After setting size of frame (after pack()-call) the window is no longer centered (upper left corner)
  
  http://stackoverflow.com/questions/8193801/how-to-set-specific-window-frame-size-in-java-swing

frame.pack();
frame.setLocationByPlatform(true);


- add SplitPane content:
DON'T USE .add() of scroll pane! (added component will not be shown!!!)

 leftScrollPane.setViewportView(createFileTree())

- Table
...

- Make JAR executable
  http://docs.oracle.com/javase/tutorial/deployment/jar/manifestindex.html
  
  META-INF/MANIFEST.MF
  
  http://stackoverflow.com/questions/9689793/cant-execute-jar-file-no-main-manifest-attribute
  
  <build>
   <plugins>
     <plugin>
    <!-- Build an executable JAR -->
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
    <archive>
      <manifest>
      <addClasspath>true</addClasspath>
      <classpathPrefix>lib/</classpathPrefix>
          <mainClass>com.mypackage.MyClass</mainClass>
      </manifest>
    </archive>
     </configuration>
    </plugin>
    </plugins>
    </build>
  
- set look and feel

private static void setLookAndFeel() {
		try {
			// Set System L&F
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			System.out.println("Look and Feel: "
					+ UIManager.getLookAndFeel().getDescription());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
	}  
  
  depending on OS:
  As indicated in other answers, System.getProperty provides the raw data. However, the Apache Commons Lang component provides a wrapper for java.lang.System with handy properties like SystemUtils.IS_OS_WINDOWS, much like the aforementioned Swingx OS util.
http://commons.apache.org/proper/commons-lang/javadocs/api-3.2.1/org/apache/commons/lang3/SystemUtils.html#IS_OS_WINDOWS


- create installation package

    1. Creating a POM file, which contains the configuration of Maven Assembly Plugin and Maven Jar Plugin.
    
<build>
    <finalName>j-explorer</finalName>
    <!-- <resources> -->
    <!-- regular resource processsing for everything -->
    <!-- <resource> -->
    <!-- <directory>src/main/resources</directory> -->
    <!-- </resource> -->
    <!-- </resources> -->
    <plugins>

      <plugin>
        <!-- Builds an executable JAR (but dependencies are not placed in a "lib"-directory!) -->
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>2.3.2</version>
        <!-- The configuration of the plugin -->
        <configuration>
          <!-- Configuration of the archiver -->
          <archive>
            <!-- Manifest (META-INF/MANIFEST.MF) specific configuration -->
            <manifest>
              <!-- Classpath (with all dependent libraries) is added to the manifest of the created jar file. -->
              <addClasspath>true</addClasspath>
              <!-- Configures the classpath prefix. This configuration option is used to specify that all needed libraries are found under lib/ directory. -->
              <classpathPrefix>lib/</classpathPrefix>
              <!-- Specifies the main class of the application -->
              <mainClass>com.datazuul.apps.jexplorer.App</mainClass>
            </manifest>
          </archive>
        </configuration>
      </plugin>

      <plugin>
        <!-- creates archive containing scripts, executable jar and dependencies in a lib-directory -->
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>2.2.2</version>
        <!-- The configuration of the plugin -->
        <configuration>
          <!-- Specifies the configuration file of the assembly plugin -->
          <descriptors>
            <descriptor>src/main/assembly/assembly.xml</descriptor>
          </descriptors>
        </configuration>
      </plugin>
    </plugins>
  </build>
  
    2. Creating a custom assembly descriptor, which specifies the details of your binary distribution.
       (This descriptor is used to describe the contents and structure of your binary distribution.)
    src/main/assembly/assembly.xml
    
  <assembly>
  <id>${project.version}-bin</id>
  <!-- Generates a zip package containing the needed files -->
  <formats>
    <format>zip</format>
  </formats>

  <!-- Adds dependencies to zip package under lib directory -->
  <dependencySets>
    <dependencySet>
      <!-- Project artifact is not copied under library directory since it is added to the root directory of the zip package. -->
      <useProjectArtifact>false</useProjectArtifact>
      <outputDirectory>lib</outputDirectory>
      <unpack>false</unpack>
    </dependencySet>
  </dependencySets>

  <fileSets>
    <!-- Adds startup scripts to the root directory of zip package. The startup scripts are located to src/main/scripts directory as stated by Maven conventions. -->
    <fileSet>
      <directory>${project.build.scriptSourceDirectory}</directory>
      <outputDirectory></outputDirectory>
      <includes>
        <include>startup.*</include>
      </includes>
    </fileSet>
    <!-- adds jar package to the root directory of zip package -->
    <fileSet>
      <directory>${project.build.directory}</directory>
      <outputDirectory></outputDirectory>
      <includes>
        <include>*.jar</include>
      </includes>
    </fileSet>
  </fileSets>
</assembly>
 
       3. start scripts
       src/main/scripts
       
       startup.bat
       java -jar j-explorer.jar
       
       startup.sh
       #!/bin/sh
java -jar j-explorer.jar
    

4. mvn package assembly:single OR 
   mvn assembly:assembly (invokes the package phase)

- Internationalization
  http://www.java2s.com/Tutorial/Java/0220__I18N/AnInternationalizedSwingApplication.htm