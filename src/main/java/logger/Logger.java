package logger;

import java.io.*;
import java.sql.Timestamp;


@SuppressWarnings("unused")
public class Logger {
    private final VerboseLevel verboseLevel;
    private final boolean fileLogging;
    private final File logFile;
    private final File oldLogFile;
    private BufferedWriter writer;
    private static Logger instance;
    private int logsLevel = 0;
    private static VerboseLevel defaultVerboseLevel;
    private static boolean defaultFileLogging;
    private static String defaultLogPath;
    private static boolean configured;

    public Logger(VerboseLevel verboseLevel, boolean fileLogging, String logFilePath) {
        this.verboseLevel = verboseLevel;
        this.fileLogging = fileLogging;
        if (fileLogging) {
            logFile = new File(logFilePath);
            oldLogFile = new File(logFilePath + "_old");
            if (!logFile.exists()) {
                try {
                    if (!logFile.createNewFile()) throw new IOException("Could not create log file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (!logFile.renameTo(oldLogFile)) try {
                    throw new IOException("Could not rename log file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                writer = new BufferedWriter(new FileWriter(logFile, true));
                write(String.format("Logs at %s%s", new Timestamp(System.currentTimeMillis()), System.lineSeparator()));
                write(String.format("Verbose level: %s%s", verboseLevel, System.lineSeparator()));
                info("Logs file opened");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.logFile = null;
            this.oldLogFile = null;
            this.writer = null;
        }
    }

    public static void setDefaults(VerboseLevel verboseLevel, boolean fileLogging, String logPath) {
        defaultVerboseLevel = verboseLevel;
        defaultFileLogging = fileLogging;
        defaultLogPath = logPath;
        configured = true;
    }

    public static Logger getDefault() {
        if (!configured) throw new IllegalArgumentException("Logger not configured");
        return new Logger(defaultVerboseLevel, defaultFileLogging, defaultLogPath);
    }

    public static Logger getInstance() {
        return (instance == null) ? instance = getDefault() : instance;
    }


    public void log(String log, LogType.defaults defaultLogType) {
        log(log, defaultLogType.value, true);
    }

    public void log(String log, LogType logType, boolean escapeLine) {
        String prefix = "|-" + new String(new char[logsLevel]).replace("\0", "--");
        StringBuilder line = new StringBuilder(prefix + logType.label + ": " + log);
        line.append((escapeLine) ? System.lineSeparator() : "\r");
        //TODO allow to write on previous line
        if (fileLogging) write(line.toString());
        if (verboseLevel.logTypes.contains(logType)) {
            for (Style style : logType.styles) line.insert(0, style);
            line.append(Style.RESET);
            System.out.print(line);
        }
    }

    public void info(String log) {
        log(log, LogType.defaults.INFO);
    }

    public void error(Exception e) {
        error(e.getMessage());
    }

    public void error(String log) {
        log(log, LogType.defaults.ERROR);
    }

    public void escapeLine() {
        System.out.println();
        if (fileLogging) write(System.lineSeparator());
    }

    public void asc() {
        logsLevel--;
    }

    public void desc() {
        logsLevel++;
    }

    private void write(String line) {
        //TODO add timestamp
        try {
            writer.write(line);
        } catch (IOException e) {
            System.out.printf("%s: Failed to write log, got '%s'%n", LogType.defaults.ERROR.value.label, e.getMessage());
        }
    }

    public void close() {
        if (fileLogging) {
            log("Logs written to " + this.logFile.getAbsolutePath(), LogType.defaults.INFO);
            write(System.lineSeparator());
            if (oldLogFile.exists()) {
                try (FileInputStream in = new FileInputStream(oldLogFile)) {
                    int n;
                    while ((n = in.read()) != -1) writer.write(n);
                } catch (IOException e) {
                    error(String.format("Failed to copy content of \"%s\" to \"%s\"", oldLogFile.getAbsolutePath(), logFile.getAbsolutePath()));
                }
            }
            if (!oldLogFile.delete()) System.out.printf("Failed to delete old log file \"%s\"%n", oldLogFile.getAbsolutePath());
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
