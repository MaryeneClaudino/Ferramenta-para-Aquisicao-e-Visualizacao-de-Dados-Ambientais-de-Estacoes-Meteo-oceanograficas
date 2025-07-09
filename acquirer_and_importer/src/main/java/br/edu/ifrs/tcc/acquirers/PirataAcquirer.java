package br.edu.ifrs.tcc.acquirers;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import br.edu.ifrs.tcc.aux.Utils;
import br.edu.ifrs.tcc.model.Station;

public class PirataAcquirer {
    private final Station station;
    private final boolean acquireAllFiles;

    public PirataAcquirer(Station station, boolean acquireAllFiles) {
        this.station = station;
        this.acquireAllFiles = acquireAllFiles;
    }

    public ArrayList<File> acquireFiles(String temporaryFolderPath) {
        ArrayList<File> acquiredFiles = new ArrayList<>();

        String url = this.getUrlFiles();
        try {
            WebClient webClient = new WebClient();
            HtmlPage page = webClient.getPage(url);
            String xpath = "(//a[contains(@href,'gz')])";
            List<HtmlElement> downloadfiles = page.getByXPath(xpath);
            if (downloadfiles.isEmpty()) {
                System.out.println("No files found!");
            } else {
                for (HtmlElement htmlItem : downloadfiles) {
                    String downloadURL = "https://www.pmel.noaa.gov" +
                            htmlItem.getAttribute("href").trim();
                    String fileName = htmlItem.getTextContent().split(".gz")[0].trim();
                    Page invoiceGz = webClient.getPage(downloadURL);
                    if (invoiceGz.getWebResponse().getContentType().equals("application/x-gzip")) {
                        System.out.println("creatign file: " + fileName);
                        IOUtils.copy(invoiceGz.getWebResponse().getContentAsStream(),
                                new FileOutputStream(temporaryFolderPath + "/" + fileName));
                    }
                }
                acquiredFiles.addAll(Utils.listFilesFromDir(new File(temporaryFolderPath), ".ascii"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return acquiredFiles;
    }

    private LocalDateTime getTheMostCurrentFileDate() {
        File dir = new File(station.path_data_files);
        ArrayList<File> files = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.of(1970, 1, 2, 0, 0, 0);

        if (dir.exists()) {
            files = Utils.listFilesFromDir(dir, ".csv");
            for (File file : files) {
                String[] spt = file.getName().split("_");
                String[] sptDate = spt[spt.length - 1].split("\\.")[0].split("-");
                LocalDateTime fileDate = LocalDateTime.of(Integer.parseInt(sptDate[0]), Integer.parseInt(sptDate[1]),
                        Integer.parseInt(sptDate[2]), 0, 0, 0);
                if (fileDate.isAfter(currentDate)) {
                    currentDate = fileDate;
                }
            }
        }

        return currentDate;
    }

    private LocalDateTime getDateFrom() {
        LocalDateTime from = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        if (!this.acquireAllFiles) {
            from = getTheMostCurrentFileDate().minusDays(1);
        }
        return from;
    }

    private String getUrlFiles() {
        LocalDateTime from = getDateFrom();
        LocalDateTime to = LocalDateTime.now();

        return "https://www.pmel.noaa.gov/cgi-tao/cover.cgi?P1=met.rad.lw.rain.bp.t.sss.s.ssd.d.cur&P2=hr&P3=" + from
                .getYear() + "&P4=" + from.getMonthValue() + "&P5=" + from.getDayOfMonth() + "&P6="
                + to.getYear() + "&P7=" + to.getMonthValue() + "&P8=" + to.getDayOfMonth()
                + "&P9=ascii&P10=zip&P11=anonymous&P12=anonymous&P13=anonymous&P14=anonymous&P15=buoy&P16=disdel_drupal&P17=mean&P18=mean&P19=tser&P20=site&P21="
                + this.station.name.toLowerCase() + "&script=disdel/deliv-buoy-disdel-v79.csh";
    }
}