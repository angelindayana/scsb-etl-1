package org.recap.camel;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 8/10/16.
 */

@Component
public class ReportProcessor implements Processor {

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        ReportEntity reportEntity = (ReportEntity) exchange.getIn().getBody();
        reportDetailRepository.save(reportEntity);
    }
}
