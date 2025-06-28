package com.turtle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turtle.pojo.dto.SoupDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SoupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testInsertSoup() throws Exception {
        SoupDTO soupDTO = new SoupDTO();
        soupDTO.setTitle("测试1");
        soupDTO.setDescription("测试1");
        soupDTO.setAnswer("测试1");
        soupDTO.setDifficulty(2);
        soupDTO.setTagIds(Arrays.asList(1L, 2L));
        soupDTO.setIsPublic(true);

        mockMvc.perform(post("/soup")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwidXNlcm5hbWUiOiJ0dXJ0bGUxIiwiaWF0IjoxNzUxMDMxNTM0LCJleHAiOjE3NTExMTc5MzR9.6ejc3T3OabK5Z7RzghL1LG5gGlVDxMwZJoKpdGlBoxE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soupDTO)))
                .andExpect(status().isOk());
    }
}
