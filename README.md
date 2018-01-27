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

```
java -jar target/server-2017-0.7-jar-with-dependencies.jar --monitor 8001 -conf conf/Mexico-City-Test.json
```

Note that the configuration file (here, `conf/Mexico-City-Test.json`) makes a reference to the team configuration file at the bottom (e.g., `conf/teams/A.json`) which is the file containing all agents allowed to connect and with which id and password. These are the ones your system will use in your agent configuraition file.

2. Start the SARL Controller, either via ECLIPSE or through the CLI (see [general SARL instructions](https://bitbucket.org/snippets/ssardina/6eybMg)).
3. Start the MASSIM Simulation by just hitting *ENTER* in the Game Server console
4. Enjoy! You should start seeing the agent reporting things in the console. 
    * You can see the simulation on the web browser.


## EXAMPLE AGENTS 

### Demo

This is the demo agent developed by Bob and Keiran to test the infrastructure. The system is run by running the **LoaderAgent** who loads the **SchedulerAgent** and then one **DummyAgent** per agent to be connected to the game.

**Scheduler** agent read all percepts from all agents and "aggregates" them all because there is redundancy in the percepts (all agents receive a lot of the same information). Then it emits events for each separate data (e.g., items, locations, etc). All communication to the environment/game server is done by this agent.

All **DummyAgents** can catch those events and do emit events that are subclasses of **E_AgentAction** (e.g., **GotoFacility**) to instruct the **Scheduler** agent to submit it to the environment/game server.

This system does not do much at the current time, but a lot of infrastructure is provided to store information as Java data (see `helpers/` and `entities/` subdirs).

### SWIMassimPlayer ###

This is one single agent whose feature is to store information in an SWI Prolog Knowledge Base, using the [Mochalog Framework](https://github.com/ssardina/mochalog).

This agent is very thin, but should provide a solid base for  developing an agent whose sophisticated reasoning happens on SWI Prolog, for example using constraints.

To help understand how the SWI Knowledge Base is updated, every percept cycle the agent dumps its KB into file `myClauses-n.pl`, where `n` is the step number.

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