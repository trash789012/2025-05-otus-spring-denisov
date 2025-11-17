package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.repositories.SlotRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий JPA для работы со слотами времени")
@DataJpaTest
@Testcontainers
public class JpaSlotRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private SlotRepository slotRepository;

    private Slot testSlot;
    private Slot testSlot2;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);

        User testUser = User.builder()
                .name("testuser")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .roles(userRoles)
                .build();
        testUser = em.persistAndFlush(testUser);

        testGroup = Group.builder()
                .name("testgroup")
                .description("Test group description")
                .members(List.of(testUser))
                .build();
        testGroup = em.persistAndFlush(testGroup);

        LocalDateTime now = LocalDateTime.now();
        testSlot = Slot.builder()
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .status(SlotStatus.FREE)
                .bookedBy(testGroup)
                .build();

        testSlot2 = Slot.builder()
                .startTime(now.plusHours(3))
                .endTime(now.plusHours(4))
                .status(SlotStatus.BOOKED)
                .bookedBy(testGroup)
                .build();

        testSlot = em.persistAndFlush(testSlot);
        testSlot2 = em.persistAndFlush(testSlot2);

        em.clear();
    }

    @Test
    @DisplayName("Должен найти слот по ID с загруженной группой")
    void shouldFindSlotByIdWithGroup() {
        // When
        Optional<Slot> foundSlot = slotRepository.findById(testSlot.getId());

        // Then
        assertThat(foundSlot).isPresent();
        Slot slot = foundSlot.get();
        assertThat(slot.getId()).isEqualTo(testSlot.getId());
        assertThat(slot.getStartTime()).isEqualTo(testSlot.getStartTime());
        assertThat(slot.getEndTime()).isEqualTo(testSlot.getEndTime());
        assertThat(slot.getStatus()).isEqualTo(SlotStatus.FREE);

        // Проверяем, что группа загружена через @EntityGraph
        assertThat(slot.getBookedBy()).isNotNull();
        assertThat(slot.getBookedBy().getName()).isEqualTo("testgroup");
        assertThat(slot.getBookedBy().getDescription()).isEqualTo("Test group description");
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующего слота")
    void shouldReturnEmptyWhenSlotNotFound() {
        // When
        Optional<Slot> foundSlot = slotRepository.findById(999L);

        // Then
        assertThat(foundSlot).isEmpty();
    }

    @Test
    @DisplayName("Должен найти все слоты в указанном временном диапазоне")
    void shouldFindAllByStartTimeBetween() {
        // Given
        LocalDateTime startRange = testSlot.getStartTime().minusMinutes(30);
        LocalDateTime endRange = testSlot.getStartTime().plusMinutes(30);

        // When
        List<Slot> slots = slotRepository.findAllByStartTimeBetween(startRange, endRange);

        // Then
        assertThat(slots).hasSize(1);
        assertThat(slots.get(0).getId()).isEqualTo(testSlot.getId());
        assertThat(slots.get(0).getBookedBy()).isNotNull(); // Группа должна быть загружена
        assertThat(slots.get(0).getBookedBy().getName()).isEqualTo("testgroup");
    }

    @Test
    @DisplayName("Должен вернуть пустой список при поиске слотов вне диапазона")
    void shouldReturnEmptyWhenNoSlotsInTimeRange() {
        // Given
        LocalDateTime startRange = testSlot.getStartTime().plusDays(1);
        LocalDateTime endRange = testSlot.getStartTime().plusDays(2);

        // When
        List<Slot> slots = slotRepository.findAllByStartTimeBetween(startRange, endRange);

        // Then
        assertThat(slots).isEmpty();
    }

    @Test
    @DisplayName("Должен найти пересекающиеся слоты")
    void shouldFindOverlappingSlots() {
        // Given
        LocalDateTime overlapStart = testSlot.getStartTime().plusMinutes(30); // 1:30
        LocalDateTime overlapEnd = testSlot.getEndTime().plusMinutes(30);     // 2:30

        // When
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                overlapStart, overlapEnd, null);

        // Then
        assertThat(overlappingSlots).hasSize(1);
        assertThat(overlappingSlots.get(0).getId()).isEqualTo(testSlot.getId());
    }

    @Test
    @DisplayName("Должен найти пересекающиеся слоты с полным перекрытием")
    void shouldFindOverlappingSlotsWithFullOverlap() {
        // Given
        LocalDateTime overlapStart = testSlot.getStartTime().minusMinutes(30); // 0:30
        LocalDateTime overlapEnd = testSlot.getEndTime().plusMinutes(30);      // 2:30

        // When
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                overlapStart, overlapEnd, null);

        // Then
        assertThat(overlappingSlots).hasSize(1);
        assertThat(overlappingSlots.get(0).getId()).isEqualTo(testSlot.getId());
    }

    @Test
    @DisplayName("Должен найти пересекающиеся слоты с частичным перекрытием в начале")
    void shouldFindOverlappingSlotsWithStartOverlap() {
        // Given
        LocalDateTime overlapStart = testSlot.getStartTime().minusMinutes(30); // 0:30
        LocalDateTime overlapEnd = testSlot.getStartTime().plusMinutes(30);    // 1:30

        // When
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                overlapStart, overlapEnd, null);

        // Then
        assertThat(overlappingSlots).hasSize(1);
        assertThat(overlappingSlots.get(0).getId()).isEqualTo(testSlot.getId());
    }

    @Test
    @DisplayName("Должен найти пересекающиеся слоты с частичным перекрытием в конце")
    void shouldFindOverlappingSlotsWithEndOverlap() {
        // Given
        LocalDateTime overlapStart = testSlot.getEndTime().minusMinutes(30); // 1:30
        LocalDateTime overlapEnd = testSlot.getEndTime().plusMinutes(30);    // 2:30

        // When
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                overlapStart, overlapEnd, null);

        // Then
        assertThat(overlappingSlots).hasSize(1);
        assertThat(overlappingSlots.get(0).getId()).isEqualTo(testSlot.getId());
    }

    @Test
    @DisplayName("Не должен находить пересекающиеся слоты при исключении ID")
    void shouldExcludeIdWhenFindingOverlappingSlots() {
        // Given
        LocalDateTime overlapStart = testSlot.getStartTime().plusMinutes(30);
        LocalDateTime overlapEnd = testSlot.getEndTime().plusMinutes(30);

        // When
        List<Slot> overlappingSlots = slotRepository.findOverlappingSlots(
                overlapStart, overlapEnd, testSlot.getId());

        // Then
        assertThat(overlappingSlots).isEmpty();
    }

    @Test
    @DisplayName("Должен сохранить новый слот")
    void shouldSaveNewSlot() {
        // Given
        LocalDateTime newStart = LocalDateTime.now().plusHours(5);
        LocalDateTime newEnd = newStart.plusHours(1);
        Slot newSlot = Slot.builder()
                .startTime(newStart)
                .endTime(newEnd)
                .status(SlotStatus.FREE)
                .bookedBy(testGroup)
                .build();

        // When
        Slot savedSlot = slotRepository.save(newSlot);
        em.flush();
        em.clear();

        // Then
        Slot foundSlot = slotRepository.findById(savedSlot.getId()).orElseThrow();
        assertThat(foundSlot.getStartTime()).isEqualTo(newStart);
        assertThat(foundSlot.getEndTime()).isEqualTo(newEnd);
        assertThat(foundSlot.getStatus()).isEqualTo(SlotStatus.FREE);
        assertThat(foundSlot.getBookedBy().getId()).isEqualTo(testGroup.getId());
    }

    @Test
    @DisplayName("Должен обновить существующий слот")
    void shouldUpdateExistingSlot() {
        // Given
        Slot slotToUpdate = slotRepository.findById(testSlot.getId()).orElseThrow();
        slotToUpdate.setStatus(SlotStatus.BOOKED);
        LocalDateTime newEnd = slotToUpdate.getEndTime().plusHours(1);
        slotToUpdate.setEndTime(newEnd);

        // When
        Slot updatedSlot = slotRepository.save(slotToUpdate);
        em.flush();
        em.clear();

        // Then
        Slot foundSlot = slotRepository.findById(testSlot.getId()).orElseThrow();
        assertThat(foundSlot.getStatus()).isEqualTo(SlotStatus.BOOKED);
        assertThat(foundSlot.getEndTime()).isEqualTo(newEnd);
    }

    @Test
    @DisplayName("Должен удалить слот")
    void shouldDeleteSlot() {
        // Given
        Long slotId = testSlot.getId();

        // When
        slotRepository.deleteById(slotId);
        em.flush();
        em.clear();

        // Then
        Optional<Slot> foundSlot = slotRepository.findById(slotId);
        assertThat(foundSlot).isEmpty();
    }

    @Test
    @DisplayName("Должен найти все слоты")
    void shouldFindAllSlots() {
        // When
        List<Slot> allSlots = slotRepository.findAll();

        // Then
        assertThat(allSlots).hasSize(2);
        assertThat(allSlots)
                .extracting(Slot::getId)
                .containsExactlyInAnyOrder(testSlot.getId(), testSlot2.getId());
    }

    @Test
    @DisplayName("Должен найти слоты по статусу")
    void shouldFindSlotsByStatus() {
        // When
        List<Slot> availableSlots = slotRepository.findAll().stream()
                .filter(slot -> slot.getStatus() == SlotStatus.FREE)
                .toList();

        List<Slot> bookedSlots = slotRepository.findAll().stream()
                .filter(slot -> slot.getStatus() == SlotStatus.BOOKED)
                .toList();

        // Then
        assertThat(availableSlots).hasSize(1);
        assertThat(availableSlots.get(0).getId()).isEqualTo(testSlot.getId());

        assertThat(bookedSlots).hasSize(1);
        assertThat(bookedSlots.get(0).getId()).isEqualTo(testSlot2.getId());
    }

}
