package com.xin.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

//声明式
@Command(name = "foo", subcommands = Bar.class)
class Foo implements Callable<Integer> {
    @Option(names = "-x") int x;

    @Override public Integer call() {
        System.out.printf("hi from foo, x=%d%n", x);
        boolean ok = true;
        return ok ? 0 : 1; // exit code
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Foo()).execute(args);
        System.exit(exitCode);
    }
}

@Command(name = "bar", description = "I'm a subcommand of `foo`")
class Bar implements Callable<Integer> {
    @Option(names = "-y") int y;

    @Override public Integer call() {
        System.out.printf("hi from bar, y=%d%n", y);
        return 23;
    }

    @Command(name = "baz", description = "I'm a subcommand of `bar`")
    int baz(@Option(names = "-z") int z) {
        System.out.printf("hi from baz, z=%d%n", z);
        return 45;
    }
}