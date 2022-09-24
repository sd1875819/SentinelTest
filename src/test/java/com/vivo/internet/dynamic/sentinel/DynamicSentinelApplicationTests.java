package com.vivo.internet.dynamic.sentinel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicSentinelApplicationTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();  //构造MockMvc
    }

    @BeforeClass
    public static void setEnvironmentInDev() {
        if (System.getProperty("app.name") != null)
            return;

        System.setProperty("app.name", "vivo-boot-demo");
        System.setProperty("app.env", "dev");
        System.setProperty("app.loc", "sz-sk");
        System.setProperty("config.host", "http://vivocfg-agent.test.vivo.xyz/vivocfg");
    }

    @Test
    public void checkDo() throws Exception {
        String responseString = mockMvc.perform(get("/check.do")
                .content("").contentType(MediaType.APPLICATION_JSON)) // json 参数和类型
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println(responseString);
    }
}

