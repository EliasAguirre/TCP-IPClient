// You can use this file as a starting point for your dictionary client
// The file contains the code for command line parsing and it also
// illustrates how to read and partially parse the input typed by the user. 
// Although your main class has to be in this file, there is no requirement that you
// use this template or hav all or your classes in this file.

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//
// This is an implementation of a simplified version of a command
// line dictionary client. The only argument the program takes is
// -d which turns on debugging output. 
//

public class CSdict {
    static final int MAX_LEN = 255;
    static Boolean debugOn = false;

    private static final int PERMITTED_ARGUMENT_COUNT = 1;
    private static String command;
    private static String[] arguments;

    private static Command commandClass = new Command();

    public static void main(String[] args) {
        byte cmdString[] = new byte[MAX_LEN];
        int len;
        // Verify command line arguments

        if (args.length == PERMITTED_ARGUMENT_COUNT) {
            debugOn = args[0].equals("-d");
            if (debugOn) {
                commandClass.setDebugOn(debugOn);
                System.out.println("Debugging output enabled");
            } else {
                System.out.println("902 Invalid command line option - Only -d is allowed.");
                return;
            }
        } else if (args.length > PERMITTED_ARGUMENT_COUNT) {
            System.out.println("901 Too many command line options - Only -d is allowed.");
            return;
        }

        // Example code to read command line input and extract arguments.

        try {
            List<String> validCommands = Arrays.asList("open", "dict", "set", "define", "match", "prefixmatch", "close",
                    "quit");
            String text = "";

            // keep ClientInterface open until "quit" passed
            while (!text.equalsIgnoreCase("quit")) {
                System.out.print("317dict> ");
                cmdString = new byte[MAX_LEN];
                System.in.read(cmdString);

                // Convert the command string to ASII
                String inputString = new String(cmdString, "ASCII");

                // Empty lines and lines starting with the character '#' are to be silently
                // ignored, and a new prompt displayed.
                if (inputString.trim().isEmpty() || inputString.trim().startsWith("#")) {
                    continue;
                }

                // Split the string into words
                String[] inputs = inputString.trim().split("( |\t)+");
                // Set the command
                command = inputs[0].toLowerCase().trim();
                text = command;

                // if invalid command, ret '900 Invalid command.'
                if (!validCommands.contains(command)) {
                    System.out.println("900 Invalid command.");
                    break;
                }
                // Remainder of the inputs is the arguments.
                arguments = Arrays.copyOfRange(inputs, 1, inputs.length);

                // TODO: evaluate commands
                switch (command) {
                    case ("open"):
                        text = "open";
                        commandClass.open(arguments);
                        break;
                    case ("dict"):
                        text = "dict";
                        commandClass.dict(arguments);
                        break;
                    case ("set"):
                        text = "set";
                        commandClass.set(arguments);
                        break;
                    case ("define"):
                        text = "define";
                        commandClass.define(arguments);
                        break;
                    case ("match"):
                        text = "match";
                        commandClass.match("exact", arguments);
                        break;
                    case ("prefixmatch"):
                        text = "prefixmatch";
                        commandClass.match("prefix", arguments);
                        break;
                    case ("close"):
                        text = "close";
                        commandClass.close(arguments);
                        break;
                    case ("quit"):
                        text = "quit";
                        commandClass.quit(arguments);
                        break;
                }
            }
        } catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
            System.exit(-1);
        }
    }
}
