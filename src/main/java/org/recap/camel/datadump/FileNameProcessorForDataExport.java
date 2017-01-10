package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.ReCAPConstants;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by peris on 11/4/16.
 */

@Component
public class FileNameProcessorForDataExport implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String fileName = getValueFor(batchHeaders, "fileName");
        String exportFormat = getValueFor(batchHeaders,"exportFormat");
        if (exportFormat.equals(ReCAPConstants.DATADUMP_DELETED_JSON_FORMAT)) {
            exchange.getOut().setHeader(Exchange.FILE_NAME, fileName+".json");
        } else {
            exchange.getOut().setHeader(Exchange.FILE_NAME, fileName+".xml");
        }
        Object body = exchange.getIn().getBody();
        exchange.getOut().setBody(body);

        Map<String, Object> headersForNewExchange = exchange.getIn().getHeaders();
        for (Iterator<String> iterator = headersForNewExchange.keySet().iterator(); iterator.hasNext(); ) {
            String header = iterator.next();
            exchange.getOut().setHeader(header, headersForNewExchange.get(header));
        }
    }

    private String getValueFor(String batchHeaderString, String key) {
        String valueFor = new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
        return valueFor;
    }
}
