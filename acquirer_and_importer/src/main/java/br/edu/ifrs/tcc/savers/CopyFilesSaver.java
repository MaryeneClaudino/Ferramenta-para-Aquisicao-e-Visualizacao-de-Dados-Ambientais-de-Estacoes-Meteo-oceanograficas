package br.edu.ifrs.tcc.savers;

import java.io.File;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import br.edu.ifrs.tcc.aux.Utils;

public class CopyFilesSaver {

    private final String outputPath;
    private final ArrayList<File> files;

    public CopyFilesSaver(ArrayList<File> files, String outputPath) {
        this.files = files;
        this.outputPath = outputPath;
    }

    public ArrayList<File> save() {
        ArrayList<File> filesToImport = new ArrayList<>();
        // Cria a pasta se não existe...
        Utils.createDirIfNotExisting(outputPath);
        try {
            for (File file : files) {
                String localFilePath = outputPath + "/" + file.getName();
                File localOutputFile = new File(localFilePath);

                if (!localOutputFile.exists() || file.length() > localOutputFile.length()) {
                    filesToImport.add(file);
                    // só copia o arquivo...
                    System.out.println("Copiando " + file.getAbsolutePath() + "\nPara "
                            + localOutputFile.getAbsolutePath() + "\n");
                    new File(localOutputFile.getParent()).mkdirs();
                    FileUtils.copyFile(file, localOutputFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filesToImport;
    }
}