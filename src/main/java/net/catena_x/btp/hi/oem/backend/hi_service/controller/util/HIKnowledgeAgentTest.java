package net.catena_x.btp.hi.oem.backend.hi_service.controller.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catena_x.btp.hi.oem.common.model.dao.knowledgeagent.*;
import net.catena_x.btp.libraries.util.apihelper.ApiHelper;
import net.catena_x.btp.libraries.util.apihelper.ResponseChecker;
import net.catena_x.btp.libraries.util.apihelper.model.DefaultApiResult;
import net.catena_x.btp.libraries.util.exceptions.BtpException;
import net.catena_x.btp.libraries.util.json.ObjectMapperFactoryBtp;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class HIKnowledgeAgentTest {
    @Autowired private ApiHelper apiHelper;
    @Autowired private RestTemplate restTemplate;
    @Autowired @Qualifier(ObjectMapperFactoryBtp.EXTENDED_OBJECT_MAPPER) private ObjectMapper objectMapper;

    @Value("${knowledgeagent.api.username:foo}") private String knowledgeApiUsername;
    @Value("${knowledgeagent.api.password:bar}") private String knowledgeApiPassword;
    @Value("${knowledgeagent.api.address:https://knowledge.dev.demo.catena-x.net/oem-edc-data/BPNL00000003COJN}")
    private String knowledgeApiAddress;

    private final Logger logger = LoggerFactory.getLogger(HIKnowledgeAgentTest.class);

    public ResponseEntity<DefaultApiResult> run() {
        return callService(generateTestInputs());
    }

    private ResponseEntity<DefaultApiResult> callService(@NotNull final HIKAInputsDAO inputs) {
        final HttpUrl requestUrl = HttpUrl.parse(knowledgeApiAddress)
                .newBuilder().addPathSegment("api").addPathSegment("agent")
                .addEncodedQueryParameter("asset", "urn:cx:Skill:consumer:Health").build();

        final HttpHeaders headers = generateDefaultHeaders();
        addAuthorizationHeaders(headers);

        final HttpEntity<HIKAInputsDAO> request = new HttpEntity<>(inputs, headers);

        final ResponseEntity<HIKAOutputsDAO> response =
                restTemplate.postForEntity(requestUrl.toString(), request, HIKAOutputsDAO.class);

        checkAndShowResponse(response);
        return apiHelper.ok("Received data from Knowledge: " + resultToString(response.getBody()));
    }

    private void checkAndShowResponse(final ResponseEntity<HIKAOutputsDAO> response) {
        try {
            ResponseChecker.checkResponse(response);
            showResponse(response.getBody());
        } catch (final BtpException exception) {
            logger.error(exception.getMessage());
        }
    }

    private void showResponse(@Nullable final HIKAOutputsDAO response) {
        System.out.println("===========================================================================");
        if(response == null) {
            System.out.println("The knowledge agent response was null!");
        } else {
            System.out.println("The knowledge agent responded:");
            System.out.println("");
            System.out.println(resultToString(response));
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

    private HIKAInputsDAO generateTestInputs() {
        final HIKAInputsDAO inputs = new HIKAInputsDAO();

        inputs.setHead(new HIKAInputsHeaderDAO());
        inputs.getHead().setVars(new ArrayList<String>());
        inputs.getHead().getVars().add("van");
        inputs.getHead().getVars().add("aggregate");
        inputs.getHead().getVars().add("healthType");
        inputs.getHead().getVars().add("adaptionValues");

        inputs.setResults(new HIKAResultsDAO<HIKAInputBindingDAO>());
        inputs.getResults().setBindings(new ArrayList<HIKAInputBindingDAO>());

        addBinding(inputs, "FNKQHZHFTHMCRX", "Differential Gear",
                "GearSet", "[0.2, 0.3, 0.4]");
        addBinding(inputs, "LKTYZWBNDOMPGQ", "Differential Gear",
                "GearSet", "[0.4, 0.3, 0.2]");

        return inputs;
    }

    private void addBinding(@NotNull final HIKAInputsDAO inputs, @NotNull final String van,
                            @NotNull final String aggregate, @NotNull final String healthType,
                            @NotNull final String adaptionValues) {
        inputs.getResults().getBindings().add(new HIKAInputBindingDAO(
                new HIKAVariableDAO("literal", van, null),
                new HIKAVariableDAO("literal", aggregate, null),
                new HIKAVariableDAO("literal", healthType, null),
                new HIKAVariableDAO("literal", adaptionValues, null)));
    }

    private String resultToString(@NotNull HIKAOutputsDAO result) {
        try {
            return objectMapper.writerFor(HIKAOutputsDAO.class).writeValueAsString(result);
        } catch(final JsonProcessingException exception) {
            return "ERROR while converting result: " + exception.getMessage();
        }
    }
}
