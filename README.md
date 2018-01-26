# SARL Agent Controller for MAC Agents in the City 2017 #

This is the SARL infrastructure to control an agent team in the [2017 MAC Agents in City Contest](https://multiagentcontest.org/) 




## INSTALLATION ##

### Prerequisites ###

* Java Runtime Environment (JRE) and Java Compiler (javac) v1.8 (sun version recommended)
* Maven project management and comprehension tool (to meet dependencies, compile, package, run).
* [SWI Prolog](http://www.swi-prolog.org/) (>7.4.x) with [JPL](http://www.swi-prolog.org/pldoc/doc_for?object=section(%27packages/jpl.html%27)) Bidirectional interface with Java (package **swi-prolog-java** in Linux)
* SARL modules and execution engine 
	* Version tested: 0.5.8.
	* Obtained via Maven automatically from <http://mvnrepository.com/artifact/io.sarl.maven>
* [Mochalog](https://github.com/ssardina/mochalog), a rich bidirectional interface between the Java Runtime and the SWI-Prolog interpreter inspired by JPL.
	* Obtained via Maven automatically from Github repository via JitPack: <https://jitpack.io/#mochalog/mochalog>.


#### SWI Connectivity ####

The connectivity to make use of SWI Prolog in SARL/Java is provided by the [Mochalog](https://github.com/ssardina/mochalog) project, which builds on top of SWI [JPL](http://www.swi-prolog.org/pldoc/doc_for?object=section(%27packages/jpl.html%27)) Bidirectional interface with Java.

If in **Windows**:

* Ensure that the environment variable `SWI_HOME_DIR` is set to the root directory of your installed version of SWI-Prolog.

If in **Linux**:

* Latest package versions at <http://www.swi-prolog.org/build/PPA.txt> 
* JPL is provided via package `swi-prolog-java` (interface between Java and SWI) installed. This will include library `libjpl.so` (e.g., `/usr/lib/swi-prolog/lib/amd64/libjpl.so`)
* Extend environment library `LD_LIBRARY_PATH` for system to find library `libjpl.so` at runtime (dynamic linking/binding) to there:
	```
	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/swi-prolog/lib/amd64/ 
	```
* Extend environment library `LD_PRELOAD` to give precedence to library `libswipl.so` (Check [this post](https://answers.ros.org/question/132411/unable-to-load-existing-owl-in-semantic-map-editor/) and [this one](https://blog.cryptomilk.org/2014/07/21/what-is-preloading/)) about library preloading):
	```
	export LD_PRELOAD=libswipl.so:$LD_PRELOAD
	```
* No need to setup **CLASSPATH** or `SWI_HOME_DIR`.


Here is some example code of its use:

		import io.mochalog.bridge.prolog.PrologContext
		import io.mochalog.bridge.prolog.SandboxedPrologContext
		import io.mochalog.bridge.prolog.query.Query

		// Set-up Prolog knowledgebase
		var prolog_kb : PrologContext
		val beliefSpace = String.format("swiplayer")
		prolog_kb = new SandboxedPrologContext(beliefSpace)
		prolog_kb.importFile("src/main/prolog/masssim_coordinator.pl") // newest version

		// Assert percepts in the KB
		prolog_kb.assertFirst("percepts(@A, @I, @S)", agentName, agents.get(agentName).step, percepts.toString)

		// Querying one solution - Tell the KB to process last percept
		agents.keySet().forEach([ agentName : String |
			prolog_kb.askForSolution(Query.format("process_last_percepts(" + agentName + ")"))
		])
		
		// Querying all solutions - Report percepts available in the KB
		val query = Query.format("percepts(Agent, Step, Percepts)")
		for (solution : prolog_kb.askForAllSolutions(query))
		{
			System.out.format("Information for agent %s on step %d\n", solution.get("Agent").toString(),  solution.get("Step").intValue)
		}
		



### Download the Project ###
To get the current version of the project, simply run ```git clone git@bitbucket.org:sarlrmit/sarl-massim-intf.git```.


### SARL MASSIM Controller Development Framework Setup ###

1. Obtain the corresponding [SARL Eclipse Distribution](http://www.sarl.io/download/) that you want to use.
2. Clone the SARL-SWI Controller `git@bitbucket.org:sarlrmit/sarl-massim-intf.git` if you have not done so already.
3. In Eclipse, navigate to *File > Import > Existing Maven Projects*.
4. In the *Root Directory* field, browse to the root directory of the **sarl-massim** repository.
5. Select the `pom.xml` (Project Object Model) provided in the *Projects* dialog and click *Finish* (see pom.xml details below)
6. The project should now be imported.
7. Right-click on the project directory in Eclipse and go to *Maven > Update Project*.
8. Click OK.
9. When the project has finished updating, go to *Project > Clean*. Click OK. System should compile.


## EXAMPLE AGENTS ##

### Demo ###

This is the demo agent developed by Bob and Keiran to test the infrastructure. The system is run by running the **LoaderAgent** who loads the **SchedulerAgent** and then one **DummyAgent** per agent to be connected to the game.

**Scheduler** agent read all percepts from all agents and "aggregates" them all because there is redundancy in the percepts (all agents receive a lot of the same information). Then it emits events for each separate data (e.g., items, locations, etc). All communication to the environment/game server is done by this agent.

All **DummyAgents** can catch those events and do emit events that are subclasses of **E_AgentAction** (e.g., **GotoFacility**) to instruct the **Scheduler** agent to submit it to the environment/game server.

This system does not do much at the current time, but a lot of infrastructure is provided to store information as Java data (see `helpers/` and `entities/` subdirs).



### SWIMassimPlayer ###

This is one single agent whose feature is to store information in an SWI Prolog Knowledge Base, using the [Mochalog Framework](https://github.com/ssardina/mochalog).

This agent is very thin, but should provide a solid base for  developing an agent whose sophisticated reasoning happens on SWI Prolog, for example using constraints.

To help understand how the SWI Knowledge Base is updated, every percept cycle the agent dumps its KB into file `myClauses-n.pl`, where `n` is the step number.

## RUNNING ##


### 1 - Start MAC17 Game Server ###

For example, from `server/` subdir:

	java -jar target/server-2017-0.7-jar-with-dependencies.jar --monitor 8001 -conf conf/Mexico-City-Test.json

Note that the configuration file (here, `conf/Mexico-City-Test.json`) makes a reference to the team configuration file at the bottom (e.g., `conf/teams/A.json`) which is the file containing all agents allowed to connect and with which id and password. These are the ones your system will use in your agent configuraition file.



### 2 - Start SARL Controller ###

There are two methods to run the SARL Controller - through Eclipse or through the CLI.

#### Running from the SARL Eclipse Distribution ####

You can just run the main agent doing RUN AS "SARL Agent". Or you can directly create a **SARL APPLICATION** under RUN AS. Remember that you may then need to configure that runner to account for other aspects, like parameters or setting of environment variables.

Alternatively, the "low-level" Java based configuration is as follows:

1. Go to *Run > Run Configurations* and double-click on **Java Application**.
2. Set a name for the Run module, for example **SWIMassimPlayer**
3. In the *Project* field browse for the project you have just imported into the workspace.
4. In the *Main class* field search for `io.janusproject.Boot`.
5. Under the *Arguments* tab in the *Program arguments* field, type the agent class you want to run (e.g., `au.edu.rmit.agtgrp.massim.sarlctrl.agents.SWIMassimPlayer`)
6. Set up environment variable `LD_LIBRARY_PATH` to where libjpl.so is located (e.g., `/usr/lib/swi-prolog/lib/amd64/` in Linux) and check "Append environment to native environment" 
7. Set up environment variable `LD_PRELOAD` to `libswipl.so`. Check [this post](https://answers.ros.org/question/132411/unable-to-load-existing-owl-in-semantic-map-editor/) and (Check [this post](https://answers.ros.org/question/132411/unable-to-load-existing-owl-in-semantic-map-editor/) and [this one](https://blog.cryptomilk.org/2014/07/21/what-is-preloading/)) about library preloading).
7. Click *Apply* or *Run*!

This needs to be set-up once. After that you can just run it by:

1. Run the **SWIMassimPlayer** run configuration we previously setup.
2. Wait for the console to announce `Launching the agent: au.edu.rmit.agtgrp.massim.sarlctrl.agents.SWIMassimPlayer`.
3. System will ask which agent configuration file to use of the ones found in the filesystem.


#### Compiling and Running from the command line (CLI) ###

The compilation is done via Maven (Maven – [Maven Getting Started Guide](http://tinyurl.com/y994z75j)):

1. Make sure `pom.xml` is correctly configured
	* sarl version: 0.5.8 AND janus.version: 2.0.5.8
2. Compile with either:
	* `mvn compile` (default pom) or `mvn compile -f <pom file>` to compile the application. Compiled classes will be placed in `target/classes`
	* To do a clean compile: `mvn clean compile`
	* `mvn package` to generate the compile target and JAR file in current director under `target/`
3. Run with (after starting Elevator simulator, of course, which will be waiting for client controller connection):
	* To run using Maven (which will take care of all dependencies needed for the SARL application):

		```
		mvn exec:java -Dexec.mainClass="io.janusproject.Boot" -Dexec.args="au.edu.rmit.agtgrp.massim.sarlctrl.agents.SWIMassimPlayer"
		```
				
	* To run using plain Java we need to include the Janus Kernel JAR file containing the Janus Project execution engine ([available here](http://maven.sarl.io/io/janusproject/io.janusproject.kernel/)):
		
		`
		java -cp /path/to/sarl-controller-<version>.jar:/path/to/io.janusproject.kernel-<version>-with-dependencies.jar io.janusproject.Boot au.edu.rmit.agtgrp.massim.sarlctrl.agents.SWIMassimPlayer
		`

### 3 - Start the Simulation ###
Hit *ENTER* in the Game Server and enjoy! 

You should start seeing the agent reporting things in the console. 

You can see the simulation on the web browser.



## LINKS ##

* Maven:
	* Doc: <https://maven.apache.org/general.html>
	* SARL distribution: <http://mvnrepository.com/artifact/io.sarl.maven>
	* JANUS / SRE (for janus.version in pom.xml):  <http://search.maven.org/#search%7Cga%7C1%7Cjanusproject>
* JitPack for remote installation of dependencies from Github & Bitbucket: 
	* Mochalog: <https://jitpack.io/#mochalog/mochalog>
	* sarl-elvatorsim-ctrl: <https://jitpack.io/#org.bitbucket.sarlrmit/sarl-elvatorsim-ctrl>
* SARL:
	* Main page: <http://www.sarl.io/>
	* github repo: <https://github.com/sarl/sarl>
	* User forum: <https://groups.google.com/forum/?hl=en#!forum/sarl>
* The Multi Agent Agents in City 2017 contest:
	* Multi-Agent Contest Home Page: https://multiagentcontest.org/
	* Main git repository: https://github.com/agentcontest/massim
	* Documentation: https://github.com/agentcontest/massim/tree/master/docs
	* EISMASSim Documentation (the interface provided to communicate with game server): https://github.com/agentcontest/massim/blob/master/docs/eismassim.md
		* Web page of the Environment Interface Standard (EIS): https://github.com/eishub/
* Mochalog (higher abstraction than JPL): <https://github.com/ssardina/mochalog>
* [SWI Prolog](http://www.swi-prolog.org/) (>7.4.x) with [JPL](http://www.swi-prolog.org/pldoc/doc_for?object=section(%27packages/jpl.html%27)) Bidirectional interface with Java (package **swi-prolog-java** in Linux)


## PROJECT LEADER & CONTACT ##

* Sebastian Sardina - ssardina@gmail.com


## PROJECT CONTRIBUTORS ##

* Sebastian Sardina
* Bob Zhou 
* Keiran Hines


## LICENSE ##

This project is using the GPLv3 for open source licensing for information and the license visit GNU website (https://www.gnu.org/licenses/gpl-3.0.en.html).