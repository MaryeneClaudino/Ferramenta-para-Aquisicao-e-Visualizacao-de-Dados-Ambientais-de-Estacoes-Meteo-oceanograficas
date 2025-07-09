package br.edu.ifrs.tcc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import br.edu.ifrs.tcc.acquirers.PirataAcquirer;
import br.edu.ifrs.tcc.aux.Utils;
import br.edu.ifrs.tcc.importers.PmelDataImporter;
import br.edu.ifrs.tcc.model.Station;
import br.edu.ifrs.tcc.processors.PmelProcessor;
import br.edu.ifrs.tcc.savers.CopyFilesSaver;
import br.edu.ifrs.tcc.services.StationService;

public class Main {
    public static void main(String[] args) {
        try {
            // PIRATA - 0N3W
            // args = new String[] { "0n3w", "new" };
            // PIRATA - 2N10W
            // args = new String[] { "2n10w", "new" };
            // PIRATA - 10S10W
            // args = new String[] { "10s10w", "new" };
            // PIRATA - 15N65E
            // args = new String[] { "15n65e", "all" };
            // TAO
            // args = new String[] { "0n147e", "all" };

            if (args.length == 0) {
                System.err.println("Sintaxe:");
                System.err.println(
                        "java -jar rq-aq.jar name_station new|all");
            }

            if (args.length == 2) {
                System.setProperty("user.timezone", "UTC");
                StationService stationService = new StationService();

                Station station = stationService.getStationByName(args[0].toUpperCase());
                System.out.println("Estação " + station.name);

                boolean acquireAllFiles = args[1].toLowerCase().equals("all");

                Utils.createDirIfNotExisting("/srv/pmel");

                Path acquirerTmpDir = Files.createTempDirectory(Paths.get("/srv/pmel"), "aquirerTemporaryFolder");

                PirataAcquirer acquirer = new PirataAcquirer(station, acquireAllFiles);

                ArrayList<File> acquiredFiles = acquirer.acquireFiles(acquirerTmpDir.toString());

                Path processorTmpDir = Files.createTempDirectory(Paths.get("/srv/pmel"), "processTemporaryFolder");

                PmelProcessor processor = new PmelProcessor(acquiredFiles, station);

                ArrayList<File> processedFiles = processor.process(processorTmpDir.toString());

                CopyFilesSaver saver = new CopyFilesSaver(processedFiles,
                        station.path_data_files);

                ArrayList<File> filesToImport = saver.save();

                if (filesToImport.size() > 0) {
                    PmelDataImporter importer = new PmelDataImporter(station, filesToImport);

                    importer.importerDataIntoDB();
                } else {
                    System.out.println("Não foram encontrados arquivos novos para importar!");
                }

                // Apagou a PASTA temporaria
                FileUtils.deleteDirectory(new File(acquirerTmpDir.toString()));

                // Apagou a PASTA temporaria
                FileUtils.deleteDirectory(new File(processorTmpDir.toString()));
                System.out.println("Aquisição e importação dos arquivos da estação " + station.name + " finalizados");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}