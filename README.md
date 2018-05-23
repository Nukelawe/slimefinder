# Slimefinder tool

Slime finder is a command line tool to search for locations in a Minecraft world with specific amounts of slime chunks within certain range of a player. It was designed to look for mobfarm perimeter locations where the number of slime chunks in the perimeter is either very high or very low.

## Release
[slimefinder-1.2.jar](https://github.com/Nukelawe/slimefinder/releases/download/1.1/slimefinder-1.2.jar)

Run the jar file with the command
```
java -jar slimefinder-<version>.jar <command-line-options>
```

## Documentation

[User manual](Slimefinder/documentation/user-manual.md)

## Commands 

### Building

The project can be run in development mode with the command

```
mvn exec:java -Dexec.mainClass="slimefinder.Main" -Dexec.args="command line arguments"
```

The jar-file can be built with the command

```
mvn package
```

### Testing

Tests are run with the command

```
mvn test
```

Test coverage report is created with the command

```
mvn jacoco:report
```

The test coverage report can be viewed by opening the file _target/site/jacoco/index.html_ with a browser

### Checkstyle

Style checks defined in the file [checkstyle.xml]() are run with the command

```
 mvn jxr:jxr checkstyle:checkstyle
```

The error report can be viewed by opening the file _target/site/checkstyle.html_
