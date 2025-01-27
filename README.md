<p align="center">
<img src="https://raw.githubusercontent.com/freerouting/freerouting/master/res/social_preview/freerouting_social_preview_1280x960_v2.png" alt="Freerouting" title="Freerouting" align="center">
</p>
<h1 align="center">Freerouting</h1>
<h5 align="center">Freerouting is an advanced autorouter for all PCB programs that support the standard Specctra or Electra DSN interface.</h5>

<p align="center">
    <a href="https://github.com/freerouting/freerouting/releases"><img src="https://img.shields.io/github/v/release/freerouting/freerouting" alt="Release version" /></a>
    <a href="LICENSE"><img src="https://img.shields.io/github/license/freerouting/freerouting" alt="License"/></a>
</p>

<h3 align="center">:point_right: This project needs JAVA and UI/UX volunteers! Contact @andrasfuchs for details! :point_left:</h3>

<br/>
<br/>

[Installers for Windows, macOS and Linux can be downloaded here.](https://github.com/freerouting/freerouting/releases)

## Introduction

This software can be used together with all host PCB design software systems containing a standard Specctra or Electra DSN interface. It imports .DSN files generated by the Specctra interface of the host system and exports .SES Specctra session files.

There are three modes for routing traces: 90 degree, 45 degree and free angle. The interactive router is production stable and unsurpassed in its free angle capabilities. An autorouter is currently under development and already stable in the conventional 45 degree mode.

After launching the router a window appears with buttons to display some router demonstrations, to open a sample design, or to open a design of your own.

After opening a design you can start the autorouter with the button in the toolbar on top of the board window.

The board editor has three different interactive states. You can switch between this states with the buttons Select, Route and Drag on the left of the toolbar.

In the beginning the board editor is in the select state. In this state you can select single board items by picking them with the left mouse button or select items in a rectangle by dragging the left mouse button. Only item types switched on in the select parameter sheet will be selected. After selecting some items the toolbar displays options for showing and manipulating these items. If you push the info button for example a window with text information about the selected items is displayed. After clicking a blue word in this text a new window with further information pops up. To return to the select state push the cancel button or click somewhere in the empty space of the board window.

By pushing the Route button you get into the state for interactive routing. In this state you can start a new trace by picking an item belonging to a net, for example a pin. Then you can follow the displayed airline with the mouse until you have reached the target item at the other end of the airline. The trace will be connected automatically to the target, if it is on the same layer. If you want to change to a different layer during interactive routing, select "change layer" and then the name of the new layer in the popup menu under the right mouse button. Then a via will be inserted, if that is possible, and a new trace starts on the new layer. You can also change the layer by pressing a number key.

After pushing the Drag button you get into the state for changing the location of vias, components or traces. In this state you can select vias or components and drag them with the left mouse button to a different location. The connected route is updated automatically. You can also move traces by pushing them from behind out of the empty space with the left mouse button pressed. That works on the current layer, which can be changed in the select parameter sheet. In this way you can make space for example to insert a new component.

For more information please use the online help in the board editor. From here you can download also a printable version of the online help.

### Additional steps for users of CadSoft-Eagle

1) Download the latest Eagle2freerouter ulp file

2) Start Eagle and open in the control panel of Eagle for example the design my_design.brd.

3) Choose in the Files pulldown-menu of Eagle the item "execute ULP" and select the Eagle2freerouter ulp file. A file with name my_design.dsn is generated.

4) Start the router, push the "Open Your Own Design" button and select my_design.dsn in the file chooser.

5) After making some changes to the design with the router select "export Eagle session script" in the Files pulldown-menu. A file with name my_design.scr is generated.

6) Choose in the Files pulldown-menu of Eagle the item "execute Script" and select my_design.scr.


### Additional steps for users of KiCad

1) Download the latest freerouting-1.5.0.jar file from the [Releases](https://github.com/freerouting/freerouting/releases)

2) Start KiCad and open your project in Pcbnew.

3) Export the PCB into Specctra DSN (File / Export... / Specctra DSN).

4) Start the router by running the freerouting-executable.jar file, push the "Open Your Own Design" button and select the exported .dsn file in the file chooser.

5) Do the routing.

5) When you're finished, export the results into a Specctra session file (File / Export Specctra Session File). The router will generate a .ses file for you.

6) Go back to KiCad's Pcbnew and import the results (File / Import Specctra Session...).



### Additional steps for users of [pcb-rnd](http://www.repo.hu/projects/pcb-rnd)

Using the standalone freerouting application

1) Download the latest freerouting-1.5.0.jar file from the [Releases](https://github.com/freerouting/freerouting/releases) page

2) Start pcb-rnd and load your layout.

3) Export the layout as Specctra DSN (File / Export... / Specctra DSN).

4) Start the router by running the freerouting-executable.jar file, push the "Open Your Own Design" button and select the exported .dsn file in the file chooser.

5) Do the routing.

5) When you're finished, export the results into a Specctra session file (File / Export Specctra Session File). The router will generate a .ses file for you.

6) Go back to pcb-rnd and import the results (File / Import autorouted dsn/ses file...). Track widths and clearances during autorouting are based on the currently selected route style during DSN export.


Using freerouting from within pcb-rnd

1) Download the latest freerouting-1.5.0-linux-x64.zip from the [Releases](https://github.com/freerouting/freerouting/releases) page

2) Unzip it and rename the top directory freerouting-1.5.0-linux-x64 to freerouting.net (the default location is /opt/freerouting.net)

3) Start pcb-rnd and ensure that this directory is specified in (File / Preferences / Config Tree / Plugins / ar_extern / freerouting_net...); the location of the executable can be customised.

4) Load your layout

5) Open the external autorouter window with (Connect / Automatic Routing / External autorouter...)

6) Select the freerouting.net tab, and push the "Route" button.

7) Go back to the layout and inspect the autorouted networks. Track widths and clearances during autorouting are based on the currently selected route style when the autorouter is started.

## Using the command line arguments

Freerouter was designed as a GUI program, but it also can function as a command line tool. Typically you would have an input file (e.g. Specctra DSN) that you exported from you EDA (e.g. KiCad). If this file has unconnected routes, you would want to wire those with autorouter, and save the result in a format that you can then import back into your EDA.

The following command line arguments are supported by freerouter:

* -de [design input file]: loads up a Specctra .dsn file at startup.
* -di [design input directory]: if the GUI is used, this sets the default folder for the open design dialogs.
* -dr [design rules file]: reads the rules from a previously saved .rules file.
* -do [design output file]: saves a Specctra board (.dsn), a Specctra session file (.ses) or Eagle session script file (.scr) when the routing is finished.
* -mp [number of passes]: sets the upper limit of the number of auto-router passes that will be performed.
* -l [language]: "de" for German, "zh" for Simplified Chinese, otherwise it's English.
* -mt [number of threads]: sets thread pool size for route optimization. The default is one less than the number of logical processors in the system. Set it to 0 to disable route optimization.
* -oit [percentage]: stops the route optimizer if the improvement drops below a certain percentage threshold per pass. Default is 0.1%, and 0% means to continue improving until there are no more route options to test.
* -us [greedy | global | hybrid]: sets board updating strategy for route optimization: greedy, global optimal or hybrid. The default is greedy. When hybrid is selected, another option "hr" specifies hybrid ratio.
* -hr [m:n]: sets hybrid ratio in the format of #_global_optiomal_passes:#_prioritized_passes. The default is 1:1. It's only effective when hybrid strategy is selected. 
* -is [sequential | random | prioritized]: sets item selection strategy for route optimization: sequential, random, prioritized. The default is prioritized. Prioritied stragegy selects items based on scores calculated in previous round.
* -im: saves intermediate steps in version-specific binary format. This allows to user to resume the interrupted optimization from the last checkpoint. Turned off by default.
* -h: shows help.

A complete command line looks something like this if your are using PowerShell on Windows:

`
& "java.exe" -jar freerouting-1.5.0.jar -de MyBoard.dsn -do MyBoard.ses -mp 100 -dr MyBoard.rules
`

This would read the _MyBoard.dsn_ file, do the auto-routing with the parameters defined in _MyBoard.rules_ for the maximum of 100 passes, and then save the result into the _MyBoard.ses_ file. 


## Multi-threaded implementation of route optimization

After Freerouting completes auto-routing it switches to the next phase, called route optimization to improve the board even further. Multi-threading reduces the time it will take to do this second step.

Besides multi-threading, multiple optimzation strategies were also implemented. Global optimal strategy selects the global optimal update after processing all items in an optimation pass, while greedy 
strategy adopts an update as soon as it is found to be better than current one, so there will be multiple updates in a greedy optimization pass. Hybrid strategy mixes the above two, and there is an 
option to select the mixing ratio.

Sequential, random and prioritized item selection strategies are implemented to determine which item to process next during an optimization pass. Each item is ranked during the optimization pass so that 
it's possible to prioritize items with better scores when selecting items to process in next optimization pass.

Test runs on a 12-core system using freerouting v1.5.0:

| Settings                           | Auto-routing time | Route optimization time | Route opt. improvement |
|------------------------------------|------------------:|------------------------:|:----------------------:|
| -mt 0                              |     18.00 seconds |                    N/A  |                    N/A |
| -mt 1 -us greedy -is prioritized   |     18.50 seconds |              55 minutes |                 51.02% |
| -mt 11 -us greedy -is prioritized  |     19.95 seconds |              13 minutes |                 50.85% |
| -mt 11 -us greedy -is sequential   |     19.31 seconds |              20 minutes |                 50.32% |
| -mt 11 -us greedy -is random       |     18.68 seconds |              14 minutes |                    N/A |
| -mt 11 -us global -is prioritized  |     18.99 seconds |              93 minutes |                 50.84% |
| -mt 11 -us hybrid -hr 1:1          |     18.40 seconds |              33 minutes |                 51.08% |
| -mt 11 -us hybrid -hr 1:3          |     18.48 seconds |              31 minutes |                 50.98% |

| File                         | Settings  | Auto-routing time | Route optimization time | Route optimization improvement |
|------------------------------|-----------|------------------:|------------------------:|-------------------------------:|
| zMRETestFixture.dsn          |           |     19.95 seconds |             797 seconds |                         50.85% |
| zMRETestFixture.dsn          | -oit 0.25 |     18.18 seconds |              49 seconds |                         50.23% |
| Mars-64-revE.dsn             | -oit 0.25 |     26.27 seconds |              29 seconds |                         51.03% |
| smoothieboard.dsn            | -oit 0.25 |    488.25 seconds |              60 seconds |                            N/A |

All parameter defaults are set according to these test results, so you don't need to change them unless you have a special need.


## Running Freerouting on 32-bit systems

Unfortunately the modern Java distributions have often limited support for 32-bit executable generation, so it's not trivial at all to have x32 installers that run on 32-bit systems.
Fortunatelly though the platform independent .JAR files can be run, if the matching Java runtime is installed on the 32-bit system you are planning to use.

You will need the following steps to make it work:
1. Get the current JAR release from our [Releases page](https://github.com/freerouting/freerouting/releases)
2. [Download](https://adoptium.net/releases.html?variant=openjdk11&jvmVariant=hotspot) and install the 32-bit version of the Adoptium Temurin 11 (LTS)
    * For Windows, select the "x86" architecture from the dropdown
    * For ARM Linux, select the "arm32" architecture from the dropdown
3. Run the downloaded JAR file using the installed java.exe
    
    `java.exe -jar freerouting-1.5.0.jar`

## How to build it from source

### Requirements

- Java >= 11 ([Adoptium Temurin 11 JRE](https://adoptium.net/releases.html?variant=openjdk11&jvmVariant=hotspot))
- [Gradle 6.x](https://gradle.org/releases/)
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `freerouting` [Gradle](http://www.gradle.org/) project in your favourite IDE (NB, IntelliJ, Eclipse etc. with Gradle Plugin) and build it
by calling the `assemble` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/freerouting`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    bash gradlew assemble
    
#### Windows (CMD)

    gradlew assemble
    
![image](https://user-images.githubusercontent.com/910321/143483981-5f1f8473-098e-4cf2-997b-a34d14346853.png)
    
#### Generated Executables

All four .jar files will be generated in the _build\libs_ subfolder. You would typically run the _freerouting-executable.jar_ file.

## Contributing

We ❤️ all our contributors; this project wouldn’t be what it is without you! 

If you want to help out, please consider replying to [issues](https://github.com/freerouting/freerouting/issues), creating new ones, or even send your fixes and improvements as [pull requests](https://github.com/freerouting/freerouting/pulls).
