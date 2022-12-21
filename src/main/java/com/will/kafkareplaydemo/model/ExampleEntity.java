package com.will.kafkareplaydemo.model;

import com.will.kafkareplaydemo.enums.Days;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExampleEntity {
    private List<Days> daysOfWeek;
}