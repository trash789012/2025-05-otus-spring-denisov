package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.PathConfig;
import ru.otus.hw.config.security.SecurityConfig;
import ru.otus.hw.config.security.jwt.JwtAuthenticationFilter;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;
import ru.otus.hw.config.security.matchers.GroupSecurityMatcher;
import ru.otus.hw.converters.SlotConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.slot.SlotFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({SecurityConfigTest.class})
@ContextConfiguration(classes = {
        SlotServiceImpl.class,
        SecurityConfig.class,
})
@DisplayName("Тесты для сервиса слотов времени")
public class SlotServiceImplTest {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private SecurityConfig securityConfig;

    @MockBean
    private PathConfig pathConfig;

    @MockBean
    private SlotRepository slotRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private SlotConverter slotConverter;

    @MockBean
    private AclService aclService;

    @MockBean
    private PermissionEvaluator permissionEvaluator;

    @MockBean
    private GroupSecurityMatcher groupSecurityMatcher;

    @Autowired
    private SlotService slotService;

    @Test
    @DisplayName("Должен найти слот по ID")
    void shouldFindById() {
        // Given
        Long slotId = 1L;
        Slot slot = createTestSlot();
        SlotDto expectedDto = createSlotDto();

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(slotConverter.toDto(slot)).thenReturn(expectedDto);

        // When
        SlotDto result = slotService.findById(slotId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(slotRepository).findById(slotId);
        verify(slotConverter).toDto(slot);
    }

    @Test
    @DisplayName("Должен выбросить исключение при поиске несуществующего слота по ID")
    void shouldThrowExceptionWhenSlotNotFoundById() {
        // Given
        Long slotId = 999L;
        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> slotService.findById(slotId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Слот с ID 999 не найден");
    }

    @Test
    @DisplayName("Должен найти все слоты")
    void shouldFindAll() {
        // Given
        Slot slot1 = createTestSlot();
        Slot slot2 = createTestSlot();
        slot2.setId(2L);

        SlotDto dto1 = createSlotDto();
        SlotDto dto2 = new SlotDto(
                2L,
                slot2.getStartTime(),
                slot2.getEndTime(),
                SlotStatus.FREE,
                null
        );

        when(slotRepository.findAll()).thenReturn(List.of(slot1, slot2));
        when(slotConverter.toDto(slot1)).thenReturn(dto1);
        when(slotConverter.toDto(slot2)).thenReturn(dto2);

        // When
        List<SlotDto> result = slotService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(slotRepository).findAll();
        verify(slotConverter, times(2)).toDto(any(Slot.class));
    }

    @Test
    @DisplayName("Должен найти слоты за период")
    void shouldFindByPeriod() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        Slot slot1 = createTestSlot();
        Slot slot2 = createTestSlot();
        slot2.setId(2L);
        slot2.setStartTime(start.plusMinutes(30));
        slot2.setEndTime(end.minusMinutes(30));

        SlotDto dto1 = createSlotDto();
        SlotDto dto2 = new SlotDto(
                2L,
                slot2.getStartTime(),
                slot2.getEndTime(),
                SlotStatus.BOOKED,
                new GroupInfoDto(1L, "", "")
        );

        when(slotRepository.findAllByStartTimeBetween(start, end)).thenReturn(List.of(slot1, slot2));
        when(slotConverter.toDto(slot1)).thenReturn(dto1);
        when(slotConverter.toDto(slot2)).thenReturn(dto2);

        // When
        List<SlotDto> result = slotService.findByPeriod(start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(slotRepository).findAllByStartTimeBetween(start, end);
        verify(slotConverter, times(2)).toDto(any(Slot.class));
    }

    @Test
    @DisplayName("Должен создать новый слот")
    @WithMockUser(username = "user")
    void shouldInsertSlot() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 1L);
        Group group = createTestGroup();
        Slot savedSlot = createTestSlot();
        SlotDto expectedDto = createSlotDto();

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(slotRepository.findOverlappingSlots(startTime, endTime, null)).thenReturn(List.of());
        when(slotRepository.save(any(Slot.class))).thenReturn(savedSlot);
        when(slotConverter.toDto(savedSlot)).thenReturn(expectedDto);

        // When
        SlotDto result = slotService.insert(slotDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(groupRepository).findById(1L);
        verify(slotRepository).findOverlappingSlots(startTime, endTime, null);
        verify(slotRepository).save(any(Slot.class));
        verify(aclService).createSlotPermissions(savedSlot, BasePermission.WRITE, BasePermission.DELETE);
        verify(aclService).createAdminPermission(savedSlot);
        verify(aclService).createRootPermission(savedSlot);
        verify(aclService).flushAclCache();
    }

    @Test
    @DisplayName("Должен создать свободный слот без группы")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldInsertFreeSlotWithoutGroup() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, null);
        Slot newSlot = Slot.builder()
                .startTime(startTime)
                .endTime(endTime)
                .status(SlotStatus.FREE)
                .bookedBy(null)
                .build();

        Slot savedSlot = createTestSlot();
        savedSlot.setStatus(SlotStatus.FREE);
        savedSlot.setBookedBy(null);
        SlotDto expectedDto = new SlotDto(
                1L,
                startTime,
                endTime,
                SlotStatus.FREE,
                null
        );

        when(slotRepository.findOverlappingSlots(startTime, endTime, null)).thenReturn(List.of());
        when(slotRepository.save(any(Slot.class))).thenReturn(savedSlot);
        when(slotConverter.toDto(savedSlot)).thenReturn(expectedDto);

        // When
        SlotDto result = slotService.insert(slotDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        assertThat(result.status()).isEqualTo(SlotStatus.FREE);
        assertThat(result.id()).isNotNull();
        verify(slotRepository).findOverlappingSlots(startTime, endTime, null);
        verify(slotRepository).save(any(Slot.class));
        verify(groupRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Должен обновить существующий слот")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldUpdateSlot() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(1L, startTime, endTime, null, 1L);
        Slot existingSlot = createTestSlot();
        Group group = createTestGroup();
        Slot updatedSlot = createTestSlot();
        updatedSlot.setStartTime(startTime);
        updatedSlot.setEndTime(endTime);
        SlotDto expectedDto = createSlotDto();

        when(groupSecurityMatcher.isMemberBoth(1L, 1L)).thenReturn(true);
        when(slotRepository.findById(1L)).thenReturn(Optional.of(existingSlot));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(slotRepository.findOverlappingSlots(startTime, endTime, 1L)).thenReturn(List.of());
        when(slotRepository.save(existingSlot)).thenReturn(updatedSlot);
        when(slotConverter.toDto(updatedSlot)).thenReturn(expectedDto);

        // When
        SlotDto result = slotService.update(slotDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(slotRepository).findById(1L);
        verify(groupRepository).findById(1L);
        verify(slotRepository).findOverlappingSlots(startTime, endTime, 1L);
        verify(slotRepository).save(existingSlot);
        verify(aclService, never()).createSlotPermissions(any(), any());
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении с null ID")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenUpdateWithNullId() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 1L);

        // When & Then
        assertThatThrownBy(() -> slotService.update(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID слота is null");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании с пустым временем начала")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateWithNullStartTime() {
        // Given
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, null, endTime, null, 1L);

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Время начала и время окончания не должны быть пустыми");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании с пустым временем окончания")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateWithNullEndTime() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, null, null, 1L);

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Время начала и время окончания не должны быть пустыми");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании с одинаковым временем начала и окончания")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateWithEqualStartAndEndTime() {
        // Given
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        SlotFormDto slotDto = new SlotFormDto(null, time, time, null, 1L);

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Продолжительность не может быть равна нулю");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании с временем начала после окончания")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateWithStartAfterEnd() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 1L);

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Время начала не может быть больше времени окончания");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании пересекающегося слота")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateOverlappingSlot() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 1L);
        Slot overlappingSlot = createTestSlot();

        when(groupSecurityMatcher.isMember(1L)).thenReturn(true);
        when(slotRepository.findOverlappingSlots(startTime, endTime, null))
                .thenReturn(List.of(overlappingSlot));

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Слот пересекается с другим существующим слотом");
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании с несуществующей группой")
    @WithMockUser(username = "user")
    void shouldThrowExceptionWhenCreateWithNonExistentGroup() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 999L);

        when(groupSecurityMatcher.isMember(999L)).thenReturn(true);
        when(slotRepository.findOverlappingSlots(startTime, endTime, null)).thenReturn(List.of());
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Группа с ID 999 не найдена");
    }

    @Test
    @DisplayName("Должен удалить слот")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldDeleteSlot() {
        // Given
        Long slotId = 1L;

        when(groupSecurityMatcher.isMember(slotId)).thenReturn(true);
        when(slotRepository.existsById(slotId)).thenReturn(true);

        // When
        slotService.delete(slotId);

        // Then
        verify(slotRepository).deleteById(slotId);
    }

    @Test
    @DisplayName("Должен выбросить исключение при удалении несуществующего слота")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenDeleteNonExistentSlot() {
        // Given
        Long slotId = 999L;

        when(groupSecurityMatcher.isMember(slotId)).thenReturn(true);
        when(slotRepository.existsById(slotId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> slotService.delete(slotId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Слот с 999 не найден");
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при недостаточных правах для создания слота")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenInsufficientPermissionsForInsert() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(null, startTime, endTime, null, 1L);

        // When & Then
        assertThatThrownBy(() -> slotService.insert(slotDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при недостаточных правах для обновления слота")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenInsufficientPermissionsForUpdate() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        SlotFormDto slotDto = new SlotFormDto(1L, startTime, endTime, null, 1L);

        // When & Then
        assertThatThrownBy(() -> slotService.update(slotDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при недостаточных правах для удаления слота")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenInsufficientPermissionsForDelete() {
        // Given
        Long slotId = 1L;

        // When & Then
        assertThatThrownBy(() -> slotService.delete(slotId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Должен удалить слот с правами ADMIN и членством в группе")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldDeleteSlotWithAdminRoleAndGroupMembership() {
        // Given
        Long slotId = 1L;

        when(groupSecurityMatcher.isMemberBySlotId(slotId)).thenReturn(true);
        when(slotRepository.existsById(slotId)).thenReturn(true);

        // When
        slotService.delete(slotId);

        // Then
        verify(slotRepository).deleteById(slotId);
        verify(groupSecurityMatcher).isMemberBySlotId(slotId);
    }

    @Test
    @DisplayName("Должен удалить слот с DELETE permission и членством в группе")
    @WithMockUser(username = "user")
    void shouldDeleteSlotWithDeletePermissionAndGroupMembership() {
        // Given
        Long slotId = 1L;

        when(groupSecurityMatcher.isMemberBySlotId(slotId)).thenReturn(true);
        when(slotRepository.existsById(slotId)).thenReturn(true);
        when(permissionEvaluator.hasPermission(any(), eq(slotId), eq("ru.otus.hw.domain.Slot"), eq("DELETE")))
                .thenReturn(true);

        // When
        slotService.delete(slotId);

        // Then
        verify(slotRepository).deleteById(slotId);
        verify(groupSecurityMatcher).isMemberBySlotId(slotId);
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при удалении с ADMIN без членства в группе")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldThrowAccessDeniedWhenAdminWithoutGroupMembership() {
        // Given
        Long slotId = 1L;

        when(groupSecurityMatcher.isMemberBySlotId(slotId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> slotService.delete(slotId))
                .isInstanceOf(AccessDeniedException.class);

        verify(slotRepository, never()).existsById(any());
        verify(slotRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при удалении с DELETE permission без членства в группе")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenDeletePermissionWithoutGroupMembership() {
        // Given
        Long slotId = 1L;

        when(groupSecurityMatcher.isMemberBySlotId(slotId)).thenReturn(false);
        when(permissionEvaluator.hasPermission(any(), eq(slotId), eq("ru.otus.hw.domain.Slot"), eq("DELETE")))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> slotService.delete(slotId))
                .isInstanceOf(AccessDeniedException.class);

        verify(slotRepository, never()).existsById(any());
        verify(slotRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при удалении с USER без прав")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenUserWithoutPermissions() {
        // Given
        Long slotId = 1L;

        // When & Then
        assertThatThrownBy(() -> slotService.delete(slotId))
                .isInstanceOf(AccessDeniedException.class);
        when(permissionEvaluator.hasPermission(any(), eq(slotId), eq("ru.otus.hw.domain.Slot"), eq("DELETE")))
                .thenReturn(false);

        verify(groupSecurityMatcher, never()).isMemberBySlotId(any());
        verify(slotRepository, never()).existsById(any());
        verify(slotRepository, never()).deleteById(any());
    }


    // -------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // -------------------------------------------------------------------------

    private Slot createTestSlot() {
        return Slot.builder()
                .id(1L)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(SlotStatus.BOOKED)
                .bookedBy(createTestGroup())
                .build();
    }

    private Group createTestGroup() {
        return Group.builder()
                .id(1L)
                .name("testgroup")
                .description("Test group description")
                .build();
    }

    private SlotDto createSlotDto() {
        return new SlotDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                SlotStatus.BOOKED,
                new GroupInfoDto(1L, "", "")
        );
    }
}
