package com.yww.api;

import com.yww.api.modules.api.service.ApiService;
import com.yww.api.modules.api.vo.ApiForm;
import com.yww.api.modules.api.vo.ApiResp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InterfaceApplicationTests {

    @Autowired
    ApiService apiService;
    @Test
    void contextLoads() {
        ApiForm apiForm = ApiForm.builder()
                .apiNo("1")
                .version("v1")
                .method("testv1")
                .build();
        ApiResp apiResp = apiService.action(apiForm, "v1");
        System.out.println(apiResp);
    }

}
