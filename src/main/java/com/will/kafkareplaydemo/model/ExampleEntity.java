package com.will.kafkareplaydemo.model;

import com.will.kafkareplaydemo.enums.Days;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExampleEntity implements Comparable<ExampleEntity> {
    private Days dayOfWeek;
    private LocalDate producerTimestamp;

    public ExampleEntity(Days day, String s) {
        this.dayOfWeek = day;
        this.producerTimestamp = LocalDate.parse(s);
    }
    @Override
    public int compareTo(ExampleEntity e) {
        return getProducerTimestamp().compareTo(e.getProducerTimestamp());
    }
}

