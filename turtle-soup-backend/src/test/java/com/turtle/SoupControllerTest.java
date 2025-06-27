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
        soupDTO.setTitle("深夜的敲门声");
        soupDTO.setDescription("某人深夜听到敲门声，开门却没人。请推理发生了什么？");
        soupDTO.setAnswer("有人恶作剧敲门后躲起来了。");
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
