package ru.otus.hw.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.domain.enums.SlotStatus;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "slots")
public class Slot {
    @Id
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private SlotStatus status;

    private Group bookedBy;
}
