package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.recap.controller.swagger.DataDumpRestController;
import org.recap.model.search.SearchRecordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by premkb on 19/8/16.
 */
public class DataDumpRestControllerUT extends BaseControllerUT {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpRestControllerUT.class);

    @Autowired
    private DataDumpRestController dataDumpRestController;

    @Value("${solrclient.url}")
    String solrClientUrl;

    private ExecutorService executorService;

    @Value("${datadump.status.file.name}")
    String dataDumpStatusFileName;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(dataDumpRestController).build();
    }

    @Test
    public void exportIncrementalMarcXmlFormatForHttp() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","CUL")
                .param("fetchType","0")
                .param("transmissionType", "1")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","0")
                .param("emailToAddress","peri.subrahmanya@htcinc.com")
                .param("collectionGroupIds","1,2"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(status == 200);
        System.out.println(contentAsString);
    }

    @Test
    public void exportIncrementalSCSBXmlFormatForHttp() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","CUL")
                .param("fetchType","0")
                .param("transmissionType", "1")
                .param("requestingInstitutionCode","PUL")
                .param("outputFormat","1")
                .param("emailToAddress","peri.subrahmanya@htcinc.com")
                .param("collectionGroupIds","1,2"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(status == 200);
        System.out.println(contentAsString);
    }

    @Test
    public void exportFullDataDumpMarcXmlFormat() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","NYPL")
                .param("fetchType","0")
                .param("transmissionType", "0")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","0")
                .param("emailToAddress","hemalatha.s@htcindia.com")
                .param("collectionGroupIds","1,2"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(ReCAPConstants.DATADUMP_PROCESS_STARTED,mvcResult.getResponse().getContentAsString());
        assertTrue(status == 200);
    }


    @Test
    public void exportFullDataDumpScsbXmlFormat() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","NYPL")
                .param("fetchType","0")
                .param("transmissionType", "0")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","1")
                .param("emailToAddress","hemalatha.s@htcindia.com")
                .param("collectionGroupIds","1,2"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(ReCAPConstants.DATADUMP_PROCESS_STARTED,mvcResult.getResponse().getContentAsString());
        assertTrue(status == 200);
    }

    @Test
    public void exportIncrementalDataDump() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","NYPL,PUL")
                .param("fetchType","1")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","1")
                .param("emailToAddress","hemalatha.s@htcindia.com")
                .param("date","2016-11-23 04:21"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(ReCAPConstants.DATADUMP_PROCESS_STARTED,mvcResult.getResponse().getContentAsString());
        assertTrue(status == 200);
    }

    @Test
    public void exportDeletedRecordsDataDump() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","NYPL,PUL")
                .param("fetchType","2")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","2")
                .param("emailToAddress","peri.subrahmanya@htcinc.com"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(ReCAPConstants.DATADUMP_PROCESS_STARTED,mvcResult.getResponse().getContentAsString());
        assertTrue(status == 200);
    }

    @Test
    public void invalidFetchTypeParameters()throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("institutionCodes","NYPL")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","1")
                .param("emailToAddress","hemalatha.s@htcindia.com")
                .param("fetchType","3"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("1. "+ReCAPConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG+"\n",mvcResult.getResponse().getContentAsString());
        assertTrue(status == 400);
    }

    @Test
    public void invalidIncrementalDumpParameters()throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                .param("fetchType","1")
                .param("requestingInstitutionCode","NYPL")
                .param("outputFormat","1")
                .param("emailToAddress","hemalatha.s@htcindia.com")
                .param("institutionCodes","NYPL"))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals("1. "+ReCAPConstants.DATADUMP_DATE_ERR_MSG+"\n",mvcResult.getResponse().getContentAsString());
        assertTrue(status == 400);
    }

    @Test
    public void getBibsFromSolr() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL","CUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Shared"));
        searchRecordsRequest.setPageSize(10);
        RestTemplate restTemplate = new RestTemplate();
        String url = solrClientUrl + "searchService/searchRecords";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest,headers);
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        Map responseEntityBody = responseEntity.getBody();
        Integer totalPageCount = (Integer) responseEntityBody.get("totalPageCount");
        String totalBibsCount = (String) responseEntityBody.get("totalBibsCount");
        List dataDumpSearchResults = (List) responseEntityBody.get("dataDumpSearchResults");
        assertNotNull(totalPageCount);
        assertNotNull(totalBibsCount);
        assertNotNull(dataDumpSearchResults);
        System.out.println("Total Pages : " + totalPageCount);
        System.out.println("Total Bibs : " + totalBibsCount);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void concurrentHttpDataExport() throws Exception {
        List<Callable<Map<String, String>>> callables = new ArrayList<>();

        for(int i=0; i<5; i++) {
            ConcurrentDataExportCallable concurrentDataExportCallable = new ConcurrentDataExportCallable(mockMvc, "2", "2", "1");
            callables.add(concurrentDataExportCallable);
        }

        List<Future<Map<String, String>>> futures = getFutures(callables);

        List<MvcResult> results = getResults(futures);
        for(MvcResult mvcResult : results) {
            logger.info(mvcResult.getResponse().getContentAsString());
            assertNotNull(mvcResult.getResponse().getContentAsString());
        }
    }

    @Test
    public void concurrentFullDataExport() throws Exception {
        List<Callable<Map<String, String>>> callables = new ArrayList<>();

        for(int i=0; i<5; i++) {
            ConcurrentDataExportCallable concurrentDataExportCallable = new ConcurrentDataExportCallable(mockMvc, "0", "0", "0");
            callables.add(concurrentDataExportCallable);
        }

        List<Future<Map<String, String>>> futures = getFutures(callables);

        List<MvcResult> results = getResults(futures);
        for(MvcResult result : results) {
            logger.info(result.getResponse().getContentAsString());
            assertNotNull(result.getResponse().getContentAsString());
        }
        File file = new File(dataDumpStatusFileName);
        file.delete();
    }

    private List<MvcResult> getResults(List<Future<Map<String, String>>> futures) {
        List<MvcResult> mvcResults = new ArrayList<>();
        for (Iterator<Future<Map<String, String>>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            Object object = null;
            try {
                object = future.get();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
            }
            MvcResult mvcResult = (MvcResult) object;
            mvcResults.add(mvcResult);
        }
        return mvcResults;
    }

    private List<Future<Map<String, String>>> getFutures(List<Callable<Map<String, String>>> callables) {
        List<Future<Map<String, String>>> futures = null;
        try {
            futures = getExecutorService().invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        futures
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
        return futures;
    }

    public class ConcurrentDataExportCallable implements Callable {

        protected MockMvc mockMvc;
        private String fetchType;
        private String outputFormat;
        private String transmissionType;

        public ConcurrentDataExportCallable(MockMvc mockMvc, String fetchType, String outputFormat, String transmissionType) {
            this.mockMvc = mockMvc;
            this.fetchType = fetchType;
            this.outputFormat = outputFormat;
            this.transmissionType = transmissionType;
        }

        @Override
        public Object call() throws Exception {

            MvcResult mvcResult = this.mockMvc.perform(get("/dataDump/exportDataDump")
                    .param("fetchType",fetchType)
                    .param("requestingInstitutionCode","NYPL")
                    .param("outputFormat",outputFormat)
                    .param("emailToAddress","peri.subrahmanya@gmail.com")
                    .param("institutionCodes","CUL")
                    .param("transmissionType",transmissionType))
                    .andReturn();
            return mvcResult;
        }
    }

    public ExecutorService getExecutorService() {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(50);
        }
        return executorService;
    }

}
