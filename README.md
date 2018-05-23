# Slimefinder tool

Slime finder is a command line tool to search for locations in a Minecraft world with specific amounts of slime chunks within certain range of a player. It was designed to look for mobfarm perimeter locations where the number of slime chunks in the perimeter is either very high or very low.

## Documentation

[User manual](Slimefinder/documentation/user-manual.md)

## Commands 

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
