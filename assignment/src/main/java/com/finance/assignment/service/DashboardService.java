package com.finance.assignment.service;

import com.finance.assignment.dto.DashboardResponseDTO;
import com.finance.assignment.dto.RecordResponseDTO;
import com.finance.assignment.entity.FinancialRecord;
import com.finance.assignment.entity.RecordType;
import com.finance.assignment.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardResponseDTO getSummary() {

        List<FinancialRecord> records = recordRepository.findAll();

        double totalIncome = records.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .mapToDouble(FinancialRecord::getAmount)
                .sum();

        double totalExpense = records.stream()
                .filter(r -> r.getType() == RecordType.EXPENSE)
                .mapToDouble(FinancialRecord::getAmount)
                .sum();

        double netBalance = totalIncome - totalExpense;

        Map<String, Double> categoryWise = records.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.summingDouble(FinancialRecord::getAmount)
                ));

        List<RecordResponseDTO> recent = records.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5)
                .map(r -> RecordResponseDTO.builder()
                        .id(r.getId())
                        .amount(r.getAmount())
                        .type(r.getType())
                        .category(r.getCategory())
                        .date(r.getDate())
                        .description(r.getDescription())
                        .createdBy(r.getCreatedBy().getName())
                        .build())
                .toList();

        return DashboardResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .categoryWiseTotals(categoryWise)
                .recentTransactions(recent)
                .build();
    }
}
