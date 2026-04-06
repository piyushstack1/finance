package com.finance.assignment.dto;

import com.finance.assignment.entity.Role;
import com.finance.assignment.entity.Status;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Status status;
}