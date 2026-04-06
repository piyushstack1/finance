package com.finance.assignment.service;

import com.finance.assignment.dto.PageResponseDTO;
import com.finance.assignment.dto.RecordRequestDTO;
import com.finance.assignment.dto.RecordResponseDTO;
import com.finance.assignment.entity.FinancialRecord;
import com.finance.assignment.entity.RecordType;
import com.finance.assignment.entity.User;
import com.finance.assignment.exception.ResourceNotFoundException;
import com.finance.assignment.repository.FinancialRecordRepository;
import com.finance.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public RecordResponseDTO createRecord(RecordRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .createdBy(user)
                .build();

        return mapToResponse(recordRepository.save(record));
    }

    public PageResponseDTO<RecordResponseDTO> getRecords(String type, String category, int pageNumber, int pageSize) {

        List<FinancialRecord> records;

        if (type != null && category != null) {
            records = recordRepository.findByTypeAndCategory(RecordType.valueOf(type), category);
        } else if (type != null) {
            records = recordRepository.findByType(RecordType.valueOf(type));
        } else if (category != null) {
            records = recordRepository.findByCategory(category);
        } else {
            records = recordRepository.findAll();
        }

        long totalElements = records.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        // Validate page number
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageNumber >= totalPages && totalElements > 0) {
            pageNumber = totalPages - 1;
        }

        // Calculate start and end indices
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, (int) totalElements);

        // Get records for current page
        List<RecordResponseDTO> pageContent = records.stream()
                .skip(startIndex)
                .limit(pageSize)
                .map(this::mapToResponse)
                .toList();

        return PageResponseDTO.<RecordResponseDTO>builder()
                .content(pageContent)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(pageNumber < totalPages - 1)
                .hasPrevious(pageNumber > 0)
                .build();
    }

    public RecordResponseDTO updateRecord(Long id, RecordRequestDTO request) {

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());

        return mapToResponse(recordRepository.save(record));
    }

    public void deleteRecord(Long id) {

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));

        recordRepository.delete(record);
    }

    private RecordResponseDTO mapToResponse(FinancialRecord record) {
        return RecordResponseDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .description(record.getDescription())
                .createdBy(record.getCreatedBy().getName())
                .build();
    }
}