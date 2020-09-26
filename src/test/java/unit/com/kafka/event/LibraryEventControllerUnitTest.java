package  com.kafka.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.event.controller.LibraryEventController;
import com.kafka.event.domain.Book;
import com.kafka.event.domain.LibraryEvent;
import com.kafka.event.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void postLibraryEvent() throws Exception {
        Book book = Book.builder().bookId(545).bookName("WE ARE THE ONE").bookAuthor("Tommy").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

        String contentBody = objectMapper.writeValueAsString(libraryEvent);
        doNothing().when(libraryEventProducer).sendLibraryEvent_Approach2(isA(LibraryEvent.class));
        mockMvc.perform(post("/v1/libraryevent").content(contentBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        Book book = Book.builder().bookId(null).bookName(null).bookAuthor("").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

        String expectedErrorMessage = "book.bookAuthor-must not be blank,book.bookId-must not be null,book.bookName-must not be blank";
        String contentBody = objectMapper.writeValueAsString(libraryEvent);
        doNothing().when(libraryEventProducer).sendLibraryEvent_Approach2(isA(LibraryEvent.class));
        mockMvc.perform(post("/v1/libraryevent").content(contentBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

    }

    @Test
    void putLibraryEvent() throws Exception {
        Book book = Book.builder().bookId(545).bookName("WE ARE THE ONE").bookAuthor("Tommy").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(878).book(book).build();

        String contentBody = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducer.sendLibraryEvent_Approach2_R(isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(put("/v1/libraryevent").content(contentBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    void putLibraryEvent_withNull() throws Exception {
        Book book = Book.builder().bookId(545).bookName("WE ARE THE ONE").bookAuthor("Tommy").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

        String contentBody = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducer.sendLibraryEvent_Approach2_R(isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(put("/v1/libraryevent").content(contentBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}
