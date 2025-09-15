package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.service.ProductForecastAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.zinemasterapp.zinemasterapp.service.StockForecastService.Result;

@RestController
@RequiredArgsConstructor
public class ForecastController {

    private final ProductForecastAppService app;

    @GetMapping("/api/products/{id}/forecast")
    public Result forecast(@PathVariable String id) {
        return app.forecastForProduct(id);
    }
}
