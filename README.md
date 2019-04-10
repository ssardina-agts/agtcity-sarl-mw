# SARL Agent Middleware Framework for MAC Agents in the City 2018 

This is the SARL Agents in City Middleware infrastructure to control an agent team in the [2018 MAC Agents in City Contest](https://multiagentcontest.org/2018/).  

You can see the infrastructure working by [watching this demo video](https://youtu.be/nFR7Strp9ms).

**Version convention**: `Major.Minor.<SARL Version>`. For example, 1.3.0.8.6 is version 1.3 for SARL 0.8.6.

Check the the tags under [commits](https://bitbucket.org/ssardina-research/sarl-agtcity18-mw/commits/all) for various release versions.

This framework can be accessible via JitPack at https://jitpack.io/#org.bitbucket.ssardina-research/sarl-agtcity-mw18 by adding the corresponding dependency and repository on the `pom.xml`.

-----------------------------
## TABLE OF CONTENTS

[TOC]

-----------------------------
## PREREQUISITES

* Java Runtime Environment (JRE) and Java Compiler (javac) v1.8 (Sun version recommended)
* Maven project management and comprehension tool (to meet dependencies, compile, package, run).
* SARL modules and execution engine 
	* SARL version to be used determined via env variable `SARL_VERSION`, e.g., `export SARL_VERSION=0.8.6`
	* Obtained via Maven automatically from <http://mvnrepository.com/artifact/io.sarl.maven>
* The [EISMASSim](https://github.com/eishub/massim) environment interface.
	* A Java library using the [Environment Interface Standard (EIS)](https://github.com/eishub/eis) to communicate with the MASSim server that can be used with platforms which support the EIS.
	* Provides a more high-level access to the game sever than low-level JSON messages.
	* Comes with the game server. Using server release `massim-2018-1.2` (Sept 2018). Check [here](https://github.com/agentcontest/massim/tree/massim-2018-1.2).
	* Uses the [eishub/EIS](https://github.com/eishub/eis) version `0.5` (sources also under `extras/`).
* The [MASSIM Agents in City Game server](https://github.com/agentcontest/massim), version `massim-2018-1.2`, to run the game.
	* Server version that comes with massim package distribution [`massim-2018-1.2`](https://github.com/agentcontest/massim/releases/tag/massim-2018-1.2).
	* The doc of the protocol and messages [here](https://github.com/agentcontest/massim/blob/massim-2018-1.2/docs/eismassim.md)


-----------------------------
## USE/INSTALL AND DEVELP THE MIDDLEWARE

Most of the times one would just use this middleware out-of-the-box to develop SARL Agents in City controller. 

To do so, you first need to have the JAR file for the middleware installed in your local Maven repo for your controller application to use it.

First the `pom.xml` of your SARL controller application using the this middleware should have the following dependency to the middleware:

	    <properties>
	        <!-- SARL Agt City MW version -->
	        <sarl-agtcity-mw.version>1.2.${sarl.version}</sarl-agtcity18-mw.version>
	
			...
		</properties>


        <!--  SARL Agent City Interface  -->
		<dependency>
		    <groupId>org.bitbucket.ssardina-research</groupId>
		    <artifactId>sarl-agtcity18-mw</artifactId>
	    	<version>${sarl-agtcity18-mw.version}</version>
		</dependency>

There are then two ways to install the corresponding JAR file for the middleware:

1. Manually get the corresponding JAR file for the middleware for the SARL version you intend to use from the Download section (or produce the JAR yourself by cloning and compiling this repo yourself) and run something like this to install it:

		mvn install:install-file -Dfile=sarl-agtcity-mw-1.2.0.7.2.jar -DgroupId=org.bitbucket.ssardina-research \
			-DartifactId=sarl-agtcity-mw -Dversion=1.2.0.7.2 -Dpackaging=jar

	This will install the middleware infrastructure in your local maven repository and your application will now have access to it. Done!

2. You can specify your application to get it automatically via Maven. To do so, include this repository for the JitPack service:

		<repositories>
				<repository>
					<id>jitpack.io</id>
					<url>https://jitpack.io</url>
				</repository>
		</repositories>

	With this, when you build your application, Maven via JitPack will get middleware from this repo, compile it, package, and install it.


### DEVELOP THE MIDDLEWARE FURTHER

Clone this repo and refer to the the [general SARL instructions](https://bitbucket.org/snippets/ssardina/6eybMg) on how to setup, configure, and run SARL applications.

----------------------------
## RUN DEMO

1. Start MAC17 Game Server. For example, from `server/` subdir:

		java -jar target/server-2017-0.7-jar-with-dependencies.jar --monitor 8001 -conf conf/Mexico-City-Test.json

	Note that the configuration file (here, `conf/Mexico-City-Test.json`) makes a reference to the team configuration file at the bottom (e.g., `conf/teams/A.json`) which is the file containing all agents allowed to connect and with which id and password. These are the ones your system will use in your agent configuration file.
	
	In the console of the server, you will see a URL link to the monitor. Click it to see the GUI interface of the game.

2. Start the SARL Controller, either via ECLIPSE or through the CLI (see [general SARL instructions](https://bitbucket.org/snippets/ssardina/6eybMg)). See below under examples for how to run the examples built-in in this package.
3. Start the MASSIM Simulation by just hitting *ENTER* in the Game Server console
	* The web GUI should start displaying/monitoring the simulation.
4. Enjoy! You should start seeing the agent reporting things in the console. 
    * You can see the simulation on the web browser.

----------------------------
## INFRASTRUCTURED PROVIDED

So, what is provided by this package?

### Capabilities and Skills

The main component of this infrastructure are the two capabilities provided with its corresponding skills:

* **C_MassimTalking**: the main capability that allows the agent to connect to the game server, register agents, and control such players, by receiving their sensing percepts and performing actions in the simulation.
	* The skill **S_MassimTalking17** implements this capability for the MAC 20017 version.
* **C_Reporting**: a capability to report information.
	* The skill **S_ConsoleReporting** implements this capability by just printing messages on console.

The main tools provided by the **C_MassimTalking** capability are:

* For setup:
	* When you create the skill, you need to pass the directory where the server JSON config file is located (e.g., `eismassimconfig.json`).
		* This file has the details of the game server (e.g., IP, port) as well as all the entity connections to the server (e.g., `connectionA1`).
	* `MT_registerEntityByName(entityName : String)`: register interest in controlling a given entity connection. If none is added, then it will be assumed that we will control all the connections in the server configuration file. This is useful if we do not want to control all the entity connections listed in the server configuration file, but only some of them.
	* `MT_initialize()`: will create all network connection to game server for the entities of relevance and will register all the relevant players to be controlled that were added via `MT_addPlayerByName` (if none, then register all available connections).
	* **NOTE:** there is no need to "register" players explicitely; they are all done automatically in the initialization phase.
* For interaction with the game server:
	*`MT_sensePlayerPercepts(playerName : String) : Map<String, Collection<Percept>>`: sense the percepts for a player; blocking.
	* `MT_senseAllPlayerPercepts(playerName : String) : Map<String, Collection<Percept>>` sense the pecepts of all the players; blocking.
	* `MT_executeAction(playerName : String, action : Action)`: instruct the execution of an action ofr a player. 
* Getters:
	* `MT_getPlayersNames() : Set<String>`: list of player connections registered in the MW.
	* `MT_getAllPlayerStates() : Map<String, PlayerState>`: get the `PlayerState` of each registered player.
	* `MT_getPlayerState(playerName : String) : PlayerState`: get the `PlayerState` of a given registered player.
	* `MT_getStatus() : EnvironmentState`: get the state of the EI.

The **S_MassimTalking17** skill makes use of entity class **PlayerState** to store each player registered in the game. This class stores, for example,  the location of the agent, its charge and load level, the items it is holding, etc.

### Entities, EI Players, Game User Agent 

Because the framework involves the MASSIM game server, an Environment Interface, and the SARL agents, there are different entities and identifications. Overall

1. There is an **entity connection**, which is the connection to the game server, with a username and password. For example `connectionA1'.
2. Then one can register a **player agent** in the Environment Interface and link it to an entity connection. The skill does this automatically by registering one player per entity connection established. Those players will have the name `player_<entity>`.
3. Finally, a connection is linked to a real game **user agent** in the game simulator (as per `username` entry in the JSON configuration file). This will have a name that will be transmitted in its percepts. For example, `connectionA1` could be the game entity `agentA1` which is a drone. 


### Events (package `au.edu.rmit.agtgrp.agtcity.sarl.mw.events`)

The main set of events are:

* The general event **E_AgentAction** and its many subclasses define all the actions that players can do in the environment.
* The general event **E_AgentPercept** and its many subclasses define all the various percepts players can receive form game server.
	* Note they are not currently posted automatically, but they are available for use to the programmer to emit.

Both are used by the demo **SchedulerAgent** to inform the dummy agents of the percepts received and to receive from them actions to execute.

There are also other events used by the example agents (E_SpawnAgent, E_Act, E_SenseEnvironment, and E_SpawnComplete).

### Entities (package `au.edu.rmit.agtgrp.agtcity.sarl.mw.entities`)

A set of classes representing entities/artifacts (e.g., facilities, jobs, storages, etc.) in the simulation are provided.

A special one is **PlayerState** which is used to keep track of each player current state, as per the last percept received.

### Aggregators (package `au.edu.rmit.agtgrp.agtcity.sarl.mw.aggregator`)

A set of classes are provided to support aggregating many percepts (for different players) into an aggregation, as there are much redundancy in the percepts received from the game server. 

So, the idea is to sense all players and build an aggregated view (in which repeated percepts are made unique), and then act on that.


## EXAMPLE AGENTS 

This package comes with two minimal examples that basically show how to sense the environment, report some information, and move players around randomly.

They also showcase the infrastructure provided to use the MW and store information as Java data (see `helpers/` and `entities/` subdirs).

The default Maven execution class is the booting class `au.edu.rmit.agtgrp.agtcity.sarl.mw.BootMAS` which takes the agent system to run as argument (defaults to `SingleRandomAgent`) 

### SuperSingleAgent ###

This is one single SARL agent that manages all the players in the simulation via the Skill provided. It does almost nothing, simply sense,  print some status information, and move players randomly to facilities. Run it as follows:

			mvn exec:java -Dexec.args=SingleRandomAgent

If packaged with all dependencies one can use:

		java -jar target/sarl-agtcity-mw-1.3.0.7.2-jar-with-dependencies.jar  SingleRandomAgent

Alternatively, one can rely on the Janus booting class as follows:

		java -cp target/sarl-agtcity-mw-1.3.0.7.2-jar-with-dependencies.jar io.janusproject.Boot au.edu.rmit.agtgrp.agtcity.sarl.mw.agents.SingleRandomAgent


One then needs to select the single agent configuration `conf/SingleAgent`, as all agents are controlled centrally.


### Demo Scheduler

This is the demo agent developed by Bob and Keiran to test the infrastructure and based on the Java-based demo that came with the server. 

The system is run by running the **SchedulerAgent** who spawns one **DummyAgent** per player to be connected to the game:

			mvn exec:java -Dexec.args=SchedulerAgent

and then select the single agent configuration `conf/SingleAgent`, as all agents are controlled centrally by scheduler SARL agent.

**Scheduler** agent reads all percepts from all players, "aggregates" them all (because there is a lot of redundancy in the percepts; all agents receive a lot of the same information, and then emits events for each separate data (e.g., items, locations, etc). All communication to the environment/game server is done by this agent.

All **DummyAgents** can catch those events and emit events that are subclasses of **E_AgentAction** (e.g., **GotoFacility**) to instruct the **SchedulerAgent** to submit it to the environment/game server for the corresponding player being managed by the **DummyAgent**.




----------------------------
## LINKS

For general links check [here](https://bitbucket.org/snippets/ssardina/6eybMg#markdown-header-1-software-prerequisites-and-links).

* The Multi Agent Agents in City 2017 contest:
	* Multi-Agent Contest Home Page: https://multiagentcontest.org/
	* Main GIT repository: https://github.com/agentcontest/massim
	* Documentation: https://github.com/agentcontest/massim/tree/master/docs
	* EISMASSim Documentation (the interface provided to communicate with game server): https://github.com/agentcontest/massim/blob/master/docs/eismassim.md
		* Web page of the Environment Interface Standard (EIS): https://github.com/eishub/


## PROJECT LEADER & CONTACT ##

* Sebastian Sardina - ssardina@gmail.com


## PROJECT CONTRIBUTORS ##

* Sebastian Sardina
* Bob Zhou 
* Keiran Hines


## LICENSE ##

This project is using the GPLv3 for open source licensing for information and the license visit GNU website (https://www.gnu.org/licenses/gpl-3.0.en.html).

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.