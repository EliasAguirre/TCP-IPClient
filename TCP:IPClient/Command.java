import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Command {
    private static Set<String> state = new HashSet<String>(Arrays.asList("open", "quit"));
    private static Socket skt;
    private static String db = "*";
    // Initialized false, only use setDebugOn in main when true
    private static Boolean debugOn = false;

    public static void setDebugOn(Boolean b) {
        debugOn = b;
    }

    public static void open(String[] args) throws IOException {
        if (!state.contains("open")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 2) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }

        int port;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("904 Invalid argument.");
            return;
        }

        String server = args[0];
        port = Integer.parseInt(args[1]);

        try {
            skt = new Socket(server, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            // SOCKET CONNECTED
            if (debugOn) {
                System.out.println("<-- " + in.readLine());
            } else {
                in.readLine();
            }

            state.remove("open");
            state.add("dict");
            state.add("set");
            state.add("define");
            state.add("match");
            state.add("prefixmatch");
            state.add("close");
            db = "*";
        } catch (UnknownHostException ex) {
            System.out.println("920 Control connection to " + server + " on port " + port + " failed to open.");
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
    }

    public static void dict(String[] args) throws IOException {
        if (!state.contains("dict")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 0) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }
        try {
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            String showdb = "SHOW DB";

            if (debugOn) {
                System.out.println("--> " + showdb);
            }
            out.println(showdb);

            String fromServer;

            if (debugOn) {
                while ((fromServer = in.readLine()) != null) {
                    if (((fromServer.startsWith("110 ")) && (fromServer.endsWith("present")))
                            || (fromServer.contains("250 ok"))) {
                        System.out.println("<-- " + fromServer);
                    } else {
                        System.out.println(fromServer);
                    }
                    if (fromServer.equals("250 ok")) {
                        break;
                    }
                }
            } else {
                while ((fromServer = in.readLine()) != null) {
                    if (((fromServer.startsWith("110 ")) && (fromServer.endsWith("present"))) ||
                            (fromServer.contains("250 ok"))) {
                        // dontprintcodeline
                    } else {
                        System.out.println(fromServer);
                    }
                    if (fromServer.equals("250 ok")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
    }

    public static void set(String[] args) {
        if (!state.contains("set")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 1) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }

        db = args[0];
    }

    public static void define(String[] args) throws IOException {
        if (!state.contains("define")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 1) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }

        try {
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            String word = args[0];

            String define = "DEFINE " + db + " " + word;

            if (debugOn) {
                System.out.println("--> " + define);
            }
            out.println(define);

            String fromServer;


            // TODO: remove extra 552 bug
            if (debugOn) {
                while ((fromServer = in.readLine()) != null) {
                    if ((fromServer.contains("250 ok")) || ( (fromServer.startsWith("150")) && fromServer.endsWith("retrieved") )
                            || (fromServer.startsWith("550 invalid")) || (fromServer.startsWith("151 ")) || fromServer.contains("552 no match")) {
                        System.out.println("<-- " + fromServer);
                    } else {
                        System.out.println(fromServer);
                    }

                    if (fromServer.contains("250 ok")) {
                        break;
                    }

                    if (fromServer.contains("550 invalid database, use SHOW DB for list")) {
                        System.out.println("999 Processing error. The set dictionary " + db
                                + " does not exist. Please set a valid dictionary");
                        break;
                    }

                    if (fromServer.contains("552 no match")) {
                        System.out.println("**No definition found**");

                        String match = "MATCH " + db + " . " + word;
                        out.println(match);

                        while ((fromServer = in.readLine()) != null) {
                            if (fromServer.contains("552 no match")) {
                                System.out.println("<-- " + fromServer);
                                System.out.println("***No matches found***");
                                break;
                            } else if (fromServer.contains("250 ok")) {
                                System.out.println("<-- " + fromServer);
                                break;
                            } else {
                                System.out.println(fromServer);
                            }
                        }
                        break;
                    }
                }
            } else {
                while ((fromServer = in.readLine()) != null) {
                    if ((fromServer.contains("250 ok")) || ( (fromServer.startsWith("150")) && fromServer.endsWith("retrieved") )
                            || fromServer.contains("552 no match")) {
                        // dont print
                    } else if ((fromServer.contains("151 "))) {
                        String line = fromServer;
                        String[] splited = line.split("\\s+");
                        String print = "@ ";
                        for (int i = 2; i < splited.length; i++) {
                            print += " " + splited[i];
                        }
                        System.out.println(print);
                    } else {
                        System.out.println(fromServer);
                    }
                    if (fromServer.contains("250 ok")) {
                        break;
                    }

                    if (fromServer.contains("550 invalid database, use SHOW DB for list")) {
                        System.out.println("999 Processing error. The set dictionary " + db
                                + " does not exist. Please set a valid dictionary");
                        break;
                    }

                    if (fromServer.contains("552 no match")) {
                        System.out.println("**No definition found**");

                        String match = "MATCH " + db + " . " + word;
                        out.println(match);

                        while ((fromServer = in.readLine()) != null) {
                            if (fromServer.contains("552 no match")) {
                                ;
                                System.out.println("***No matches found***");
                                break;
                            } else if (fromServer.contains("250 ok")) {
                                break;
                            } else {
                                System.out.println(fromServer);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
    }

    public static void match(String type, String[] args) throws IOException {
        if (type.equals("prefix") && !state.contains("prefixmatch")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        } else if (type.equals("exact") && !state.contains("match")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 1) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }
        if (!type.equals("exact") && !type.equals("prefix")) {
            System.out.println("904 Invalid argument.");
            return;
        }

        try {
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            String match = "MATCH " + db + " " + type + " " + args[0];

            if (debugOn) {
                System.out.println("--> " + match);
            }
            out.println(match);

            String fromServer;

            if (debugOn) {
                while ((fromServer = in.readLine()) != null) {

                    if ((fromServer.contains("250 ok")) || (fromServer.contains("552 no match"))
                            || ((fromServer.contains("550 invalid"))) || ( (fromServer.startsWith("152 ")) && (fromServer.endsWith("found")))) {
                        System.out.println("<-- " + fromServer);
                    } else {
                        System.out.println(fromServer);
                    }

                    if (fromServer.contains("552 no match")) {
                        System.out.println("****No matching word(s) found****");
                        break;
                    }

                    if (fromServer.contains("250 ok")) {
                        break;
                    }

                    if (fromServer.contains("550 invalid database, use SHOW DB for list")) {
                        System.out.println("999 Processing error. The set dictionary " + db
                                + " does not exist. Please set a valid dictionary");
                        break;
                    }
                }
            } else {
                while ((fromServer = in.readLine()) != null) {
                    if ((fromServer.contains("250 ok")) || ( (fromServer.startsWith("152 ")) && (fromServer.endsWith("found")))
                            || (fromServer.contains("552 no match")) || ((fromServer.contains("550 invalid")))) {
                        // do not print line
                    } else {
                        System.out.println(fromServer);
                    }

                    if (fromServer.contains("552 no match")) {
                        System.out.println("****No matching word(s) found****");
                        break;
                    }

                    if (fromServer.contains("250 ok")) {
                        break;
                    }

                    if (fromServer.contains("550 invalid database, use SHOW DB for list")) {
                        System.out.println("999 Processing error. The set dictionary " + db
                                + " does not exist. Please set a valid dictionary");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
    }

    public static void close(String[] args) throws IOException {
        if (!state.contains("close")) {
            System.out.println("910 Supplied command not expected at this time.");
            return;
        }
        if (args.length != 0) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }

        try {
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

            if (debugOn) {
                System.out.println("--> QUIT");
            }
            out.println("QUIT");
            String fromServer;

            if ((fromServer = in.readLine()) != null) {
                if (debugOn) {
                    System.out.println("<-- " + fromServer);
                }
                if (fromServer.contains("221 bye")) {
                    skt.close();
                    state.clear();
                    state.add("open");
                    state.add("quit");
                }
            }
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
    }

    public static void quit(String[] args) throws IOException {
        if (args.length != 0) {
            System.out.println("903 Incorrect number of arguments.");
            return;
        }
        if(skt == null){
            System.exit(0);
        }
        try {
            if (!skt.isClosed()) {
                String fromServer;
                PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));

                if (debugOn){
                    System.out.println("--> " + "QUIT");
                }
                out.println("QUIT");
                while ((fromServer = in.readLine()) != null){
                    if (debugOn){
                        System.out.println("<-- " + fromServer);
                        in.close();
                        skt.close();
                        break;
                    } else {
                        skt.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("925 Control connection I/O error, closing control connection.");
            skt.close();
            state.clear();
            state.add("open");
            state.add("quit");
        }
        System.exit(0);
    }
}