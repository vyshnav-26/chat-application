package com.chatapp.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void protectedEndpointReturnsStandardUnauthorizedError() throws Exception {
		mockMvc.perform(get("/users/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("Authentication is required"));
	}

	@Test
	void protectedEndpointRejectsInvalidBearerTokenWithStandardError() throws Exception {
		mockMvc.perform(get("/users/me").header("Authorization", "Bearer invalid-token"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	@WithMockUser
	void unknownPathPreservesNotFoundStatusAndErrorFormat() throws Exception {
		mockMvc.perform(get("/missing-route"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void malformedJsonPreservesBadRequestStatusAndErrorFormat() throws Exception {
		mockMvc.perform(post("/auth/login").contentType("application/json").content("{"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void registrationRejectsPasswordOverBcryptUtf8ByteLimit() throws Exception {
		String password = "ä".repeat(40);
		String payload = "{\"username\":\"alice\",\"password\":\"" + password + "\"}";
		mockMvc.perform(post("/auth/register").contentType("application/json").content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Password must not exceed 72 bytes in UTF-8"));
	}

}

