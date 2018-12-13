# Stash Tool

Welcome to the Stash Tool. The smart way automatically configure environments.

# Including Stash Tool as a dependency
The stash tool is located in the cruat repository
 - To access the cruat repository simply add the following to your pom.xml

```
	<repositories>
		<repository>
			<id>repo.cruat</id>
			<name>cruat repository</name>
			<url>http://repo.cruat.com/snapshots</url>
		</repository>
 	</repositories>	
```
 - To access the stash tool as a dependency use
```

	<dependencies>
		...
		<dependency>
			<groupId>com.cruat.tools</groupId>
			<artifactId>stash</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
	<dependencies>
```


# Getting Started 
To use this tool, start with <b>com.cruat.tools.stash.config.Configurator</b>
This class has a method called execute which takes a string representing a path.

If the destination of the passed path does not exist, nothing will happen.
If the destination of the passed path is a file, that file will be processed as an instruction
If the destination of the passed file is a directory, the directory is recursively transversed
and each entry is treated like an individual file.

# Basic Configuration

Stash tool allows for flexible configuration.

Every non-commented line is considered to be an instruction for the stash tool.
There are 4 basic arguments that the stash tool requires for an instruction :
- File : Which file the operation will be executed on
- Operation : Determines what is done with the information
- Setting : Which setting within the file to do the operation on
- Value : The second Operand for binary Operations.

Note that configurations are considered in sequence within a file.

The most basic command is 
<location><Operation><propertyName>=<value>

The default operation is to change the value of the specified property to 
Location equates to any logical path. For example, "/config.properties"
Note that the location must point to a file.

The Operation determines what happens within that file

The Property Name identifies what you want to do the operation on

The value determines the second operand in binary operations.

# Operations
``>``  (With a value) This will replace the whatever is stored at the identifier with the value
``<`` undefined
``*`` undefined
``?`` undefined
``|`` undefined

# TODO

Fix missing DTDs
