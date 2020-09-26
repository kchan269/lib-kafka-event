package com.kafka.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.event.domain.Book;
import com.kafka.event.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer eventProducer;

    @Test
    void sendLibraryEvent_Approach2_R_failure() throws JsonProcessingException {

        Book book = Book.builder().bookId(545).bookName("WE ARE THE ONE").bookAuthor("NORTH").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

        SettableListenableFuture future = new SettableListenableFuture();
        
        future.setException(new RuntimeException("Exception calling Kafka"));
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        assertThrows(Exception.class, () ->eventProducer.sendLibraryEvent_Approach2_R(libraryEvent).get());


    }

    @Test
    void sendLibraryEvent_Approach2_R_success() throws JsonProcessingException, ExecutionException, InterruptedException {

        Book book = Book.builder().bookId(545).bookName("WE ARE THE ONE").bookAuthor("NORTH").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();




        String record = objectMapper.writeValueAsString(libraryEvent);

        SettableListenableFuture future = new SettableListenableFuture();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("Library-events", libraryEvent.getLibraryEventId(), record );
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("Library-events", 1),
                1, 1, 1, System.currentTimeMillis(),1,1);
        SendResult<Integer, String> sendResult = new SendResult<Integer, String>(producerRecord, recordMetadata);

        future.set(sendResult);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = eventProducer.sendLibraryEvent_Approach2_R(libraryEvent);
        SendResult<Integer, String> sendResult1 = listenableFuture.get();
        assert sendResult1.getRecordMetadata().partition() ==1;
        assertEquals("Library-events",  sendResult1.getRecordMetadata().topic());

    }


}
