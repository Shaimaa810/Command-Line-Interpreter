import java.util.Arrays;
import java.util.Objects;

public class Parser {
    String commandName;
    String []args;

    public boolean parse(String input) {
        String []words = input.split("\\s+");
        commandName = words[0].toLowerCase();
        int i = 1;
        if ((Objects.equals(commandName, "cp") && Objects.equals(words[1], "-r")) || (Objects.equals(commandName, "ls") && words.length > 1 && Objects.equals(words[1], "-r"))){
            commandName += " -r";
            i++;
        }
        args = new String[words.length - i];
        for (int j = 0; i < words.length; ++i, ++j) {
            args[j] = words[i];
        }
        return isValidCommand();
    }
    public boolean isValidCommand() {
        if (args.length == 0 && (Objects.equals(commandName, "pwd") || Objects.equals(commandName, "ls") || Objects.equals(commandName, "ls -r") ||
                Objects.equals(commandName, "history") || Objects.equals(commandName, "cd")))
            return true;
        if (args.length == 1 && (Objects.equals(commandName, "cd") || Objects.equals(commandName, "wc")))
            return true;
        if (args.length == 2 && (Objects.equals(commandName, "cp") || Objects.equals(commandName, "cp -r")))
            return true;
        if (args.length >= 1 && (Objects.equals(commandName, "echo") || Objects.equals(commandName, "mkdir") || Objects.equals(commandName, "rmdir") ||
                Objects.equals(commandName, "touch") || Objects.equals(commandName, "rm") || Objects.equals(commandName, "cat")))
            return true;
        return false;
    }
    public String getCommandName() {
        return commandName;
    }
    public String[] getArgs() {
        return args;
    }
}
