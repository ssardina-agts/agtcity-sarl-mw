# SARL Agent Controller for MAC Agents in the City 2017 #

This is the SARL infrastructure to control an agent team in the [2017 MAC Agents in City Contest](https://multiagentcontest.org/) 

## PREREQUISITES

* Java Runtime Environment (JRE) and Java Compiler (javac) v1.8 (sun version recommended)
* Maven project management and comprehension tool (to meet dependencies, compile, package, run).
* SARL modules and execution engine 
	* Version tested: 0.6.1.
	* Obtained via Maven automatically from <http://mvnrepository.com/artifact/io.sarl.maven>
* If you want to use Prolog knowledgebases:
    * [SWI Prolog](http://www.swi-prolog.org/): a state of the art Prolog system.
	    * Version >7.4.x with [JPL](http://www.swi-prolog.org/pldoc/doc_for?object=section(%27packages/jpl.html%27)) Bidirectional interface with Java (package **swi-prolog-java** in Linux)
    * [Mochalog](https://github.com/ssardina/mochalog) (optional): a rich bidirectional interface between the Java Runtime and the SWI-Prolog interpreter inspired by JPL.
	    * Obtained via Maven automatically from Github repository via JitPack: <https://jitpack.io/#mochalog/mochalog>.
        * Check the Mocha Wiki to understsand how to install and use it in your agent.

## INSTALL AND RUN

Refer to the my [general SARL instructions](https://bitbucket.org/snippets/ssardina/6eybMg) on how to setup, configure, and run SARL applications.


1. Start MAC17 Game Server. For example, from `server/` subdir:

		java -jar target/server-2017-0.7-jar-with-dependencies.jar --monitor 8001 -conf conf/Mexico-City-Test.json

	Note that the configuration file (here, `conf/Mexico-City-Test.json`) makes a reference to the team configuration file at the bottom (e.g., `conf/teams/A.json`) which is the file containing all agents allowed to connect and with which id and password. These are the ones your system will use in your agent configuraition file.

2. Start the SARL Controller, either via ECLIPSE or through the CLI (see [general SARL instructions](https://bitbucket.org/snippets/ssardina/6eybMg)).
3. Start the MASSIM Simulation by just hitting *ENTER* in the Game Server console
4. Enjoy! You should start seeing the agent reporting things in the console. 
    * You can see the simulation on the web browser.


## INFRASTRUCTURED PROVIDED

So, what is provided by this package?

### Capabilities and Skills

The main component of this infrastructure are the two capabilities provided with its corresponding skills:

* **C_MassimTalking**: the main capability that allows the agent to connect to the game server, register agents, and control such players, by receiving their sensing percepts and performing actions in the simulation.
	* The skill **S_MassimTalking07** implements this capability for the MAC 20017 version.
* **C_Reporting**: a capability to report information.
	* The skill **S_ConsoleReporting** implements this capability by just printing messages on console.

The main tools provided by the **C_MassimTalking** capability are:

* `MT_initialize()` and `MT_registerPlayersFromConfig()`: initialize the Massim Talking infrastructure and register players using the configuration file already loaded.
* `MT_sensePlayerPercepts(playerName : String)`: sense the percepts for a player; blocking.
* `MT_executeAction(playerName : String, action : Action)`: instruct the execution of an action ofr a player. 

The **S_MassimTalking07** skill makes use of entity class **PlayerState** to store each player registered in the game. This class stores, for example,  the location of the agent, its charge and load level, the items it is holding, etc.


### Events

The main set of events are:

* The genearl event **E_AgentAction** and its many subclases define all the actions that players can do in the environment.
* The general event **E_AgentPercept** ang its many subclases define all the various percepts players can receive form game server.
	* Note they are not currently posted automatically, but they are available for use to the programmer to emit.

Both are used by the demo **SchedulerAgent** to inform the dummy agents of the percepts received and to receive from them actions to execute.

There are also other events used by the example agents (E_SpawnAgent, E_Act, E_SenseEnvironment, and E_SpawnComplete).

### Entities

A set of classes representing entities/artifacts (e.g., facilities, jobs, storages, etc.) in the simulation are provided.

A special one is **PlayerState** which is used to keep track of each player current state, as per the last percept received.

### Aggregators

A set of classes are provided to support aggregating many percepts (for different players) into an aggregation, as there are much redundancy in the percepts received from the game server.


## USING THIS INTERFACE IN YOUR SARL SYSTEM

You basically need to make sure the JAR file for this infrastructure is accessible by your system, so you can use the capacities, skills, entities, etc.

You can do that automatically usinv Maven and JitPack, by configuring your POM with:

        <!-- SARL-agtcity-intf version -->
        <sarl-agtcity-intf.version>-SNAPSHOT</sarl-agtcity-intf.version>


        <!-- JitPack used for remote installation of MASSim from Github -->
        <repository>
            <id>jitpack.io</id>
            <name>JitPack Repository</name>
            <url>https://jitpack.io</url>
        </repository>
		
		
        <!--  SARL Agent City Interface  -->
		<dependency>
		    <groupId>org.bitbucket.ssardina-research</groupId>
		    <artifactId>sarl-agtcity-intf</artifactId>
	    	    <version>${sarl-agtcity-intf.version}</version>
		</dependency>		


## EXAMPLE AGENTS 

This package comes with two minimal examples

### SuperSingleAgent ###

This is one single SARL agent that manages all the players in the simulation via the Skill provided. It does almost nothing, simply sense and print some status information.

### Demo Scheduler

This is the demo agent developed by Bob and Keiran to test the infrastructure and based on the Java-based demo that came with the server. 

The system is run by running the **SchedulerAgent** who spawns one **DummyAgent** per player to be connected to the game.

**Scheduler** agent reads all percepts from all players, "aggregates" them all (because there is a lot of redundancy in the percepts; all agents receive a lot of the same information, and then emits events for each separate data (e.g., items, locations, etc). All communication to the environment/game server is done by this agent.

All **DummyAgents** can catch those events and emit events that are subclasses of **E_AgentAction** (e.g., **GotoFacility**) to instruct the **SchedulerAgent** to submit it to the environment/game server for the corrending player being managed by the **DummyAgent**.

This system does not do much at the current time, but a lot of infrastructure is provided to store information as Java data (see `helpers/` and `entities/` subdirs).


## LINKS

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