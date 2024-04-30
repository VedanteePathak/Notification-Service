package org.adrij.web;

import org.adrij.common.Models.DTOs.Requests.BlacklistRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.adrij.common.Models.Entities.BlacklistEntity;
import org.adrij.common.Repositories.BlacklistRepository;
import org.adrij.common.Services.Implementations.RedisService;
import org.adrij.web.Services.Implementations.BlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlacklistServiceTest {
    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private BlacklistService blacklistService;

    @Test
    public void testAddToBlacklist() {
        BlacklistRequestDTO blacklistRequestDTO = new BlacklistRequestDTO();
        blacklistRequestDTO.setPhoneNumbers(new ArrayList<>(Set.of("1234567890")));

        when(blacklistRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        ResponseEntity<ResponseDTO> response = blacklistService.addPhoneNumbersToBlacklist(blacklistRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ResponseDTO.class, response.getBody());
    }

    @Test
    public void testRemoveFromBlacklist() {
        BlacklistRequestDTO blacklistRequestDTO = new BlacklistRequestDTO();
        blacklistRequestDTO.setPhoneNumbers(new ArrayList<>(Set.of("1234567890")));

        when(blacklistRepository.findByPhoneNumberIn(any())).thenReturn(new ArrayList<>());
        ResponseEntity<ResponseDTO> response = blacklistService.removePhoneNumbersFromBlacklist(blacklistRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ResponseDTO.class, response.getBody());
    }

    @Test
    public void testGetBlacklist_Redis() {
        ArrayList<BlacklistEntity> blacklistEntries = new ArrayList<>();
        blacklistEntries.add(new BlacklistEntity("1234567890"));
        when(blacklistRepository.findAll()).thenReturn(blacklistEntries);
        when(redisService.getAllPhoneNumbersInBlacklist()).thenReturn(
                new HashSet<>(blacklistEntries.stream()
                        .map(BlacklistEntity::getPhoneNumber)
                        .collect(Collectors.toCollection(ArrayList::new))));

        ResponseEntity<ResponseDTO> response = blacklistService.getAllPhoneNumbersInBlacklist();
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ResponseDTO.class, response.getBody());
    }

    @Test
    public void testAddToBlacklist_Redis() {
        BlacklistRequestDTO blacklistRequestDTO = new BlacklistRequestDTO();
        blacklistRequestDTO.setPhoneNumbers(new ArrayList<>(Set.of("1234567890")));

        when(blacklistRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        doNothing().when(redisService).addPhoneNumbersToBlacklist(any());
        ResponseEntity<ResponseDTO> response = blacklistService.addPhoneNumbersToBlacklist(blacklistRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ResponseDTO.class, response.getBody());
    }

    @Test
    public void testRemoveFromBlacklist_Redis() {
        BlacklistRequestDTO blacklistRequestDTO = new BlacklistRequestDTO();
        blacklistRequestDTO.setPhoneNumbers(new ArrayList<>(Set.of("1234567890")));

        when(blacklistRepository.findByPhoneNumberIn(any())).thenReturn(new ArrayList<>());
        doNothing().when(redisService).removePhoneNumbersFromBlacklist(any());
        ResponseEntity<ResponseDTO> response = blacklistService.removePhoneNumbersFromBlacklist(blacklistRequestDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ResponseDTO.class, response.getBody());
    }
}
