package com.poc.apiorchestrator.jobs;

import com.poc.apiorchestrator.services.DocumentService;
public class ThreadDocument extends Thread{
    private DocumentService documentService;

    public ThreadDocument(DocumentService documentService){
        this.documentService = documentService;
    }

    @Override
    public void run() {
        documentService.listening();
    }
}
