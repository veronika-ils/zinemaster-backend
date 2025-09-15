package com.zinemasterapp.zinemasterapp.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockForecastService {

    private static final double RECENT_BOOST = 3.0;//povekje vredat poslednite tri dena
    private static final double EPS = 0.1;//da ne delam so 0
    private static final double MIN_BASE_RATE = 0.3;//najmalce sto moze(za na pr ako mn malce dena e ili nema nikoj poracano)

    public record Daily(LocalDate day, int qty) {}
    public record Result(
            String productId,
            double forecastPerDay,
            double daysToStockout,
            LocalDate predictedOutDate,
            boolean redWarning
    ) {}
    //record se za prefrluvanje data, ne treba constructor,klasa poednostavna e

    public Result forecast(String productId, int onHandQty, List<Daily> history) {

        int n = history.size();//kolku dena go ima produktot
        double xw = 0, wsum = 0;

        for (int i = 0; i < n; i++) {
            int q = history.get(i).qty();//zemame kolku se
            double w = (n - i <= 3) ? RECENT_BOOST : 1.0;//dali se vo poslednite tri dena
            xw += q * w;
            wsum += w;
        }

        double rate = (wsum > 0 ? xw / wsum : 0.0);

        if (n < 3) {
            double avg = history.stream().mapToInt(Daily::qty).average().orElse(0.0);
            rate = Math.max(rate, Math.max(avg, MIN_BASE_RATE));
        } else if (rate < EPS) {
            rate = Math.max(rate, MIN_BASE_RATE);
        }

        double days = (onHandQty <= 0) ? 0.0 : onHandQty / Math.max(rate, EPS);
        LocalDate outDate = LocalDate.now().plusDays((long)Math.ceil(days));
        boolean red = days < 5.0;

        return new Result(
                productId,
                round2(rate),
                round2(days),
                outDate,
                red
        );
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
