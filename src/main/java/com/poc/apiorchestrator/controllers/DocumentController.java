package com.poc.apiorchestrator.controllers;

import com.poc.apiorchestrator.dto.Event;
import com.poc.apiorchestrator.services.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/document/cancel")
public class DocumentController {

    DocumentService documentService;

    public DocumentController(DocumentService documentService){
        this.documentService = documentService;
    }

    @PostMapping("/{idDocument}")
    public ResponseEntity orchestrator(@PathVariable String idDocument) throws ExecutionException, InterruptedException {
        documentService.sendMessage(idDocument);
        return ResponseEntity.status(201).build();
    }
}
