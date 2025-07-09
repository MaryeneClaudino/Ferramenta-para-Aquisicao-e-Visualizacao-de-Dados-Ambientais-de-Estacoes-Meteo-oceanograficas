package br.edu.ifrs.tcc.aux;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static void createDirIfNotExisting(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static ArrayList<String> getLineWithoutBlanks(String line) {
        ArrayList<String> ret = new ArrayList<>();
        String[] aux = line.trim().split(" ");

        for (int i = 0; i < aux.length; i++) {
            if (!aux[i].isBlank() || !aux[i].isEmpty()) {
                ret.add(aux[i]);
            }
        }

        return ret;
    }

    public static String getAsCSVLine(String line, Integer nCols) {
        ArrayList<String> lineSpt = getLineWithoutBlanks(line);
        String csvLine = "";
        try {
            for (int i = 0; i < lineSpt.size(); i++) {
                if (i < lineSpt.size() - 1) {
                    csvLine += lineSpt.get(i) + ";";
                } else {
                    csvLine += lineSpt.get(i);
                }
            }

            if (csvLine.split(";", -1).length != nCols) {
                throw new Exception();
            }
        } catch (Exception ex) {
            String[] spt = csvLine.split(";");

            for (int i = 0; i < spt.length; i++) {
                boolean dashInMiddle = spt[i].indexOf("-", 1) != 0 && spt[i].indexOf("-", 1) != -1 ? true : false;

                if (dashInMiddle) {
                    int index = spt[i].indexOf("-", 1);
                    spt[i] = spt[i].substring(0, index) + ";-"
                            + spt[i].substring(index + 1);
                }
            }

            csvLine = "";
            for (int i = 0; i < spt.length; i++) {
                if (i < spt.length - 1) {
                    csvLine += spt[i] + ";";
                } else {
                    csvLine += spt[i];
                }
            }
        }
        return csvLine;
    }

    public static String getDataFromLineTime(String line) {
        ArrayList<String> lineTime = getLineWithoutBlanks(line);

        return lineTime.get(2) + lineTime.get(3) + lineTime.get(4);
    }

    public static String transformDateIntoTimestamp(String dateTime) {
        String date = dateTime.split(" ")[0];
        String time = dateTime.split(" ")[1];

        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" +
                date.substring(6) + " " + time.substring(0, 2)
                + ":" + time.substring(2) + ":" + "00";
    }

    public static ArrayList<String> getFileLines(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return new ArrayList<>(lines);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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

    public static String calculateMD5(File file) {
        return calculateMD5(Utils.getFileLines(file));
    }

    public static String calculateMD5(ArrayList<String> arr) {
        StringBuilder builder = new StringBuilder();
        for (String s : arr) {
            builder.append(s);
        }
        String str = builder.toString();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(), 0, str.length());
            String md5File = new BigInteger(1, md.digest()).toString(16);
            if (md5File.length() < 32) {
                int cout = 32 - md5File.length();
                for (int i = 0; i < cout; i++) {
                    md5File = "0" + md5File;
                }
            }
            return md5File;
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<File> listFilesFromDir(File dir, String ext) {
        ArrayList<File> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(dir.getAbsolutePath()))) {
            List<String> filesPath = walk.filter(Files::isRegularFile).map(x -> x.toString())
                    .collect(Collectors.toList());
            for (String path : filesPath) {
                if (path.toUpperCase().endsWith(ext.toUpperCase())) {
                    files.add(new File(path));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Collections.sort(files);
        return files;
    }
}