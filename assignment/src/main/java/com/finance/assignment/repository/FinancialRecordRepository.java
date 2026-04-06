package com.finance.assignment.repository;

import com.finance.assignment.entity.FinancialRecord;
import com.finance.assignment.entity.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByType(RecordType type);

    List<FinancialRecord> findByCategory(String category);

    List<FinancialRecord> findByTypeAndCategory(RecordType type, String category);
}