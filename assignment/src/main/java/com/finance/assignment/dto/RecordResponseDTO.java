package com.finance.assignment.dto;

import com.finance.assignment.entity.RecordType;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordResponseDTO {

    private Long id;
    private Double amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String description;
    private String createdBy;
}