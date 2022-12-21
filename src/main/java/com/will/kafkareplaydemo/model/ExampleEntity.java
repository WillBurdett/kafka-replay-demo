package com.will.kafkareplaydemo.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ExampleEntity {
    private List<String> daysOfWeek;
    private LocalDateTime dateTime;

    public ExampleEntity() {
    }

    public ExampleEntity(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
        this.dateTime = LocalDateTime.now();
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExampleEntity that = (ExampleEntity) o;
        return Objects.equals(daysOfWeek, that.daysOfWeek) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(daysOfWeek, dateTime);
    }

    @Override
    public String toString() {
        return "ExampleEntity{" +
                "daysOfWeek=" + daysOfWeek +
                ", dateTime=" + dateTime +
                '}';
    }
}
