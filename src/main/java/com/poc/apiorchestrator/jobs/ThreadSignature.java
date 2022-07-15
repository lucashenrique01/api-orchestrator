package com.poc.apiorchestrator.jobs;

import com.poc.apiorchestrator.services.SignatureService;

public class ThreadSignature extends Thread{
    private SignatureService signatureService;

    public ThreadSignature(SignatureService signatureService){
        this.signatureService = signatureService;
    }

    @Override
    public void run() {
        signatureService.listening();
    }
}
