package ru.otus.hw.service;

import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.slot.SlotFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления временными слотами (тайм-слотами)
 * Обеспечивает функциональность бронирования временных интервалов пользователями или группами,
 * включая проверку на пересечение слотов и управление доступностью.
 */
public interface SlotService {

    /**
     * Найти слот по идентификатору.
     * Включает информацию о забронировавшем пользователе/группе и временных рамках.
     *
     * @param id идентификатор слота
     * @return DTO слота с полной информацией включая связи
     * @throws EntityNotFoundException если слот с указанным ID не существует
     */
    SlotDto findById(Long id);

    /**
     * Получить все существующие слоты.
     * Может требовать пагинацию при большом количестве записей.
     *
     * @return список всех слотов в системе
     */
    List<SlotDto> findAll();

    /**
     * Найти слоты в указанном временном периоде.
     * Возвращает все слоты, которые полностью или частично попадают в заданный интервал.
     *
     * @param start начало периода поиска (включительно)
     * @param end конец периода поиска (включительно)
     * @return список слотов, пересекающихся с указанным периодом
     * @throws IllegalArgumentException если start после end, или период слишком большой
     */
    List<SlotDto> findByPeriod(LocalDateTime start, LocalDateTime end);

    /**
     * Создать новый временной слот.
     * Выполняет проверки на валидность данных и отсутствие пересечений с существующими слотами.
     *
     * @param slotDto DTO с данными для создания слота (время, длительность, забронировавший)
     * @return DTO созданного слота с присвоенным ID
     * @throws IllegalArgumentException если время начала позже времени окончания,
     *         длительность отрицательная или обязательные поля не заполнены
     * @throws EntityNotFoundException если пользователь или группа не существуют
     */
    SlotDto insert(SlotFormDto slotDto);

    /**
     * Обновить информацию о существующем слоте.
     * Позволяет изменить время, длительность или забронировавшего пользователя/группу.
     *
     * @param slotDto DTO с обновленными данными слота (должен содержать ID)
     * @return DTO обновленного слота
     * @throws EntityNotFoundException если слот с указанным ID не существует
     * @throws IllegalArgumentException если обновленные данные некорректны
     */
    SlotDto update(SlotFormDto slotDto);

    /**
     * Удалить слот по идентификатору.
     * Удаление невозможно если слот связан с другими сущностями (если есть зависимости).
     *
     * @param id идентификатор удаляемого слота
     * @throws EntityNotFoundException если слот не найден
     */
    void delete(Long id);
}
