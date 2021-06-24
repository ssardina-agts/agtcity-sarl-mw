# SARL Middleware for Agents in the City 2018+

This is the SARL Middleware infrastructure (MW) to control an agent team in the [Agents in City Contest](https://multiagentcontest.org/2018/) from the Multi Agent Contest. In particular, this is the MW for the [RMIT 2018+ version of the game and server](https://github.com/ssardina-agts/agtcity-server), which brings back items in shops as the 2017 edition.

The MW can be used to develop SARL agent controllers for Agents in City, providing convenient capacities and skills to sense and act in the game server.

You can see the infrastructure working by clicking on the following video:

[![click to watch video](https://multiagentcontest.org/2016/banner.jpg)](https://youtu.be/nFR7Strp9ms).

This framework can be [accessible via JitPack](https://jitpack.io/#com.github.ssardina-agts/agtcity-sarl-mw) by adding the corresponding dependency and repository on the `pom.xml` (see below).

## PRE-REQUISITES

- Java Runtime Environment (JRE) and Java Compiler (javac) v1.8+. 
    - Tested with SUN Java 1.8 and OpenJDK 11.
- Maven project management and comprehension tool (to meet dependencies, compile, package, run).
- The [RMIT 2018+ game server edition](https://github.com/ssardina-agts/agtcity-server) (not the official 2018 server). This updated edition that brings back _items_ to _shop_ as in the 2017 version.

The following  dependencies are resolved via Maven and JitPack automatically:

- [SARL modules and execution engine](http://mvnrepository.com/artifact/io.sarl.maven).
- The [EISMASSim](https://github.com/ssardina-agts/agtcity-server/tree/master/eismassim) environment interface connectivity that comes with the [MASSim Agents in City Server (RMIT 2018+ edition)](https://github.com/ssardina-agts/agtcity-server).
    - This is a Java API that provides high-level access to the game sever to avoid dealing with low-level XML or JSON messages. It uses the generic [Environment Interface Standard (EIS)](https://github.com/eishub/eis) to communicate with the MASSim server (this is automatically gathered via Maven by the server package).
    - The doc of the protocol and messages can be found [here](https://github.com/ssardina-agts/agtcity-server/blob/master/docs/eismassim.md).

## VERSION CONVENTION

[Semantic versioning](https://semver.org/) is used with versions of the form `Major.Minor.Patch`. Each version will rely on a particular SARL version, which is indicated via `<sarl.version>` property in the POM file.

## USING THE MIDDLEWARE

Most of the times one would just use this MW out-of-the-box to develop SARL controllers for the Agents in City game.

You can gather the MW via JitPack+GitHub by adding it as a dependency in the `pom.xml` file of your SARL application as follows:

```xml
<properties>
	<!-- SARL Agt City MW version - can use tag or commit hash id -->
	<agtcity-sarl-mw.version>14b2889</agtcity-sarl-mw.version>
	...
</properties>

<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>


<!--  SARL Agent City Interface  -->
<dependency>
	<groupId>com.github.ssardina-agts</groupId>
	<artifactId>agtcity-sarl-mw</artifactId>
<version>${agtcity-sarl-mw.version}</version>
</dependency>
```

Alternatively, if you have the JAR file already of the MW, you can manually install it in your local Maven via `mvn install:install-file`; for example:

```shell

mvn install:install-file -Dfile=agtcity-sarl-mw-1.2.0.7.2.jar -DgroupId=com.github.ssardina-agts \
			-DartifactId=agtcity-sarl-mw -Dversion=1.2.0.7.2 -Dpackaging=jar
```

## RUN DEMO

The MW comes with two simple SARL agent controllers that can be used for testing:

- `SingleRandomAgent`: a set of four connected entities/agents go to shops randomly and buy items.
- `SingleGiveAgent`: two agents are connected, they go to shop1, one buys items and gives to the other.

1. **Start RMIT 2018+ Game Server**. From `server/` folder:

	```bash
	$ ./startServer.sh conf/SampleConfig.json
	```

	or run it using Java itself:

	```bash
	$ java -jar target/server-2020-1.0-jar-with-dependencies.jar --monitor 8001 -conf conf/SampleConfig.json
	```

	Note that the configuration file (here, `conf/SampleConfig.json`) makes a reference to the team configuration file at the bottom (e.g., `conf/teams/A.json`) which is the file containing all agents allowed to connect and with which id and password. These are the ones your system will use in your agent configuration file.

	In the console of the server, you will see a URL link to the monitor. Click it to see the GUI interface of the game.

2. **Start the SARL demo controller**, either via ECLIPSE or through the CLI. For example:

	```bash
	$ mvn exec:java -Dexec.args="SingleRandomAgent conf/SingleAgent"
	```

	See below under _Examples_ for more information on the two "dummy" controller examples provided here in the MW, mostly for testing and as agent templates.

3. **Start the game simulation** by just hitting *ENTER* in the Game Server console.
	- The web GUI should start displaying/monitoring the simulation.
4. **Enjoy!** You should start seeing the agent reporting things in the console. 
    - You can see the simulation on the web browser.

## EI Agent --> Connection Entity --> Game Entity Agent 

Before explaining what the MW provides, in terms of capacities, skills, events and data structures, we need to understand the different abstractions from an actual entity entity in the game (e.g., a specific truck or drone) to a SARL agent controlling one more of those agents.

Because the framework involves the [MASSIM game server](https://github.com/ssardina-agts/agtcity-server), the [EISMASSim](https://github.com/ssardina-agts/agtcity-server/blob/master/docs/eismassim.md) Environment Interface (EI from now on) for MASSIM, and the SARL agents, there are different components and identifications that need to be well understood:

1. An entity is an agent playing in the simulation in some team, that is, a specific truck, drone, car in a team. A team will be composed of many entities of different types.
2. An **entity connection** represents a _connection to a concrete entity_ in the game server (e.g., a truck or motorcycle) that can _execute actions_ and can _receive percepts_. An entity connection is specified in the JSON configuration file by:
    - A **name** of the connection, like `connectionA1` or just `entityA1`.
    - The **username** (e.g., `agentA1`) and a **password** to be able to successfully establish the connection to the entity in the game server. Via this credentials, the connection will link to a specific entity in the game (e.g., a specific truck).

3. An  **EI agent** in the EI links/exposes an entity connection to the application. In general an EI agent may link to many entity connections, but for simplicity, the MW skill does a 1-to-1 link between EI agents and entity connections (i.e., entities on the game. So, for every entity connection, there will be a corresponding EI agent with the same name: EI agent `entityA4` in the EI will juts manage the entity connection  `entityA4` (which could be, say, a truck in the game).

So, for example, EI agent `entityA1` will be linked 1-to-1 to entity connection `entityA1` that has a username `agentA1` and a given password so as to control a truck in team A.

## INFRASTRUCTURE PROVIDED

The MW provides:

1. A capacity and associated skill `MassimTalking` to talk to the game server.
2. A capacity and associated skill `Reporting` to communicate information (in console).
3. A set of useful events.
4. A class `State` that can store the current overall state of those entities connected.
5. Utility classes to create and update objects carrying domain information.

## MassimTalking Capacity and Skill

The main component of this infrastructure are the two capabilities provided with its corresponding skills:

- `C_MassimTalking`: the main capability of the MW allowing SARL agents to connect to the game server, register entities, and control such entities by receiving their sensing percepts and performing actions in the simulation. The skill `S_MassimTalking` implements this capability for the [Agent in City RMIT server](https://github.com/ssardina-agts/agtcity-server/).

The main tools provided by the `C_MassimTalking` capability are:

- For setup:
	- When you create the skill, you need to pass the directory where the server JSON config file is located (e.g., `eismassimconfig.json`).
		- This file has the details of the game server (e.g., IP, port) as well as all the entity connections to the server (e.g., `entityA1` or `connectionA1`).
	- `MT_registerEntityByName(entityName : String)`: register interest in controlling a given entity connection. If none is added, then it will be assumed that we will control all the connections in the server configuration file. This is useful if we do not want to control all the entity connections listed in the server configuration file, but only some of them.
    	- Note this will NOT make the connection to the server, it will just signal the MW what will be controlled.
	- `MT_initialize()`: will create all network connection to game server for the entities to be controlled. This will create one EI agent per entity connection, and register using each credentials.
- For interaction with the game server:
	- `MT_senseEntityPercepts(entityName : String) : Collection<Percept>`: sense the percepts of an entity; blocking.
	- `MT_executeAction(entityName : String, action : Action)`: instruct an entity to execute an action in the game. 
- Getters:
	- `MT_getEntitiesNames() : Set<String>`: list of entity connections registered in the MW.
	- `MT_getAllEntityData() : Map<String, EntityData>`: get the `EntityData` of each registered entity.
	- `MT_getEntityData(entityName : String) : EntityData`: get the `EntityData` of a given registered entity.
	- `MT_getStatus() : EnvironmentState`: get the state of the EI.
	- `MT_getStepNo() : int`: get the last simulation step number seen.

The `S_MassimTalking` skill makes use of entity class `EntityData` to store the latest info about each entity registered in the game. This class stores, for example,  the location of the agent, its charge and load level, the items it is holding, etc.

## Reporting Capacity and Skill

`C_Reporting`: a capability to report information. It provides two actions:
- `agent_says(message, param)`: says a message with [varidic](http://www.sarl.io/docs/official/reference/general/FuncDecls.html#4-variadic-function) arguments `param`.
- `shouts_says(message, param)`: shouts a message with [varidic](http://www.sarl.io/docs/official/reference/general/FuncDecls.html#4-variadic-function) arguments `param`.

The skill `S_ConsoleReporting` implements this capability by printing on console.

## Events (package `au.edu.rmit.agtgrp.agtcity.sarl.mw.events`)

A set of events are provided for signaling various pieces of information related to the application (e.g., actions, percepts received, jobs available, information about entities, etc.).

Note these events are _not_ posted automatically, but they are available to the programmer to emit.
## Aggregator `au.edu.rmit.agtgrp.agtcity.sarl.mw.util.State`

A `State` class is provided to aggregate a snapshot of the game as perceived by a SARL agent, which may sense across many entities. An object of this class can be used to carry the current snapshots of the simulator, as sensed via percepts.

So, the idea is to sense all entities and build an aggregated view (in which repeated percepts are made unique), and then act on that.

## Demo controllers for testing 

This package comes with two minimal examples that basically shows how to sense the environment, report some information, and move entities around randomly across shops known.

- `SingleRandomAgent`: a set of four connected entities/agents go to shops randomly and buy items.
- `SingleGiveAgent`: two agents are connected, they go to shop1, one buys items and gives to the other.

The default Maven execution class is the booting class `au.edu.rmit.agtgrp.agtcity.sarl.mw.BootMAS` which can take the agent to boot as argument or otherwise will ask the user for which one to execute:

```shell
mvn exec:java
```

This will ask for agent selection from the available ones and then which folder in `conf/` to be used to find the `eismassimconfig.json` server connection info file.

To directly specify which controller and server connection info file:

```shell
mvn exec:java -Dexec.args="SingleRandomAgent conf/SingleAgent"
```

If packaged with all dependencies one can use:

```shell
java -jar target/agtcity-sarl-mw-2.0.0.8.6-jar-with-dependencies.jar  SingleRandomAgent
```

One then needs to select the single agent configuration `conf/SingleAgent`, as all agents are controlled centrally.

----------------------------
## PROJECT CONTRIBUTORS

- Sebastian Sardina (contact - ssardina@gmail.com)
- Bob Zhou
- Keiran Hines

## LICENSE ##

This project is using the GPLv3 for open source licensing for information and the license visit GNU website (https://www.gnu.org/licenses/gpl-3.0.en.html).

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.