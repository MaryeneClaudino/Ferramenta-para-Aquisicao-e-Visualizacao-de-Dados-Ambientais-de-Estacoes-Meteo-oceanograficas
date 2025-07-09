package br.edu.ifrs.tcc.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.json.JSONArray;
import br.edu.ifrs.tcc.aux.Utils;
import br.edu.ifrs.tcc.model.SectionKey;
import br.edu.ifrs.tcc.model.Station;

public class PmelProcessor {

    private final ArrayList<File> dataFiles;
    private final Station station;

    public PmelProcessor(ArrayList<File> dataFiles, Station station) {
        this.dataFiles = dataFiles;
        this.station = station;
    }

    public ArrayList<File> process(String path) {
        ArrayList<File> prcessedFiles = new ArrayList<>();
        JSONArray file_out_types = station.acquirer_importer_cfg.getJSONArray("file_out_types");
        String dataRegularity = station.acquirer_importer_cfg.getString("regularity");
        for (int t = 0; t < file_out_types.length(); t++) {
            HashMap<String, ArrayList<String>> allSections = this.getFileAllSessions(file_out_types.get(t).toString());
            ArrayList<String> allTimestamps = new ArrayList<>();
            for (String key : allSections.keySet()) {
                ArrayList<String> lines = allSections.get(key);
                this.getAllTimestamp(allTimestamps, lines);
            }

            allTimestamps.sort(null);

            try {
                // separa cada arquivo em arquivos diários
                HashMap<String, ArrayList<String>> dailyFilesMap = new HashMap<>();
                ArrayList<String> modifiedLines = this.getAllLines(file_out_types.get(t).toString(), allTimestamps,
                        allSections);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("header", modifiedLines.remove(0));

                for (String origLine : modifiedLines) {
                    String lineDatePart = origLine.split(";")[0].split(" ")[0];
                    if (!dailyFilesMap.containsKey(lineDatePart)) {
                        ArrayList<String> dateLines = new ArrayList<>();
                        dateLines.add(headers.get("header"));
                        dailyFilesMap.put(lineDatePart, dateLines);
                    }
                    dailyFilesMap.get(lineDatePart).add(origLine);
                }

                // salvar arquivos diarios
                String origFileName = file_out_types.get(t).toString() + "_" +
                        this.station.name.toLowerCase() + "_" + dataRegularity;
                ArrayList<String> orderedKeySet = new ArrayList(dailyFilesMap.keySet());
                Collections.sort(orderedKeySet);

                File dir = new File(path);

                ArrayList<File> repoFiles = Utils.listFilesFromDir(dir, ".csv");
                for (String key : orderedKeySet) {
                    boolean isFilesIguals = false;
                    for (File repoFile : repoFiles) {
                        if (repoFile.getName().equals(origFileName.split("\\.")[0] + "_" + key +
                                ".csv")) {
                            isFilesIguals = true;
                            if (!(Utils.calculateMD5(repoFile))
                                    .equals(Utils.calculateMD5(dailyFilesMap.get(key)))) {
                                repoFile.delete();
                                File nf = new File(path + "/" + origFileName.split("\\.")[0] + "_"
                                        + key + ".csv");
                                Utils.writeLinesToFile(nf, dailyFilesMap.get(key), true);
                            }
                        }
                    }
                    if (!isFilesIguals) {
                        File nf = new File(
                                path + "/" + origFileName.split("\\.")[0] + "_" + key + ".csv");
                        Utils.writeLinesToFile(nf, dailyFilesMap.get(key), true);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        prcessedFiles.addAll(Utils.listFilesFromDir(new File(path), ".csv"));

        return prcessedFiles;
    }

    private HashMap<String, ArrayList<String>> getFileAllSessions(String fileOutType) {
        HashMap<String, ArrayList<String>> allSections = new HashMap<>();
        String missingData = "";
        try {
            for (File currentFile : dataFiles) {
                if (this.isFileTypeCorrect(fileOutType, currentFile)) {
                    ArrayList<String> currentLines = Utils.getFileLines(currentFile);
                    ArrayList<String> preSectionLines = new ArrayList<>();
                    int indexLine = 0;
                    for (int i = 0, found = 0; found == 0 && i < currentLines.size(); i++) {
                        String currentLine = currentLines.get(i).trim();
                        if (!currentLine.startsWith("Time: ")) {
                            preSectionLines.add(currentLine);
                            if (currentLine.startsWith("Units:")) {
                                missingData = this.getValueMissingData(currentLines.get(i));
                            }
                        } else {
                            found = 1;
                            indexLine = i;
                        }
                    }
                    int index = 1;
                    for (int i = indexLine; i < currentLines.size(); i++) {
                        ArrayList<String> sectionLines = new ArrayList<>();
                        String diff = Utils.getDataFromLineTime(currentLines.get(indexLine));
                        if (!fileOutType.toUpperCase().equals("MET")) {
                            currentLines.remove(0);
                        }
                        String depth = Utils.getAsCSVLine(currentLines.get(indexLine + 1), null);
                        String header = Utils.getAsCSVLine(currentLines.get(indexLine + 2), null);
                        for (int s = (indexLine + 3), found = 0; found == 0 && s < currentLines.size(); s++) {
                            String currentLine = currentLines.get(s).trim();
                            if (!currentLine.startsWith("Time: ")) {
                                sectionLines.add(Utils.getAsCSVLine(currentLine, header.split(";").length));
                            } else {
                                found = 1;
                                indexLine = s;
                            }

                            if (s == currentLines.size() - 1) {
                                i = s;
                            }
                        }

                        SectionKey key = new SectionKey(index, currentFile.getName(), this.station.name.toLowerCase(),
                                depth, header, diff, missingData);
                        index++;
                        allSections.put(key.getKey(), sectionLines);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return allSections;
    }

    private ArrayList<String> getAllLines(String type, ArrayList<String> allTimestamps,
            HashMap<String, ArrayList<String>> allSections) {
        ArrayList<String> fileLines = new ArrayList<>();
        String headerFile = this.getClassHeader(type);
        fileLines.add(headerFile);
        String[] headersCol = headerFile.split(";");
        String missingData = "";
        int printCount = 0;
        for (String timestamp : allTimestamps) {
            String[] params = new String[headersCol.length - 1];
            String fileLine = "";
            fileLine += Utils.transformDateIntoTimestamp(timestamp) + ";";
            String paramData = null;
            for (String key : allSections.keySet()) {
                ArrayList<String> lines = allSections.get(key);
                String headerOrig = this.getHeadersKey(key);
                String depth = this.getDepthKey(key);
                missingData = this.getMissingDataKey(key);
                int numParamsHeaderOrig = this.getNumOfParamsFromDepth(depth, type);
                for (String line : lines) {
                    int count = 0;
                    String timestampLine = line.split(";")[0] + " " + line.split(";")[1];
                    if (timestamp.equals(timestampLine)) {
                        for (int i = 1; i < headersCol.length; i++) {
                            paramData = this.getColData(headerOrig, depth, line, headersCol[i], type, missingData);
                            if (paramData != null) {
                                // O parse para Double é usado para casos vem -99.90 misturado no arquivo
                                if (Double.parseDouble(paramData) == Double.parseDouble(missingData)) {
                                    params[i - 1] = null;
                                } else {
                                    params[i - 1] = paramData;
                                }
                                count++;
                            }
                            if (count == numParamsHeaderOrig) {
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    params[i] = "";
                }
                if (i < params.length - 1) {
                    fileLine += params[i] + ";";
                } else {
                    fileLine += params[i];
                    fileLines.add(fileLine);
                    if (printCount > 1000) {
                        System.out.println(fileLine);
                        printCount = 0;
                    }
                    printCount++;
                }
            }
        }

        return fileLines;
    }

    private void getAllTimestamp(ArrayList<String> allTimestamps, ArrayList<String> allLines) {
        for (String line : allLines) {
            String timestamp = line.split(";")[0] + " " + line.split(";")[1];
            if (!allTimestamps.contains(timestamp)) {
                allTimestamps.add(timestamp);
            }
        }
    }

    private String getValueMissingData(String line) {
        String[] spt = line.split(",");
        for (String part : spt) {
            if (part.contains("=")) {
                return part.split("=")[0].trim();
            }
        }
        return null;
    }

    private String getHeadersKey(String key) {
        return key.split("_")[3];
    }

    private String getDepthKey(String key) {
        return key.split("_")[2];
    }

    private String getMissingDataKey(String key) {
        return key.split("_")[5];
    }

    private int getNumOfParamsFromDepth(String depth, String type) {
        int count = 0;
        String[] depthSpt = depth.split(";");
        for (int i = 1; i < depthSpt.length; i++) {
            if (!depthSpt[i].toUpperCase().startsWith("INSTRUMENT")
                    && !depthSpt[i].toUpperCase().startsWith("QUALITY")) {
                if (type.toUpperCase().equals("DOPPLER")) {
                    count += 2;
                } else {
                    count++;
                }
            } else {
                break;
            }
        }
        return count;
    }

    private int searchForParam(String[] params, String param) {
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals(param)) {
                return i;
            }
        }
        return -1;
    }

    private boolean valueNaN(String value) {
        if (value.toUpperCase().contains("NAN")) {
            return true;
        }
        return false;
    }

    private String getColData(String header, String depth, String line, String param, String type, String missingData) {
        String[] headerCol = header.split(";");
        String[] depthCol = depth.split(";");
        String[] lineCol = line.split(";");
        String valueParam = null;

        int index = -1;
        if (!type.toUpperCase().equals("MET")) {
            for (int i = 2; i < header.length(); i++) {
                if (headerCol[i].toUpperCase().startsWith("ID") || headerCol[i].toUpperCase().startsWith("SD")
                        || depthCol[i - 1].toUpperCase().startsWith("QUALITY")
                        || depthCol[i - 1].toUpperCase().startsWith("INSTRUMENT")) {
                    break;
                }

                if (!headerCol[i].equals("SST") && !headerCol[i].equals("SSS") && !headerCol[i].equals("SSD")) {
                    headerCol[i] = headerCol[i] + depthCol[i - 1];
                }

                if (param.equals(headerCol[i])) {
                    index = i;
                }

                if (param.startsWith("CUR_SPD") || param.startsWith("CUR_DIR")) {
                    String depthParam = param.substring(7, param.length());

                    int zonal = searchForParam(headerCol, "ZONAL" + depthParam);
                    int merid = searchForParam(headerCol, "MERID" + depthParam);

                    if (zonal != -1 && merid != -1) {
                        Double cur_u = Double.parseDouble(lineCol[zonal]);
                        Double cur_v = Double.parseDouble(lineCol[merid]);

                        if (cur_u == Double.parseDouble(missingData) || cur_v == Double.parseDouble(missingData)) {
                            valueParam = missingData;
                        } else {
                            valueParam = param.startsWith("CUR_SPD") ? this.calcCurrentSpeed(cur_u, cur_v)
                                    : this.calcCurrentDirection(cur_u, cur_v);
                        }
                    }
                }
            }
        } else {
            index = searchForParam(headerCol, param);
        }

        if (index != -1) {
            if (!valueNaN(lineCol[index])) {
                valueParam = lineCol[index];
            } else {
                valueParam = null;
            }
        }
        return valueParam;
    }

    private String getClassHeader(String type) {
        if (type.toUpperCase().equals("MET")) {
            return "Timestamp;UWND;VWND;WSPD;WDIR;AIRT;SST;RH;SLP;PREC;SWRad;LWRad";
        } else if (type.toUpperCase().equals("PERFIL_TEMP")) {
            return "Timestamp;SST;TEMP2;TEMP3;TEMP4;TEMP5;TEMP7;TEMP8;TEMP9;TEMP10;TEMP11;TEMP13;TEMP14;TEMP15;TEMP16;TEMP17;TEMP20;TEMP23;TEMP24;TEMP25;TEMP26;TEMP28;TEMP29;TEMP30;TEMP31;TEMP33;TEMP34;TEMP35;TEMP36;TEMP37;TEMP40;TEMP41;TEMP43;TEMP45;TEMP46;TEMP47;TEMP48;TEMP49;TEMP50;TEMP51;TEMP53;TEMP55;TEMP57;TEMP59;TEMP60;TEMP61;TEMP62;TEMP63;TEMP66;TEMP70;TEMP75;TEMP80;TEMP82;TEMP83;TEMP85;TEMP87;TEMP90;TEMP91;TEMP98;TEMP99;TEMP100;TEMP101;TEMP102;TEMP103;TEMP107;TEMP110;TEMP112;TEMP120;TEMP121;TEMP122;TEMP123;TEMP125;TEMP130;TEMP132;TEMP137;TEMP140;TEMP142;TEMP150;TEMP151;TEMP153;TEMP157;TEMP160;TEMP161;TEMP162;TEMP170;TEMP175;TEMP179;TEMP180;TEMP183;TEMP199;TEMP200;TEMP201;TEMP203;TEMP207;TEMP222;TEMP225;TEMP232;TEMP250;TEMP251;TEMP275;TEMP282;TEMP285;TEMP300;TEMP301;TEMP382;TEMP400;TEMP470;TEMP482;TEMP500;TEMP750;TEMP1000;TEMP2000";
        } else if (type.toUpperCase().equals("PERFIL_SAL")) {
            return "Timestamp;SSS;SAL3;SAL5;SAL7;SAL10;SAL11;SAL14;SAL15;SAL16;SAL20;SAL25;SAL26;SAL29;SAL30;SAL33;SAL35;SAL37;SAL40;SAL45;SAL50;SAL51;SAL57;SAL60;SAL62;SAL70;SAL75;SAL76;SAL79;SAL80;SAL83;SAL87;SAL100;SAL101;SAL102;SAL112;SAL120;SAL125;SAL132;SAL137;SAL140;SAL150;SAL151;SAL175;SAL183;SAL200;SAL201;SAL225;SAL250;SAL275;SAL300;SAL400;SAL500;SAL750";
        } else if (type.toUpperCase().equals("PERFIL_DENS")) {
            return "Timestamp;SSD;DEN3;DEN5;DEN7;DEN10;DEN11;DEN14;DEN15;DEN16;DEN20;DEN25;DEN26;DEN29;DEN30;DEN33;DEN35;DEN37;DEN40;DEN45;DEN50;DEN51;DEN57;DEN60;DEN62;DEN70;DEN75;DEN76;DEN79;DEN80;DEN83;DEN87;DEN100;DEN101;DEN102;DEN112;DEN120;DEN125;DEN132;DEN137;DEN140;DEN150;DEN151;DEN175;DEN183;DEN200;DEN201;DEN225;DEN250;DEN275;DEN300;DEN400;DEN500;DEN750";
        } else if (type.toUpperCase().equals("DOPPLER")) {
            return "Timestamp;ZONAL3;MERID3;CUR_SPD3;CUR_DIR3;ZONAL10;MERID10;CUR_SPD10;CUR_DIR10;ZONAL12;MERID12;CUR_SPD12;CUR_DIR12;ZONAL15;MERID15;CUR_SPD15;CUR_DIR15;ZONAL25;MERID25;CUR_SPD25;CUR_DIR25;ZONAL38;MERID38;CUR_SPD38;CUR_DIR38;ZONAL40;MERID40;CUR_SPD40;CUR_DIR40;ZONAL45;MERID45;CUR_SPD45;CUR_DIR45;ZONAL50;MERID50;CUR_SPD50;CUR_DIR50;ZONAL52;MERID52;CUR_SPD52;CUR_DIR52;ZONAL60;MERID60;CUR_SPD60;CUR_DIR60;ZONAL80;MERID80;CUR_SPD80;CUR_DIR80;ZONAL82;MERID82;CUR_SPD82;CUR_DIR82;ZONAL100;MERID100;CUR_SPD100;CUR_DIR100;ZONAL102;MERID102;CUR_SPD102;CUR_DIR102;ZONAL120;MERID120;CUR_SPD120;CUR_DIR120;ZONAL150;MERID150;CUR_SPD150;CUR_DIR150;ZONAL160;MERID160;CUR_SPD160;CUR_DIR160;ZONAL182;MERID182;CUR_SPD182;CUR_DIR182;ZONAL200;MERID200;CUR_SPD200;CUR_DIR200;ZONAL202;MERID202;CUR_SPD202;CUR_DIR202;ZONAL250;MERID250;CUR_SPD250;CUR_DIR250;ZONAL300;MERID300;CUR_SPD300;CUR_DIR300";
        }

        return "";
    }

    private boolean isFileTypeCorrect(String fileOutType, File file) {
        if (fileOutType.toUpperCase().equals("MET")) {
            if (file.getName().startsWith("met" + this.station.name.toLowerCase())
                    || file.getName().startsWith("rain" + this.station.name.toLowerCase())
                    || file.getName().startsWith("rad" + this.station.name.toLowerCase())
                    || file.getName().startsWith("bp" + this.station.name.toLowerCase())
                    || file.getName().startsWith("lw" + this.station.name.toLowerCase())) {
                return true;
            }
        } else if (fileOutType.toUpperCase().equals("PERFIL_TEMP")) {
            if (file.getName().startsWith("t" + this.station.name.toLowerCase())) {
                return true;
            }
        } else if (fileOutType.toUpperCase().equals("PERFIL_SAL")) {
            if (file.getName().startsWith("s" + this.station.name.toLowerCase())) {
                return true;
            }
        } else if (fileOutType.toUpperCase().equals("PERFIL_DENS")) {
            if (file.getName().startsWith("d" + this.station.name.toLowerCase())) {
                return true;
            }
        } else if (fileOutType.toUpperCase().equals("DOPPLER")) {
            if (file.getName().startsWith("cur" + this.station.name.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private String calcCurrentSpeed(Double cur_u, Double cur_v) {
        return String.format("%.5f", Math.sqrt(Math.pow(cur_u, 2) + Math.pow(cur_v, 2))).replace(",", ".");
    }

    private String calcCurrentDirection(Double cur_u, Double cur_v) {
        Double cur_dir = (Math.toDegrees(Math.atan2(cur_u, cur_v)));
        cur_dir = cur_dir < 0 ? cur_dir + 360 : cur_dir;
        cur_dir = cur_dir % 360;

        return String.format("%.5f", cur_dir).replace(",", ".");
    }
}