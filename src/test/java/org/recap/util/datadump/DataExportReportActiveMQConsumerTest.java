package org.recap.util.datadump;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by peris on 11/11/16.
 */
public class DataExportReportActiveMQConsumerTest {

    @Mock
    ReportDetailRepository mockReportDetailsRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processNewSuccessReportEntity() throws  Exception {
       DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ReCAPConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ReCAPConstants.NUM_RECORDS, "12");
        values.put(ReCAPConstants.NUM_BIBS_EXPORTED, "Num Bibs Exported");
        values.put(ReCAPConstants.BATCH_EXPORT, "Batch Export");
        values.put(ReCAPConstants.REQUEST_ID, "PUL-2017-12-12 11");

        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity reportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);

        assertNotNull(reportEntity);


    }

    @Test
    public void processExistingSuccessReportEntity() throws  Exception {
       DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ReCAPConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ReCAPConstants.NUM_RECORDS, "12");
        values.put(ReCAPConstants.NUM_BIBS_EXPORTED, "NoOfBibsExported");
        values.put(ReCAPConstants.BATCH_EXPORT, ReCAPConstants.BATCH_EXPORT_SUCCESS);
        values.put(ReCAPConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11",ReCAPConstants.BATCH_EXPORT_SUCCESS)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(ReCAPConstants.NUM_RECORDS, "10");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if(reportDataEntity.getHeaderName().equals("NoOfBibsExported")){
                assertEquals("22", reportDataEntity.getHeaderValue());
            }
        }
    }

    @Test
    public void processNewFailureReportEntity() throws  Exception {
       DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ReCAPConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ReCAPConstants.FAILED_BIBS, "2");
        values.put(ReCAPConstants.FAILURE_CAUSE, "Bad happened");
        values.put(ReCAPConstants.BATCH_EXPORT, "Batch Export");
        values.put(ReCAPConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void processExistingFailureReportEntity() throws  Exception {
        DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(ReCAPConstants.REQUESTING_INST_CODE, "PUL");
        values.put(ReCAPConstants.FAILED_BIBS, "2");
        values.put(ReCAPConstants.FAILURE_CAUSE, "Bad happened");
        values.put(ReCAPConstants.BATCH_EXPORT, "Batch Export");
        values.put(ReCAPConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11",ReCAPConstants.BATCH_EXPORT_FAILURE)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(ReCAPConstants.NUM_RECORDS, "3");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if(reportDataEntity.getHeaderName().equals(ReCAPConstants.FAILED_BIBS)){
                assertEquals("5", reportDataEntity.getHeaderValue());
            }
        }
    }

}