package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.repository.DiscountDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscountDealServiceTest {

    @Mock
    private DiscountDealRepository discountDealRepository;

    @InjectMocks
    private DiscountDealService discountDealService;

    private DiscountDeal discountDeal;
    private DiscountDealDTO discountDealDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        discountDeal = new DiscountDeal(1L, 1L, "Buy 1 Get 1 Free", true, Instant.parse("2024-01-15T18:35:24.00Z"));
        discountDealDTO = new DiscountDealDTO(1L, 1L, "Buy 1 Get 1 Free", true, 0);
    }

    @Test
    public void testSaveDiscountDeal() {
        when(discountDealRepository.save(any(DiscountDeal.class))).thenReturn(discountDeal);

        final DiscountDealDTO savedDiscountDealDTO = discountDealService.saveDiscountDeal(discountDealDTO);

        assertNotNull(savedDiscountDealDTO);
        assertEquals(discountDealDTO.getDealDescription(), savedDiscountDealDTO.getDealDescription());
        verify(discountDealRepository, times(1)).save(any(DiscountDeal.class));
    }

    @Test
    public void testGetDiscountDealById() {
        when(discountDealRepository.findById(anyLong())).thenReturn(Optional.of(discountDeal));

        final DiscountDealDTO foundDiscountDealDTO = discountDealService.getDiscountDealById(1L);

        assertNotNull(foundDiscountDealDTO);
        assertEquals(discountDeal.getDealDescription(), foundDiscountDealDTO.getDealDescription());
        verify(discountDealRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAllDiscountDeals() {
        when(discountDealRepository.findAll()).thenReturn(Arrays.asList(discountDeal));

        final List<DiscountDealDTO> discountDeals = discountDealService.getAllDiscountDeals();

        assertNotNull(discountDeals);
        assertFalse(discountDeals.isEmpty());
        assertEquals(1, discountDeals.size());
        verify(discountDealRepository, times(1)).findAll();
    }

    @Test
    public void testRemoveDiscountDeal() {
        doNothing().when(discountDealRepository).deleteById(anyLong());

        discountDealService.removeDiscountDeal(1L);

        verify(discountDealRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void getDiscountDealsForProductId() {
        Long productId = 1L;
        List<DiscountDeal> deals = Arrays.asList(new DiscountDeal(2L, productId, "BUY N GET M Free", true,null));
        when(discountDealRepository.findByProductId(productId)).thenReturn(deals);

        List<DiscountDealDTO> result = discountDealService.getDiscountDealsForProductId(productId);

        assertNotNull(result);
        assertEquals(deals.size(), result.size());
        verify(discountDealRepository).findByProductId(productId);
    }

    @Test
    void getActiveDiscountDealForProductId() {
        Long productId = 1L;
        Optional<DiscountDeal> activeDeal = Optional.of(new DiscountDeal(3L, productId, "BUY 1 GET 2 FREE", true, null));
        when(discountDealRepository.findByProductIdAndActive(productId, true)).thenReturn(activeDeal);

        DiscountDealDTO result = discountDealService.getActiveDiscountDealForProductId(productId);

        assertNotNull(result);
        verify(discountDealRepository).findByProductIdAndActive(productId, true);
    }
}
