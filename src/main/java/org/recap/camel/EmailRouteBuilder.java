package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.recap.ReCAPConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by chenchulakshmig on 13/9/16.
 */
@Component
public class EmailRouteBuilder {

    private String emailBody;
    private String emailBodyForNoData;
    private String emailPassword;

    @Autowired
    public EmailRouteBuilder(CamelContext context, @Value("${data.dump.email.username}") String username, @Value("${data.dump.email.password.file}") String passwordDirectory,
                             @Value("${data.dump.email.from}") String from, @Value("${data.dump.email.subject}") String subject,@Value("${data.dump.email.nodata.subject}") String noDataSubject,
                             @Value("${smtpServer}") String smtpServer) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    loadEmailBodyTemplate();
                    loadEmailBodyTemplateForNoData();
                    loadEmailPassword();

                    from(ReCAPConstants.EMAIL_Q)
                            .routeId(ReCAPConstants.EMAIL_ROUTE_ID)
                            .setHeader("emailPayLoad").body(EmailPayLoad.class)
                            .onCompletion().log("Email has been sent successfully.")
                            .end()
                                .choice()
                                    .when(header(ReCAPConstants.DATADUMP_EMAILBODY_FOR).isEqualTo(ReCAPConstants.DATADUMP_DATA_AVAILABLE))
                                        .setHeader("subject", simple(subject))
                                        .setBody(simple(emailBody))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple("${header.emailPayLoad.to}"))
                                        .log("email body for data available")
                                        .to("smtps://" + smtpServer + "?username=" + username + "&password=" + emailPassword)
                                    .when(header("emailBodyFor").isEqualTo(ReCAPConstants.DATADUMP_NO_DATA_AVAILABLE))
                                        .setHeader("subject", simple(noDataSubject))
                                        .setBody(simple(emailBodyForNoData))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple("${header.emailPayLoad.to}"))
                                        .log("email body for no data available")
                                        .to("smtps://" + smtpServer + "?username=" + username + "&password=" + emailPassword)
                    ;
                }

                private void loadEmailBodyTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("email_body.vm");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                out.append("\n");
                            } else {
                                out.append(line);
                                out.append("\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    emailBody = out.toString();
                }

                private void loadEmailBodyTemplateForNoData() {
                    InputStream inputStream = getClass().getResourceAsStream("no_data_email_body.vm");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                out.append("\n");
                            } else {
                                out.append(line);
                                out.append("\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    emailBodyForNoData = out.toString();
                }

                private void loadEmailPassword() {
                    File file = new File(passwordDirectory);
                    if (file.exists()) {
                        try {
                            emailPassword = FileUtils.readFileToString(file, "UTF-8").trim();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
