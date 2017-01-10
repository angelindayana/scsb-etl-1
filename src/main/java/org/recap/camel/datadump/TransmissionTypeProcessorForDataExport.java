package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by peris on 11/8/16.
 */
public class TransmissionTypeProcessorForDataExport implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
        String exportFormat = getValueFor(batchHeaders, "transmissionType");
        exchange.getOut().setHeader("transmissionType", exportFormat);
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
