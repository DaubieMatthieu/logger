package logger;

import java.io.Serializable;
import java.util.Objects;

public class LogType implements Serializable {
    public String label;
    public Style[] styles;

    //mandatory for deserialization
    @SuppressWarnings("unused")
    public LogType() {
    }

    public LogType(String label, Style... styles) {
        this.label = label;
        this.styles = styles;
    }

    public enum defaults {
        INFO("Info", Style.CYAN_BOLD_BRIGHT),
        ERROR("Error", Style.RED_BOLD),
        WARNING("Warning", Style.YELLOW_BOLD_BRIGHT),
        DETAIL("Detail", Style.WHITE_BRIGHT),
        DEBUG("Debug", Style.WHITE),
        ;

        public final LogType value;

        defaults(String label, Style... styles) {
            this.value = new LogType(label, styles);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogType logType = (LogType) o;
        return Objects.equals(label, logType.label);
    }

    @Override
    //allows LogTypes to be identified after deserialization
    public int hashCode() {
        return Objects.hash(label);
    }
}
