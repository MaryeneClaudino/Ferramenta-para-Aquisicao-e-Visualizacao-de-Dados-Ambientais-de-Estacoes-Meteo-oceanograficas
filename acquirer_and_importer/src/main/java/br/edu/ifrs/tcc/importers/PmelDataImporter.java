package br.edu.ifrs.tcc.importers;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import br.edu.ifrs.tcc.aux.Utils;
import br.edu.ifrs.tcc.model.Data;
import br.edu.ifrs.tcc.model.Station;
import br.edu.ifrs.tcc.services.DataService;
import br.edu.ifrs.tcc.services.ParameterService;

public class PmelDataImporter {
    private final Station station;
    private final ArrayList<File> filesToImport;
    private final ParameterService paramService = new ParameterService();
    private final DataService dataService = new DataService();

    public PmelDataImporter(Station station, ArrayList<File> filesToImport) {
        this.station = station;
        this.filesToImport = filesToImport;
    }

    public void importerDataIntoDB() {
        ArrayList<Data> dataToInsert = new ArrayList<>();
        for (File file : filesToImport) {
            ArrayList<String> fileLines = Utils.getFileLines(file);
            String header = fileLines.remove(0);
            ArrayList<String> headerWithParamId = this.getHeaderWithParamId(header);

            for (String line : fileLines) {
                String[] sptLine = line.split(";", -1);
                String timestamp = sptLine[0];
                for (int i = 1; i < headerWithParamId.size(); i++) {
                    if (sptLine[i] != "" || !sptLine[i].trim().isEmpty()) {
                        dataToInsert.add(
                                new Data(station.id, Integer.parseInt(headerWithParamId.get(i)),
                                        Timestamp.valueOf(timestamp), Double.parseDouble(sptLine[i])));
                    }
                }
            }
        }

        int printCount = 0;
        for (Data d : dataToInsert) {
            printCount++;
            if (printCount >= 1000) {
                System.out.println(d);
                printCount = 0;
            }
            try {
                dataService.insert(d);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getHeaderWithParamId(String header) {
        ArrayList<String> headerParamId = new ArrayList<>();
        String[] sptHeader = header.split(";");
        headerParamId.add(sptHeader[0]);

        for (String h : sptHeader) {
            if (!h.toUpperCase().equals("TIMESTAMP")) {
                headerParamId.add((paramService.getParameterByName(h).id).toString());
            }
        }

        return headerParamId;
    }
}