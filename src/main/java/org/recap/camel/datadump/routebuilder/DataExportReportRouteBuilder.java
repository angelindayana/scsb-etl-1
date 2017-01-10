package org.recap.camel.datadump.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.ReCAPConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 11/12/16.
 */
@Component
public class DataExportReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataExportReportRouteBuilder.class);

    @Autowired
    public DataExportReportRouteBuilder(CamelContext camelContext) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ReCAPConstants.DATADUMP_SUCCESS_REPORT_Q)
                            .routeId(ReCAPConstants.DATADUMP_SUCCESS_REPORT_ROUTE_ID)
                            .bean(DataExportReportActiveMQConsumer.class, "saveSuccessReportEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ReCAPConstants.DATADUMP_FAILURE_REPORT_Q)
                            .routeId(ReCAPConstants.DATADUMP_FAILURE_REPORT_ROUTE_ID)
                            .bean(DataExportReportActiveMQConsumer.class, "saveFailureReportEntity");
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
