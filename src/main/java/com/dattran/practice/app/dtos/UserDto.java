package com.dattran.practice.app.dtos;

import com.dattran.practice.app.annotations.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UserDto {
    @NotNull(message = "Password must not be null")
    @Length(min = 10, max = 50)
    String password;

    @NotNull(message = "Username must not be null")
    @Length(min = 10, max = 75)
    String username;

    @NotNull(message = "Address must not be null")
    String address;

    LocalDate dateOfBirth;

    @Email(message = "Invalid email")
    String email;

    @ValidPhoneNumber
    String phoneNumber;

    @NotNull(message = "Role id must not be null")
    List<Long> roleIds;
}
