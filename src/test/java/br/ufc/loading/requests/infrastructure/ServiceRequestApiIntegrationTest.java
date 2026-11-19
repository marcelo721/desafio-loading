package br.ufc.loading.requests.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServiceRequestApiIntegrationTest {

    private static final String PASSWORD = "senha-segura-123";
    private static final String ADMIN_EMAIL = "admin@test.dev";
    private static final String ADMIN_PASSWORD = "Admin@12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerShouldCreateUserAndRejectDuplicatedEmail() throws Exception {
        String email = uniqueEmail();

        register("Maria", email).andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        register("Maria", email).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerShouldRejectInvalidData() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "", "email", "invalid", "password", "123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'email')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password')]").exists());
    }

    @Test
    void loginShouldReturnTokenAndRejectWrongPassword() throws Exception {
        String email = uniqueEmail();
        register("Maria", email).andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpointsShouldRequireAValidToken() throws Exception {
        mockMvc.perform(get("/api/requests")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/requests").header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createShouldStartOpenAndOwnedByTheAuthenticatedUser() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/requests")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "Printer broken", "description", "Jammed paper",
                                "category", "IT", "priority", "HIGH", "status", "COMPLETED"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.title").value("Printer broken"));
    }

    @Test
    void createShouldRejectEmptyRequiredFields() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/requests")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "   ", "description", "", "category", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'title')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'description')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'category')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'priority')]").exists());
    }

    @Test
    void createShouldRejectUnknownPriority() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post("/api/requests")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "Title", "description", "Desc",
                                "category", "IT", "priority", "URGENT"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void statusShouldFollowTheMandatoryFlowAndRecordHistory() throws Exception {
        String token = registerAndLogin();
        String id = createRequest(token, "Flow request", "MEDIUM");

        changeStatus(token, id, "IN_PROGRESS").andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        changeStatus(token, id, "COMPLETED").andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/api/requests/{id}/history", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].toStatus").value("OPEN"))
                .andExpect(jsonPath("$[0].fromStatus").doesNotExist())
                .andExpect(jsonPath("$[1].fromStatus").value("OPEN"))
                .andExpect(jsonPath("$[1].toStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[2].fromStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[2].toStatus").value("COMPLETED"));
    }

    @Test
    void skippingAStepShouldBeRejectedWithAClearMessage() throws Exception {
        String token = registerAndLogin();
        String id = createRequest(token, "Skip request", "LOW");

        changeStatus(token, id, "COMPLETED")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value(containsString("Aberta")))
                .andExpect(jsonPath("$.message").value(containsString("Em andamento")));

        mockMvc.perform(get("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void goingBackwardsShouldBeRejected() throws Exception {
        String token = registerAndLogin();
        String id = createRequest(token, "Backwards request", "LOW");
        changeStatus(token, id, "IN_PROGRESS").andExpect(status().isOk());

        changeStatus(token, id, "OPEN").andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listShouldFilterByStatusAndPriority() throws Exception {
        String token = registerAndLogin();
        String lowId = createRequest(token, "Low item", "LOW");
        createRequest(token, "Medium item", "MEDIUM");
        createRequest(token, "High item", "HIGH");
        changeStatus(token, lowId, "IN_PROGRESS").andExpect(status().isOk());

        mockMvc.perform(get("/api/requests").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));

        mockMvc.perform(get("/api/requests").param("status", "IN_PROGRESS")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Low item"));

        mockMvc.perform(get("/api/requests").param("priority", "HIGH")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("High item"));

        mockMvc.perform(get("/api/requests").param("status", "OPEN").param("priority", "LOW")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.totalElements").value(0));

        mockMvc.perform(get("/api/requests").param("status", "UNKNOWN")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listShouldSupportTextSearchSortingAndPagination() throws Exception {
        String token = registerAndLogin();
        createRequest(token, "Alpha printer", "LOW");
        createRequest(token, "Beta chair", "LOW");
        createRequest(token, "Gamma printer", "LOW");

        mockMvc.perform(get("/api/requests").param("search", "PRINTER")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get("/api/requests").param("sortBy", "title").param("direction", "ASC")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.content[0].title").value("Alpha printer"))
                .andExpect(jsonPath("$.content[2].title").value("Gamma printer"));

        mockMvc.perform(get("/api/requests").param("sortBy", "title").param("direction", "DESC")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.content[0].title").value("Gamma printer"));

        mockMvc.perform(get("/api/requests").param("size", "2").param("page", "1")
                        .param("sortBy", "title").param("direction", "ASC")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.page").value(1));

        mockMvc.perform(get("/api/requests").param("sortBy", "password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldChangeEditableFieldsAndKeepStatus() throws Exception {
        String token = registerAndLogin();
        String id = createRequest(token, "Original title", "LOW");
        changeStatus(token, id, "IN_PROGRESS").andExpect(status().isOk());

        mockMvc.perform(put("/api/requests/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "Edited title", "description", "Edited description",
                                "category", "Facilities", "priority", "HIGH"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Edited title"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(put("/api/requests/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "", "description", "x", "category", "x", "priority", "LOW"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShouldRemoveTheRequest() throws Exception {
        String token = registerAndLogin();
        String id = createRequest(token, "To be deleted", "LOW");

        mockMvc.perform(delete("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownOrMalformedIdsShouldReturnClearErrors() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get("/api/requests/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(get("/api/requests/{id}", "not-a-uuid")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void usersShouldNotAccessRequestsOwnedByOthers() throws Exception {
        String ownerToken = registerAndLogin();
        String strangerToken = registerAndLogin();
        String id = createRequest(ownerToken, "Private request", "LOW");

        mockMvc.perform(get("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(strangerToken)))
                .andExpect(status().isForbidden());
        changeStatus(strangerToken, id, "IN_PROGRESS").andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(strangerToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/requests").header(HttpHeaders.AUTHORIZATION, bearer(strangerToken)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void administratorsShouldAccessEveryRequest() throws Exception {
        String ownerToken = registerAndLogin();
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);
        String id = createRequest(ownerToken, "Visible to admin", "LOW");

        mockMvc.perform(get("/api/requests/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk());
        changeStatus(adminToken, id, "IN_PROGRESS").andExpect(status().isOk());
        mockMvc.perform(get("/api/requests").header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void dashboardShouldCountTheUsersRequestsByStatus() throws Exception {
        String token = registerAndLogin();
        String first = createRequest(token, "One", "LOW");
        String second = createRequest(token, "Two", "LOW");
        createRequest(token, "Three", "LOW");
        changeStatus(token, first, "IN_PROGRESS").andExpect(status().isOk());
        changeStatus(token, first, "COMPLETED").andExpect(status().isOk());
        changeStatus(token, second, "IN_PROGRESS").andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/summary").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.open").value(1))
                .andExpect(jsonPath("$.inProgress").value(1))
                .andExpect(jsonPath("$.completed").value(1));
    }

    @Test
    void auditShouldBeRestrictedToAdministratorsAndRegisterOperations() throws Exception {
        String userToken = registerAndLogin();
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);
        String id = createRequest(userToken, "Audited request", "LOW");
        changeStatus(userToken, id, "IN_PROGRESS").andExpect(status().isOk());

        mockMvc.perform(get("/api/audit").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit").param("size", "100")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.entityId == '" + id + "' && @.operation == 'CREATE')]").exists())
                .andExpect(jsonPath("$.content[?(@.entityId == '" + id + "' && @.operation == 'STATUS_CHANGE')]")
                        .exists());
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.dev";
    }

    private ResultActions register(String name, String email) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", name, "email", email, "password", PASSWORD))));
    }

    private String registerAndLogin() throws Exception {
        String email = uniqueEmail();
        register("Test User", email).andExpect(status().isCreated());
        return login(email, PASSWORD);
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        return readJson(result).get("token").asText();
    }

    private String createRequest(String token, String title, String priority) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/requests")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", title, "description", "Description of " + title,
                                "category", "IT", "priority", priority))))
                .andExpect(status().isCreated())
                .andReturn();
        return readJson(result).get("id").asText();
    }

    private ResultActions changeStatus(String token, String id, String status) throws Exception {
        return mockMvc.perform(patch("/api/requests/{id}/status", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("status", status))));
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
