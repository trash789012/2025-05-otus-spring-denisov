package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.PathConfig;
import ru.otus.hw.config.security.SecurityConfig;
import ru.otus.hw.config.security.jwt.JwtAuthenticationFilter;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;
import ru.otus.hw.config.security.matchers.GroupSecurityMatcher;
import ru.otus.hw.converters.GroupConverter;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersAndSlotsDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;
import ru.otus.hw.dto.user.UserFormInfoDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;
import ru.otus.hw.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тесты для сервиса групп")
@Import({
        PathConfig.class,
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtTokenProvider.class,
        SecurityConfig.class
})
public class GroupServiceImplTest {

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserConverter userConverter;

    @MockBean
    private SlotRepository slotRepository;

    @MockBean
    private GroupConverter groupConverter;

    @MockBean
    private AclService aclService;

    @MockBean
    private GroupSecurityMatcher groupSecurityMatcher;

    @Autowired
    private GroupServiceImpl groupService;

    @Test
    @DisplayName("Должен найти группу по ID")
    void shouldFindById() {
        // Given
        Long groupId = 1L;
        Group group = createTestGroup();
        GroupDto expectedDto = createGroupDto();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupConverter.toDto(group)).thenReturn(expectedDto);

        // When
        GroupDto result = groupService.findById(groupId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(groupRepository).findById(groupId);
        verify(groupConverter).toDto(group);
    }

    @Test
    @DisplayName("Должен бросить исключение при поиске несуществующей группы по ID")
    void shouldThrowExceptionWhenGroupNotFoundById() {
        // Given
        long groupId = 999L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> groupService.findById(groupId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Группа с ID 999 не найдена");
    }

    @Test
    @DisplayName("Должен найти все группы без вложенных объектов")
    void shouldFindAllWithoutNested() {
        // Given
        Group group1 = createTestGroup();
        Group group2 = createTestGroup();
        group2.setId(2L);
        group2.setName("anothergroup");

        GroupInfoDto dto1 = createGroupInfoDto();
        GroupInfoDto dto2 = new GroupInfoDto(
                2L,
                "anothergroup",
                "Another group description"
        );

        when(groupRepository.findAll()).thenReturn(List.of(group1, group2));
        when(groupConverter.toWithoutNestedDto(group1)).thenReturn(dto1);
        when(groupConverter.toWithoutNestedDto(group2)).thenReturn(dto2);

        // When
        List<GroupInfoDto> result = groupService.findAllWithoutNested();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(groupRepository).findAll();
        verify(groupConverter, times(2))
                .toWithoutNestedDto(any(Group.class));
    }

    @Test
    @DisplayName("Должен найти группу с участниками по ID")
    void shouldFindGroupWithMembersById() {
        // Given
        Long groupId = 1L;
        Group group = createTestGroup();
        GroupWithMembersDto expectedDto = createGroupWithMembersDto();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupConverter.toWithMembersDto(group)).thenReturn(expectedDto);

        // When
        GroupWithMembersDto result = groupService.findGroupWithMembersById(groupId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(groupRepository).findById(groupId);
        verify(groupConverter).toWithMembersDto(group);
    }

    @Test
    @DisplayName("Должен найти группу с участниками и слотами по ID")
    void shouldFindGroupWithMembersAndSlotsById() {
        // Given
        Long groupId = 1L;
        Group group = createTestGroup();
        GroupWithMembersAndSlotsDto expectedDto = createGroupWithMembersAndSlotsDto();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupConverter.toWithMembersAndSlotsDto(group)).thenReturn(expectedDto);

        // When
        GroupWithMembersAndSlotsDto result = groupService.findGroupWithMembersAndSlotsById(groupId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(groupRepository).findById(groupId);
        verify(groupConverter).toWithMembersAndSlotsDto(group);
    }

    @Test
    @DisplayName("Должен удалить участника из группы")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldDeleteMemberFromGroup() {
        // Given
        Long groupId = 1L;
        Long memberId = 2L;
        Group group = createTestGroup();
        User member = createTestUser();
        member.setId(2L);
        List<User> members = new ArrayList<>();
        members.add(member);
        group.setMembers(members);

        doReturn(true).when(groupSecurityMatcher).isMember(anyLong());
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        groupService.deleteMemberFromGroup(memberId, groupId);

        // Then
        assertThat(group.getMembers()).isEmpty();
        verify(groupRepository).findById(groupId);
        verify(userRepository).findById(memberId);
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("Должен выбросить исключение при удалении участника с null ID группы")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenDeleteMemberWithNullGroupId() {
        // When & Then
        assertThatThrownBy(() -> groupService.deleteMemberFromGroup(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID группы null");
    }

    @Test
    @DisplayName("Должен выбросить исключение при удалении участника с null ID участника")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenDeleteMemberWithNullMemberId() {
        // When & Then
        assertThatThrownBy(() -> groupService.deleteMemberFromGroup(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID участника null");
    }

    @Test
    @DisplayName("Должен найти пользователей для добавления в группу по поисковому паттерну")
    void shouldFindUsersForGroupBySearchTerm() {
        // Given
        Long groupId = 1L;
        String searchTerm = "test";
        Group group = createTestGroup();
        User existingUser = createTestUser();
        group.setMembers(List.of(existingUser));

        User newUser1 = createTestUser();
        newUser1.setId(2L);
        newUser1.setName("testuser2");
        User newUser2 = createTestUser();
        newUser2.setId(3L);
        newUser2.setName("testuser3");

        UserFormInfoDto userDto1 = new UserFormInfoDto(
                2L,
                "testuser2",
                "Test2",
                "User2",
                null
        );
        UserFormInfoDto userDto2 = new UserFormInfoDto(
                3L,
                "testuser3",
                "Test3",
                "User3",
                null
        );

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findBySearchTermAndIdNotIn(searchTerm, List.of(1L)))
                .thenReturn(List.of(newUser1, newUser2));
        when(userConverter.toInfoDto(newUser1)).thenReturn(userDto1);
        when(userConverter.toInfoDto(newUser2)).thenReturn(userDto2);

        // When
        List<UserFormInfoDto> result = groupService.findUsersForGroupBySearchTerm(groupId, searchTerm);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userDto1, userDto2);
        verify(groupRepository).findById(groupId);
        verify(userRepository).findBySearchTermAndIdNotIn(searchTerm, List.of(1L));
    }

    @Test
    @DisplayName("Должен найти всех пользователей по поисковому паттерну когда в группе нет участников")
    void shouldFindAllUsersWhenGroupHasNoMembers() {
        // Given
        Long groupId = 1L;
        String searchTerm = "test";
        Group group = createTestGroup();
        group.setMembers(List.of());

        User user = createTestUser();
        UserFormInfoDto userDto = new UserFormInfoDto(1L, "testuser", "Test", "User", null);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findBySearchTerm(searchTerm)).thenReturn(List.of(user));
        when(userConverter.toInfoDto(user)).thenReturn(userDto);

        // When
        List<UserFormInfoDto> result = groupService.findUsersForGroupBySearchTerm(groupId, searchTerm);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(userDto);
        verify(groupRepository).findById(groupId);
        verify(userRepository).findBySearchTerm(searchTerm);
    }

    @Test
    @DisplayName("Должен добавить участников в группу")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldAddMembersToGroup() {
        // Given
        Long groupId = 1L;
        List<Long> memberIds = List.of(2L, 3L);
        Group group = createTestGroup();
        User existingMember = createTestUser();
        List<User> members = new ArrayList<>();
        members.add(existingMember);
        group.setMembers(members);

        User newMember1 = createTestUser();
        newMember1.setId(2L);
        newMember1.setName("user2");
        User newMember2 = createTestUser();
        newMember2.setId(3L);
        newMember2.setName("user3");

        GroupWithMembersDto expectedDto = createGroupWithMembersDto();

        when(groupSecurityMatcher.isMember(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findAllById(memberIds)).thenReturn(List.of(newMember1, newMember2));
        when(groupRepository.save(group)).thenReturn(group);
        when(groupConverter.toWithMembersDto(group)).thenReturn(expectedDto);

        // When
        GroupWithMembersDto result = groupService.addMembersToGroup(memberIds, groupId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        assertThat(group.getMembers()).hasSize(3);
        assertThat(group.getMembers()).contains(existingMember, newMember1, newMember2);
        verify(groupRepository).findById(groupId);
        verify(userRepository, times(2)).findAllById(memberIds);
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("Должен выбросить исключение при добавлении пустого списка участников")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldThrowExceptionWhenAddEmptyMembersList() {
        // Given
        Long groupId = 1L;
        List<Long> memberIds = List.of();

        when(groupSecurityMatcher.isMember(groupId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupService.addMembersToGroup(memberIds, groupId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID участника null");
    }

    @Test
    @DisplayName("Должен выбросить исключение при добавлении несуществующих участников")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldThrowExceptionWhenAddNonExistentMembers() {
        // Given
        Long groupId = 1L;
        List<Long> memberIds = List.of(2L, 3L);
        Group group = createTestGroup();

        when(groupSecurityMatcher.isMember(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findAllById(memberIds)).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> groupService.addMembersToGroup(memberIds, groupId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пользователи с ids [2, 3] не найдены");
    }

    @Test
    @DisplayName("Должен создать новую группу")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldInsertGroup() {
        // Given
        GroupFormDto groupDto = new GroupFormDto(null, "newgroup", "New group description",
                List.of(1L, 2L), List.of(1L, 2L));
        Group newGroup = createTestGroup();
        newGroup.setId(2L);
        newGroup.setName("newgroup");
        GroupDto expectedDto = new GroupDto(
                2L,
                "newgroup",
                "New group description",
                null,
                null);

        List<User> members = List.of(createTestUser(), createTestUser());
        List<Slot> slots = List.of(createTestSlot(), createTestSlot());

        when(userRepository.findAllById(groupDto.memberIds())).thenReturn(members);
        when(slotRepository.findAllById(groupDto.slotIds())).thenReturn(slots);
        when(groupRepository.save(any(Group.class))).thenReturn(newGroup);
        when(groupConverter.toDto(newGroup)).thenReturn(expectedDto);

        // When
        GroupDto result = groupService.insert(groupDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findAllById(groupDto.memberIds());
        verify(slotRepository).findAllById(groupDto.slotIds());
        verify(groupRepository).save(any(Group.class));
        verify(aclService).createPermission(newGroup, BasePermission.WRITE);
        verify(aclService).createPermission(newGroup, BasePermission.DELETE);
        verify(aclService).createAdminPermission(newGroup);
        verify(aclService).createRootPermission(newGroup);
    }

    @Test
    @DisplayName("Должен обновить существующую группу")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldUpdateGroup() {
        // Given
        GroupFormDto groupDto = new GroupFormDto(1L, "updatedgroup", "Updated description",
                List.of(1L, 2L), List.of(1L, 2L));
        Group existingGroup = createTestGroup();
        Group updatedGroup = createTestGroup();
        updatedGroup.setName("updatedgroup");
        updatedGroup.setDescription("Updated description");
        GroupDto expectedDto = new GroupDto(
                1L,
                "updatedgroup",
                "Updated description",
                null,
                null);

        List<User> members = List.of(createTestUser(), createTestUser());
        List<Slot> slots = List.of(createTestSlot(), createTestSlot());

        when(groupSecurityMatcher.isMember(groupDto.id())).thenReturn(true);
        when(groupRepository.findById(groupDto.id())).thenReturn(Optional.of(existingGroup));
        when(userRepository.findAllById(groupDto.memberIds())).thenReturn(members);
        when(slotRepository.findAllById(groupDto.slotIds())).thenReturn(slots);
        when(groupRepository.save(existingGroup)).thenReturn(updatedGroup);
        when(groupConverter.toDto(updatedGroup)).thenReturn(expectedDto);

        // When
        GroupDto result = groupService.update(groupDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(groupRepository).findById(groupDto.id());
        verify(userRepository).findAllById(groupDto.memberIds());
        verify(slotRepository).findAllById(groupDto.slotIds());
        verify(groupRepository).save(existingGroup);
        verify(aclService, never()).createPermission(any(), any());
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании группы с пустым именем")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenCreateGroupWithEmptyName() {
        // Given
        GroupFormDto groupDto = new GroupFormDto(null, "", "Description", List.of(), List.of());

        // When & Then
        assertThatThrownBy(() -> groupService.insert(groupDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Имя группы должно быть заполнено");
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении с null ID")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldThrowExceptionWhenUpdateWithNullId() {
        // Given
        GroupFormDto groupDto = new GroupFormDto(
                null,
                "group",
                "Description",
                null,
                null
        );

        // When & Then
        assertThatThrownBy(() -> groupService.update(groupDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group id is null");
    }

    @Test
    @DisplayName("Должен удалить группу по ID")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldDeleteById() {
        // Given
        Long groupId = 1L;

        when(groupSecurityMatcher.isMember(groupId)).thenReturn(true);

        // When
        groupService.deleteById(groupId);

        // Then
        verify(groupRepository).deleteById(groupId);
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при недостаточных правах для удаления группы")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenInsufficientPermissionsForDelete() {
        // Given
        Long groupId = 1L;

        // When & Then
        assertThatThrownBy(() -> groupService.deleteById(groupId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Должен выбросить AccessDeniedException при недостаточных правах для добавления участников")
    @WithMockUser(username = "user")
    void shouldThrowAccessDeniedWhenInsufficientPermissionsForAddMembers() {
        // Given
        List<Long> memberIds = List.of(2L);
        Long groupId = 1L;

        // When & Then
        assertThatThrownBy(() -> groupService.addMembersToGroup(memberIds, groupId))
                .isInstanceOf(AccessDeniedException.class);
    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // -------------------------------------------------------------------------

    private Group createTestGroup() {
        return Group.builder()
                .id(1L)
                .name("testgroup")
                .description("Test group description")
                .members(List.of())
                .slots(List.of())
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("testuser")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    private Slot createTestSlot() {
        return Slot.builder()
                .id(1L)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(SlotStatus.FREE)
                .build();
    }

    private GroupDto createGroupDto() {
        return new GroupDto(1L, "testgroup", "Test group description",
                null, null);
    }

    private GroupInfoDto createGroupInfoDto() {
        return new GroupInfoDto(1L, "testgroup", "Test group description");
    }

    private GroupWithMembersDto createGroupWithMembersDto() {
        return new GroupWithMembersDto(1L, "testgroup", "Test group description", List.of());
    }

    private GroupWithMembersAndSlotsDto createGroupWithMembersAndSlotsDto() {
        return new GroupWithMembersAndSlotsDto(1L, "testgroup", "Test group description", List.of(), List.of());
    }

}
