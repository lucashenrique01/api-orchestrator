package com.poc.apiorchestrator.rest.email;

import com.poc.apiorchestrator.dto.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "email", url = EnvUrl.EMAILDOCKER)
public interface ClientApiEmailMocked {

    @PostMapping("/email/document/canceled")
    void postEmailandIdDcoument(@RequestBody Data data);

}
