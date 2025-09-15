package com.zinemasterapp.zinemasterapp.projections;

import java.time.LocalDate;

public interface DailyQty {
    LocalDate getDay();
    Integer getQty();
}
