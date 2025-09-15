package com.zinemasterapp.zinemasterapp.service;


import com.zinemasterapp.zinemasterapp.projections.DailyQty;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductForecastAppService {

    private final ProductRepository productRepo;
    private final ProductRequestItemRepository priRepo;
    private final StockForecastService forecast;

    public StockForecastService.Result forecastForProduct(String productId) {
        var p = productRepo.findById(productId).orElseThrow();


        List<DailyQty> grouped = priRepo.findDailyByProduct(productId);


        LocalDate start = p.getAddedAt().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = LocalDate.now();

        Map<LocalDate, Integer> map = new HashMap<>();
        for (var g : grouped) map.put(g.getDay(), g.getQty());

        var series = new ArrayList<StockForecastService.Daily>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            series.add(new StockForecastService.Daily(d, map.getOrDefault(d, 0)));//denovite sto nema gi polni so 0
        }

        int onHand = Optional.ofNullable(p.getQuantity()).orElse(0);//kolku ima ostanato
        return forecast.forecast(productId, onHand, series);
    }
}
