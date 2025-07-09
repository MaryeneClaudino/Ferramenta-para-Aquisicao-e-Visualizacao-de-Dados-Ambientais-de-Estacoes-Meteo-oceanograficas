package br.edu.ifrs.tcc.backend.aux;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifrs.tcc.backend.model.Data;

public class Utils {
    public static ArrayList<String> getFileLines(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return new ArrayList<>(lines);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Timestamp> getAllDataTimestamp(List<Data> allData) {
        ArrayList<Timestamp> allTimestamps = new ArrayList<>();

        for (Data data : allData) {
            if (!allTimestamps.contains(data.getTimestamp())) {
                allTimestamps.add(data.getTimestamp());
            }
        }

        return allTimestamps;
    }

    public static String getLine(String[] lines) {
        String strLine = "";

        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) {
                lines[i] = "";
            }
            if (i == lines.length - 1) {
                strLine += lines[i];
            } else {
                strLine += lines[i] + ";";
            }
        }

        return strLine;
    }

    public static void writeLinesToFile(File correctDateFile, ArrayList<String> lines, boolean append)
            throws UnsupportedEncodingException, UnsupportedEncodingException,
            IOException {
        if (lines != null && !lines.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }

            Files.write(correctDateFile.toPath(), sb.toString().getBytes("UTF-8"),
                    StandardOpenOption.CREATE,
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            Files.write(correctDateFile.toPath(), "".getBytes("UTF-8"),
                    StandardOpenOption.CREATE,
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}