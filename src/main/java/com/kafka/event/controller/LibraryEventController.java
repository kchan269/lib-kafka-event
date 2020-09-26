package com.kafka.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.event.domain.LibraryEvent;
import com.kafka.event.domain.LibraryEventType;
import com.kafka.event.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEnvent(@RequestBody  @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        log.info("before sendLibraryEvent");
    //    libraryEventProducer.sendLibraryEvent(libraryEvent);
     //   SendResult<Integer, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
     //   log.info("SendResult is {} ", sendResult.toString());
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
      //  libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        libraryEventProducer.sendLibraryEvent_Approach2_R(libraryEvent);
        log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEnvent(@RequestBody  @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        log.info("before sendLibraryEvent {}" , libraryEvent);
        if (libraryEvent.getLibraryEventId()==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);

        libraryEventProducer.sendLibraryEvent_Approach2_R(libraryEvent);
        log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
