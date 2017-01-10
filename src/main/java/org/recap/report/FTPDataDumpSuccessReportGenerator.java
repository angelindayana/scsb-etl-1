package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.recap.ReCAPConstants;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.datadump.DataDumpSuccessReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 29/9/16.
 */

@Component
public class FTPDataDumpSuccessReportGenerator implements ReportGeneratorInterface {

    @Autowired
    ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(ReCAPConstants.BATCH_EXPORT_SUCCESS) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(ReCAPConstants.FTP) ? true : false;
    }

    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(ReCAPConstants.BATCH_EXPORT) ? true : false;
    }

    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {

        if(!CollectionUtils.isEmpty(reportEntities)) {
            DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
            List<DataDumpSuccessReport> dataDumpSuccessReportList = new ArrayList<>();
            for(ReportEntity reportEntity : reportEntities) {
                DataDumpSuccessReport dataDumpSuccessReportRecord = new DataDumpSuccessReportGenerator().prepareDataDumpCSVSuccessRecord(reportEntity);
                dataDumpSuccessReportList.add(dataDumpSuccessReportRecord);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            dataDumpSuccessReport.setReportType(reportEntity.getType());
            dataDumpSuccessReport.setInstitutionName(reportEntity.getInstitutionName());
            dataDumpSuccessReport.setFileName(fileName);
            dataDumpSuccessReport.setDataDumpSuccessReportList(dataDumpSuccessReportList);
            producerTemplate.sendBody(ReCAPConstants.DATADUMP_SUCCESS_REPORT_FTP_Q, dataDumpSuccessReport);
            DateFormat df = new SimpleDateFormat(ReCAPConstants.DATE_FORMAT_FOR_FILE_NAME);
            String generatedFileName = FilenameUtils.removeExtension(dataDumpSuccessReport.getFileName()) + "-" + dataDumpSuccessReport.getReportType() + "-" + df.format(new Date()) + ".csv";
            return generatedFileName;
        }

        return null;
    }
}
