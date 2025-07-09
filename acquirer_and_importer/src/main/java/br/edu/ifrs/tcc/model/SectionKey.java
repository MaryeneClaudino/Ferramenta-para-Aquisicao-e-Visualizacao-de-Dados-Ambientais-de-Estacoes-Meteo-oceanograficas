package br.edu.ifrs.tcc.model;

public class SectionKey {
    private int index;
    private String fileName;
    private String stationName;
    private String depth;
    private String header;
    private String diff;
    private String missingData;

    public SectionKey(int index, String fileName, String stationName, String depth, String header, String diff,
            String missingData) {
        this.index = index;
        this.fileName = fileName;
        this.stationName = stationName;
        this.depth = depth;
        this.header = header;
        this.diff = diff;
        this.missingData = missingData;
    }

    public String getKey() {
        return this.index + "_" + this.getFileType(this.fileName, this.stationName) + "_" + this.depth + "_"
                + this.header + "_" + this.diff + "_" + this.missingData;
    }

    private String getFileType(String fileName, String stationName) {
        return fileName.split(stationName)[0];
    }
}
