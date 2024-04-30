package org.adrij.web;

import org.adrij.common.Exceptions.KafkaCustomException;
import org.adrij.web.Services.Implementations.KafkaProducerService;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaService;

    @Test
    public void testSendMessage_Success() {
        Long requestId = 123L;
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);
        kafkaService.produceKafkaMessage(requestId);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
    }

    @Test
    public void testSendMessage_TimeoutException() {
        Long requestId = 123L;
        TimeoutException timeoutException = new TimeoutException("Test TimeoutException");
        doThrow(timeoutException).when(kafkaTemplate).send(anyString(), anyString());

        KafkaCustomException exception =
                assertThrows(KafkaCustomException.class, () -> kafkaService.produceKafkaMessage(requestId));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
        verifyNoMoreInteractions(kafkaTemplate);
        assertExceptionMessage(
                "KAFKA_TIMEOUT_ERROR",
                "Timeout while sending message to Kafka. Exact: Test TimeoutException",
                exception);
    }

    @Test
    public void testSendMessage_RecordTooLargeException() {
        Long requestId = 123L;
        RecordTooLargeException recordTooLargeException = new RecordTooLargeException("Test RecordTooLargeException");
        doThrow(recordTooLargeException).when(kafkaTemplate).send(anyString(), anyString());

        KafkaCustomException exception =
                assertThrows(KafkaCustomException.class, () -> kafkaService.produceKafkaMessage(requestId));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
        verifyNoMoreInteractions(kafkaTemplate);
        assertExceptionMessage(
                "KAFKA_LARGE_RECORD_ERROR",
                "Message size exceeds Kafka's maximum allowable size. Exact: Test RecordTooLargeException",
                exception);
    }


    @Test
    public void testSendMessage_UnknownException() {
        Long requestId = 123L;
        RuntimeException runtimeException = new RuntimeException("Test RuntimeException");
        doThrow(runtimeException).when(kafkaTemplate).send(anyString(), anyString());

        KafkaCustomException exception =
                assertThrows(KafkaCustomException.class, () -> kafkaService.produceKafkaMessage(requestId));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
        verifyNoMoreInteractions(kafkaTemplate);
        assertExceptionMessage(
                "KAFKA_UNKNOWN_ERROR",
                "Unknown error happened in Kafka producer. Exact: Test RuntimeException",
                exception);
    }

    private void assertExceptionMessage(String expectedErrorCode, String expectedMessage, KafkaCustomException exception) {
        assertEquals(expectedErrorCode, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());
    }
}

