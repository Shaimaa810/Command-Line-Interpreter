import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.commons.io.FileUtils;


/*
    //////////////////  import org.apache.commons.io.FileUtils; in intellij ////////////////////
    open project in intellij -> from left bar -> right click on project folder -> select Open Module Settings ->
        from left bar select libraries -> select + -> select java -> Select folder commons-io-2.15.0 (exist in folder org.apache_library in project) -> click ok
 */


public class Terminal {
    Parser parser;
    Path currentPath;
    ArrayList<String> history;
    public Terminal() {
        parser = new Parser();
        currentPath = Paths.get("").toAbsolutePath();
        history = new ArrayList<>();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void echo() {
        for (int i = 0; i < parser.getArgs().length; ++i) {
            System.out.print(parser.getArgs()[i] + " ");
        }
        System.out.println();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void pwd() {
        System.out.println(currentPath.toString());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void cd() {
        if (parser.getArgs().length == 0) {
            String homeDir = System.getProperty("user.home");
            currentPath = currentPath.resolve(homeDir);
        }
        else {
            Path newPath = currentPath.resolve(parser.getArgs()[0]).normalize();
            if (Files.exists(newPath)) {
                currentPath = newPath;
            }
            else {
                System.out.println("The system cannot find the path specified.");
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void ls() {
        File []content = currentPath.toFile().listFiles();
        ArrayList<String> sortedContent = new ArrayList<>();
        for (File f : content) sortedContent.add(f.getName());
        Collections.sort(sortedContent);
        for (String str : sortedContent) System.out.print(str + " ");
        System.out.println();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void ls_r() {
        File []content = currentPath.toFile().listFiles();
        ArrayList<String> sortedContent = new ArrayList<>();
        for (File f : content) sortedContent.add(f.getName());
        Collections.sort(sortedContent, Collections.reverseOrder());
        for (String str : sortedContent) System.out.print(str + " ");
        System.out.println();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void mkdir() {
        for (String path : parser.getArgs()) {
            Path newPath = currentPath.resolve(path).normalize();
            if (Files.exists(newPath)) {
                System.out.println("A subdirectory or file \"" + newPath.toString() + "\" already exists.");
            }
            else {
                File dir = new File(newPath.toString());
                dir.mkdirs();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void rmdir() {
        if (Objects.equals(parser.getArgs()[0], "*")) {
            // remove all empty directories
            File []content = currentPath.toFile().listFiles();
            for (File dir : content) {
                if (dir.isDirectory() && dir.listFiles().length == 0) {
                    dir.delete();
                }
            }
        }
        else {
            for (String dir : parser.getArgs()) {
                Path newPath = currentPath.resolve(dir).normalize();
                if (Files.exists(newPath)) {
                    if (newPath.toFile().isDirectory()) {
                        if (newPath.toFile().listFiles().length == 0) {
                            newPath.toFile().delete();
                        }
                        else {
                            System.out.println("directory " + newPath.getFileName() + " is not empty");
                        }
                    }
                    else {
                        System.out.println(newPath.getFileName() + " is not a directory");
                    }
                }
                else {
                    System.out.println("directory " + newPath.getFileName() + " not exist");
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void touch() {
        for (String file : parser.getArgs()) {
            Path newPath = currentPath.resolve(file).normalize();
            if (newPath.getParent().toFile().exists() && newPath.getParent().toFile().isDirectory()) {
                File f = new File(newPath.toString());
                try {
                    if (!f.createNewFile()){
                        System.out.println("file \"" + newPath.toString() + "\" already exists");
                    }
                } catch (IOException e) {

                }
            }
            else {
                System.out.println("directory \"" + newPath.getParent().toString() + "\" not exist");
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void cp() {
        File source = new File(currentPath.resolve(parser.getArgs()[0]).normalize().toString());
        File destination = new File(currentPath.resolve(parser.getArgs()[1]).normalize().toString());
        if (Files.exists(source.toPath())) {
            if (!source.isFile()) {
                System.out.println("\"" + source.toString() + "\" not a file");
                return;
            }
        }
        else {
            System.out.println("file \"" + source.toString() + "\" not exist");
            return;
        }
        if (Files.exists(destination.toPath())) {
            if (!destination.isFile()) {
                System.out.println("\"" + destination.toString() + "\" not a file");
                return;
            }
        }
        else {
            System.out.println("file \"" + destination.toString() + "\" not exist");
            return;
        }
        destination.delete();
        try {
            destination.createNewFile();
        } catch (IOException e) {}
        try {
            Scanner reader = new Scanner(source);
            FileWriter writer = new FileWriter(destination);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                writer.write(line);
            }
            reader.close();
            writer.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e){}
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void cp_r() {
        File source = new File(currentPath.resolve(parser.getArgs()[0]).normalize().toString());
        File destination = new File(currentPath.resolve(parser.getArgs()[1]).normalize().toString());
        if (Files.exists(source.toPath())) {
            if (!source.isDirectory()) {
                System.out.println("\"" + source.toString() + "\" not a directory");
                return;
            }
        }
        else {
            System.out.println("directory \"" + source.toString() + "\" not exist");
            return;
        }
        if (Files.exists(destination.toPath())) {
            if (!destination.isDirectory()) {
                System.out.println("\"" + destination.toString() + "\" not a directory");
                return;
            }
        }
        else {
            System.out.println("directory \"" + destination.toString() + "\" not exist");
            return;
        }
        File parent = new File(destination.toPath().resolve(source.getName()).normalize().toString());
        parent.mkdirs();
        try {
            FileUtils.copyDirectory(source, parent);
        } catch (IOException e){}
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void rm() {
        for (String f : parser.getArgs()) {
            File file = new File(currentPath.resolve(f).normalize().toString());
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                else {
                    System.out.println("\"" + file.toString() + "\" not a file");
                }
            }
            else {
                System.out.println("file \"" + file.toString() + "\" not exist");
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void cat() {
        for (String f : parser.getArgs()) {
            File file = new File(currentPath.resolve(f).normalize().toString());
            if (file.exists())  {
                if (file.isFile()) {
                    try {
                        Scanner reader = new Scanner(file);
                        while (reader.hasNextLine()) {
                            String line = reader.nextLine();
                            System.out.println(line);
                        }
                        reader.close();
                    } catch (FileNotFoundException e){}
                }
                else {
                    System.out.println("\"" + file.toString() + "\"  not a file");
                }
            }
            else {
                System.out.println("file \"" + file.toString() + "\" not exist");
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void wc() {
        File file = new File(currentPath.resolve(parser.getArgs()[0]).normalize().toString());
        if (file.exists()) {
            if (file.isFile()) {
                try {
                    Scanner reader = new Scanner(file);
                    int numLines = 0, numWords = 0, numChars = 0;
                    while (reader.hasNextLine()) {
                        String line = reader.nextLine();
                        String []words = line.split("\\s+");
                        numLines++;
                        numWords += words.length;
                        numChars += line.length();
                    }
                    System.out.println("#lines = " + numLines);
                    System.out.println("#words = " + numWords);
                    System.out.println("#characters = " + numChars);
                    System.out.println("file name: " + file.getName());
                } catch (FileNotFoundException e) {}
            }
            else {
                System.out.println("\"" + file.toString() + "\"  not a file");
            }
        }
        else {
            System.out.println("file \"" + file.toString() + "\" not exist");
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void history() {
        for (String str : history) System.out.println(str);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void chooseCommandAction(String commandName) {
        switch (commandName) {
            case "echo":
                echo();
                break;
            case "pwd":
                pwd();
                break;
            case "cd":
                cd();
                break;
            case "ls":
                ls();
                break;
            case "ls -r":
                ls_r();
                break;
            case "mkdir":
                mkdir();
                break;
            case "rmdir":
                rmdir();
                break;
            case "touch":
                touch();
                break;
            case "cp":
                cp();
                break;
            case "cp -r":
                cp_r();
                break;
            case "rm":
                rm();
                break;
            case "cat":
                cat();
                break;
            case "wc":
                wc();
                break;
            case "history":
                history();
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);
        Terminal terminal = new Terminal();
        while (true) {
            System.out.print("> ");
            System.out.println(terminal.currentPath);
            System.out.print("> ");
            String command = read.nextLine();
            terminal.history.add(command);
            if (Objects.equals(command.toLowerCase(), "exit")) {
                break;
            }
            else if (terminal.parser.parse(command)) {
                terminal.chooseCommandAction(terminal.parser.getCommandName());
            }
            else {
                System.out.println("Error! invalid command name or invalid parameters.");
            }
        }
    }
}
