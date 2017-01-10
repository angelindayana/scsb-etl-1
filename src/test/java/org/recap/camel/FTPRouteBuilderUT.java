package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVFailureRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 21/7/16.
 */
public class FTPRouteBuilderUT extends BaseTestCase{

    @Value("${ftp.userName}")
    String ftpUserName;

    @Value("${ftp.remote.server}")
    String ftpRemoteServer;

    @Value("${ftp.knownHost}")
    String ftpKnownHost;

    @Value("${ftp.privateKey}")
    String ftpPrivateKey;

    @Value("${etl.report.directory}")
    private String reportDirectoryPath;

    @Autowired
    ProducerTemplate producer;

    @Test
    public void uploadFileToFTP() throws Exception {

        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new FailureReportReCAPCSVRecord();
        failureReportReCAPCSVRecord.setOwningInstitution("PUL");
        failureReportReCAPCSVRecord.setOwningInstitutionBibId("1111");
        failureReportReCAPCSVRecord.setOwningInstitutionHoldingsId("2222");
        failureReportReCAPCSVRecord.setLocalItemId("333333333");
        failureReportReCAPCSVRecord.setItemBarcode("4444");
        failureReportReCAPCSVRecord.setCustomerCode("PA");
        failureReportReCAPCSVRecord.setTitle("History, of Science");
        failureReportReCAPCSVRecord.setCollectionGroupDesignation("open");
        failureReportReCAPCSVRecord.setCreateDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setLastUpdatedDateItem(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        failureReportReCAPCSVRecord.setExceptionMessage("exception");
        failureReportReCAPCSVRecord.setErrorDescription("error");

        ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();
        reCAPCSVFailureRecord.setFileName("test.xml");
        reCAPCSVFailureRecord.setInstitutionName("PUL");
        reCAPCSVFailureRecord.setReportType(org.recap.ReCAPConstants.FAILURE);
        assertNotNull(failureReportReCAPCSVRecord.getCreateDateItem());
        assertNotNull(failureReportReCAPCSVRecord.getLastUpdatedDateItem());
        reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(Arrays.asList(failureReportReCAPCSVRecord));

        producer.sendBody(ReCAPConstants.FTP_SUCCESS_Q, reCAPCSVFailureRecord);

        Thread.sleep(2000);
    }
}