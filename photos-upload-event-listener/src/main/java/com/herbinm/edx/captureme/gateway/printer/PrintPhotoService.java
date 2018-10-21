package com.herbinm.edx.captureme.gateway.printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

@Service
public class PrintPhotoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintPhotoService.class);

    @JmsListener(destination = "uploads-queue")
    public void createThumbnail(String requestJSON) throws JMSException {
        LOGGER.info("Received Message: {}", requestJSON);
    }
}
