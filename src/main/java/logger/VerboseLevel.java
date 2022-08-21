package logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class VerboseLevel implements Serializable {
    private String description;
    public Set<LogType> logTypes;

    public VerboseLevel() {
    }

    public VerboseLevel(LogType... logTypes) {
        this(new HashSet<>(Arrays.asList(logTypes)));
    }

    public VerboseLevel(Set<LogType> logTypes) {
        try {
            this.description = logTypes.stream().map(logType -> logType.label).collect(Collectors.joining(", "));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.logTypes = logTypes;
    }

    public enum defaults {
        NONE(),
        MAIN(LogType.defaults.INFO),
        ERRORS(LogType.defaults.INFO, LogType.defaults.ERROR),
        WARNINGS(LogType.defaults.INFO, LogType.defaults.ERROR, LogType.defaults.WARNING),
        DETAILED(LogType.defaults.INFO, LogType.defaults.ERROR, LogType.defaults.WARNING, LogType.defaults.DETAIL),
        DEBUG(LogType.defaults.INFO, LogType.defaults.ERROR, LogType.defaults.WARNING, LogType.defaults.DETAIL, LogType.defaults.DEBUG),
        ;
        public final VerboseLevel value;

        defaults(LogType.defaults... logTypes) {
            this.value = new VerboseLevel(Arrays.stream(logTypes).map(log_types -> log_types.value).collect(Collectors.toSet()));
        }
    }

    @Override
    public String toString() {
        return "VerboseLevel{" + description + '}';
    }
}
