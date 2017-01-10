package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.commons.io.FilenameUtils;
import org.recap.repository.XmlRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by angelind on 21/7/16.
 */

@Component
public class XmlRouteBuilder {

    Logger logger = LoggerFactory.getLogger(ReportsRouteBuilder.class);

    @Autowired
    public XmlRouteBuilder(CamelContext context, XmlRecordRepository xmlRecordRepository, XMLFileLoadReportProcessor xmlFileLoadReportProcessor, XMLFileLoadExceptionReportProcessor xmlFileLoadExceptionReportProcessor, XMLFileLoadValidator xmlFileLoadValidator,
                           @Value("${etl.split.xml.tag.name}") String xmlTagName,
                           @Value("${etl.load.directory}") String inputDirectoryPath,
                           @Value("${etl.pool.size}") Integer poolSize, @Value("${etl.max.pool.size}") Integer maxPoolSize) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    FileEndpoint fileEndpoint = endpoint("file:" + inputDirectoryPath + "?moveFailed=" + inputDirectoryPath + File.separator + "exception", FileEndpoint.class);
                    fileEndpoint.setFilter(new XmlFileFilter());

                    from(fileEndpoint)
                            .onCompletion()
                            .process(xmlFileLoadReportProcessor)
                            .end()
                            .onException(Exception.class)
                            .process(xmlFileLoadExceptionReportProcessor)
                            .end()
                            .process(xmlFileLoadValidator)
                            .split()
                            .tokenizeXML(xmlTagName)
                            .streaming()
                            .parallelProcessing().threads(poolSize, maxPoolSize)
                            .process(new XmlProcessor(xmlRecordRepository));
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    class XmlFileFilter implements GenericFileFilter {
        @Override
        public boolean accept(GenericFile file) {
            return FilenameUtils.getExtension(file.getAbsoluteFilePath()).equalsIgnoreCase("xml");
        }
    }
}
