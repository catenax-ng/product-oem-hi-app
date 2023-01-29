package net.catena_x.btp.hi.oem.backend.hi_service.controller.util;

import net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent.*;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.ResponseChecker;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import net.catena_x.btp.libraries.util.exceptions.BtpException;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.function.BiConsumer;

@Component
public class HIKnowledgeAgentTest {
    @Autowired private ApiHelper apiHelper;
    @Autowired private RestTemplate restTemplate;

    @Value("${knowledgeagent.api.username:foo}") private String knowledgeApiUsername;
    @Value("${knowledgeagent.api.password:bar}") private String knowledgeApiPassword;
    @Value("${knowledgeagent.api.address:https://knowledge.int.demo.catena-x.net/consumer-edc-data/BPNL00000003CQI9}")
    private String knowledgeApiAddress;

    private final Logger logger = LoggerFactory.getLogger(HIKnowledgeAgentTest.class);

    public ResponseEntity<DefaultApiResult> run() {
        return callService(generateTestInputs());
    }

    private ResponseEntity<DefaultApiResult> callService(@NotNull final HIKAInputs inputs) {
        final HttpUrl requestUrl = HttpUrl.parse(knowledgeApiAddress)
                .newBuilder().addPathSegment("api").addPathSegment("agent")
                .addQueryParameter("asset", "urn:cx:Skill:consumer:Health") .build();

        final HttpHeaders headers = generateDefaultHeaders();
        addAuthorizationHeaders(headers);

        final HttpEntity<HIKAInputs> request = new HttpEntity<>(inputs, headers);

        final ResponseEntity<String> response = restTemplate.postForEntity(
                requestUrl.toString(), request, String.class);

        checkAndShowResponse(response);
        return apiHelper.ok("Received data from Knowledge Agent: " + response.getBody());
    }

    private void checkAndShowResponse(final ResponseEntity<String> response) {
        try {
            ResponseChecker.checkResponse(response);
            showResponse(response.getBody());
        } catch (final BtpException exception) {
            logger.error(exception.getMessage());
        }
    }

    private void showResponse(@Nullable final String response) {
        System.out.println("===========================================================================");
        if(response == null) {
            System.out.println("The knowledge agent response was null!");
        } else {
            System.out.println("The knowledge agent responded:");
            System.out.println("");
            System.out.println("response");
            System.out.println("");
            System.out.println("===========================================================================");
        }
    }

    private void addAuthorizationHeaders(final HttpHeaders headers) {
        headers.add("Authorization", getAuthString());
    }

    private String getAuthString() {
        StringBuilder sb = new StringBuilder();

        String authStr = sb.append(knowledgeApiUsername).append(":").append(knowledgeApiPassword).toString();
        sb.setLength(0);
        sb.append("Basic ").append(Base64.getEncoder().encodeToString(authStr.getBytes()));
        return sb.toString();
    }

    protected HttpHeaders generateDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/sparql-results+json");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private HIKAInputs generateTestInputs() {
        final HIKAInputs inputs = new HIKAInputs();

        inputs.setHead(new HIKAInputsHeader());
        inputs.getHead().setVars(new ArrayList<String>());
        inputs.getHead().getVars().add("vin");
        inputs.getHead().getVars().add("aggregate");

        inputs.setResults(new HIKAResults());
        inputs.getResults().setBindings(new ArrayList<HIKABinding>());

        final BiConsumer<String, String> addBinding =
        (final String vin, final String aggregate) ->
                inputs.getResults().getBindings().add(new HIKABinding(new HIKAVariable("literal", vin),
                        new HIKAVariable("literal", aggregate)));

        addBinding.accept("WBAAL31029PZ00001", "engine control module");
        addBinding.accept("WBAAL31029PZ00002", "clutch");
        addBinding.accept("WBAAL31029PZ00003", "wiring harness");

        return inputs;
    }
}
