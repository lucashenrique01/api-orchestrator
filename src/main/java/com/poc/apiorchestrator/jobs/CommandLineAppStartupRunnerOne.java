package com.poc.apiorchestrator.jobs;

import com.poc.apiorchestrator.dto.Data;
import com.poc.apiorchestrator.services.DocumentService;
import com.poc.apiorchestrator.services.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandLineAppStartupRunnerOne implements ApplicationRunner {


    @Autowired
    private DocumentService documentService;
    @Autowired
    private SignatureService signatureService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadDocument threadDocument = new ThreadDocument(documentService);
        ThreadSignature threadSignature = new ThreadSignature(signatureService);
        threadDocument.start();
        threadSignature.start();
    }
}
