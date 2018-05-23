package slimefinder;

import slimefinder.cli.CLI;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.parseArguments(args);
        cli.execute();
    }    
}
