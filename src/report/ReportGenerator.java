package report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import model.Mahasiswa;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    // compile jrxml from resources and export PDF
    public static String generateReport(List<Mahasiswa> data, int idFrom, int idTo, InputStream logoStream, String outputPath) throws JRException {
        // load jrxml from resources (we will place jrxml in resources/report/mahasiswa_report.jrxml)
        InputStream jrxmlStream = ReportGenerator.class.getResourceAsStream("/report/mahasiswa_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(data);

        Map<String, Object> params = new HashMap<>();
        params.put("Logo", logoStream); // logo parameter
        params.put("ID_FROM", idFrom);
        params.put("ID_TO", idTo);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, datasource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

        return outputPath;
    }
}
