package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.model.etl.EtlLoadRequest;
import org.recap.report.ReportGenerator;
import org.recap.repository.*;
import org.recap.camel.EtlDataLoadProcessor;
import org.recap.camel.RecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rajeshbabuk on 22/6/16.
 */

@Controller
public class EtlDataLoadController {

    Logger logger = LoggerFactory.getLogger(EtlDataLoadController.class);

    @Autowired
    CamelContext camelContext;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    XmlRecordRepository xmlRecordRepository;

    @Value("${etl.load.batchSize}")
    private Integer batchSize;

    @Value("${etl.load.directory}")
    private String inputDirectoryPath;

    @Autowired
    RecordProcessor recordProcessor;

    @Autowired
    ProducerTemplate producer;

    @Autowired
    ReportGenerator reportGenerator;

    @RequestMapping("/")
    public String etlDataLoader(Model model) {
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        model.addAttribute("etlLoadRequest", etlLoadRequest);
        return "etlDataLoader";
    }

    @ResponseBody
    @RequestMapping(value = "/etlDataLoader/bulkIngest", method = RequestMethod.POST)
    public String bulkIngest(@Valid @ModelAttribute("etlLoadRequest") EtlLoadRequest etlLoadRequest,
                            BindingResult result,
                            Model model) {
        EtlDataLoadProcessor etlDataLoadProcessor = new EtlDataLoadProcessor();

        String fileName = etlLoadRequest.getFileName();
        etlDataLoadProcessor.setBatchSize(etlLoadRequest.getBatchSize());
        etlDataLoadProcessor.setFileName(fileName);
        etlDataLoadProcessor.setInstitutionName(etlLoadRequest.getOwningInstitutionName());
        etlDataLoadProcessor.setXmlRecordRepository(xmlRecordRepository);
        etlDataLoadProcessor.setBibliographicDetailsRepository(bibliographicDetailsRepository);
        etlDataLoadProcessor.setHoldingsDetailsRepository(holdingsDetailsRepository);
        etlDataLoadProcessor.setItemDetailsRepository(itemDetailsRepository);
        etlDataLoadProcessor.setProducer(producer);
        etlDataLoadProcessor.setRecordProcessor(recordProcessor);
        etlDataLoadProcessor.startLoadProcess();
        return etlDataLoader(model);
    }

    @ResponseBody
    @RequestMapping(value = "/etlDataLoader/status", method = RequestMethod.GET)
    public String report() {
        String status = "Process Started";
        if (camelContext.getStatus().isStarted()) {
            status = "Running";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Status  : " + status).append("\n");
        //TODO: This call takes a long time to fetch the count.
        //stringBuilder.append("Number of Bibs loaded : " + bibliographicDetailsRepository.count()).append("\n");
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/etlDataLoader/uploadFiles", method = RequestMethod.POST)
    public String uploadFiles(@Valid @ModelAttribute("etlLoadRequest") EtlLoadRequest etlLoadRequest,
                             BindingResult result,
                             Model model) throws IOException {

        MultipartFile multipartFile = etlLoadRequest.getFile();
        if (null == multipartFile || StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            return etlDataLoader(model);
        }
        File uploadFile = new File(multipartFile.getOriginalFilename());
        FileUtils.writeByteArrayToFile(uploadFile, etlLoadRequest.getFile().getBytes());
        FileUtils.copyFile(uploadFile, new File(inputDirectoryPath + File.separator + multipartFile.getOriginalFilename()));
        return etlDataLoader(model);
    }

    @ResponseBody
    @RequestMapping(value = "/etlDataLoader/reports", method = RequestMethod.POST)
    public String generateReport(@Valid @ModelAttribute("etlLoadRequest") EtlLoadRequest etlLoadRequest,
                             BindingResult result,
                             Model model) {
        Calendar cal = Calendar.getInstance();
        Date dateFrom = etlLoadRequest.getDateFrom();
        if(dateFrom != null) {
            cal.setTime(dateFrom);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date from = cal.getTime();
        Date dateTo = etlLoadRequest.getDateTo();
        if(dateTo != null) {
            cal.setTime(dateTo);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date to = cal.getTime();
        String generatedReportFileName = reportGenerator.generateReport(etlLoadRequest.getReportFileName(), etlLoadRequest.getOperationType(),etlLoadRequest.getReportType(), etlLoadRequest.getReportInstitutionName(),
                from, to, etlLoadRequest.getTransmissionType());
        if(StringUtils.isBlank(generatedReportFileName)){
            logger.error("Report wasn't generated! Please contact help desk!");
        } else {
            logger.info("Report successfully generated!" + " : " + generatedReportFileName);
        }
        return etlDataLoader(model);
    }
}
