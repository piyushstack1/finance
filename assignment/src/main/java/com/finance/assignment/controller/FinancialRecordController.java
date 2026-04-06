package com.finance.assignment.controller;

import com.finance.assignment.dto.PageResponseDTO;
import com.finance.assignment.dto.RecordRequestDTO;
import com.finance.assignment.dto.RecordResponseDTO;
import com.finance.assignment.entity.RecordType;
import com.finance.assignment.exception.CustomException;
import com.finance.assignment.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<RecordResponseDTO> createRecord(@Valid @RequestBody RecordRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecord(request));
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<RecordResponseDTO>> getRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        if (type != null) {
            try {
                RecordType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid type. Allowed values: INCOME, EXPENSE");
            }
        }

        if (page < 0) {
            throw new CustomException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new CustomException("Page size must be greater than 0");
        }
        if (size > 100) {
            throw new CustomException("Page size cannot exceed 100");
        }

        return ResponseEntity.ok(recordService.getRecords(type, category, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordResponseDTO> updateRecord(@PathVariable Long id,
                                          @Valid @RequestBody RecordRequestDTO request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}