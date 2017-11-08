package org.nerd.kid.preprocessing;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVFIleWriter {
    private static char separator_default = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, separator_default, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separator) throws IOException {
        writeLine(w, values, separator, ' ');
    }

    private static String followCSVformat(String value) {
        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }

    public static void writeLine(Writer w, List<String> values, char separator, char custom) throws IOException {
        boolean first = true;

        if (separator == ' ') {
            separator = separator_default;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separator);
            }
            if (custom == ' ') {
                sb.append(followCSVformat(value));
            } else {
                sb.append(custom).append(followCSVformat(value)).append(custom);
            }
            first = false;

        }
        sb.append("\n");
        w.append(sb.toString());
    }

    public void writeLine(String s) {
    }
}
