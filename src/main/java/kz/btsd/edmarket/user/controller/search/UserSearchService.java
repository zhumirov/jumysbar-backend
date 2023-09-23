package kz.btsd.edmarket.user.controller.search;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserShortDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserSearchService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventProgressRepository eventProgressRepository;


    public long countNewUsersForlast7(Platform platform) {
        LocalDate localDatePeriod7Days = LocalDate.now().minusDays(7);
        Date datePeriod7Days = Date
                .from(localDatePeriod7Days.atStartOfDay().atZone(ZoneId.of("Asia/Almaty"))
                        .toInstant());
        return userRepository.countByPlatformAndCreatedDateAfterAndDeletedFalse(platform, datePeriod7Days);
    }

    public List<EventTitleDto> top3SubscribedEvents(Platform platform) {
        List<Long> ids = eventRepository.countMostSubscribedByPlatform(platform, PageRequest.of(0, 3));
        List<EventTitleDto> popularEvents = new ArrayList<>();
        for (Long eventId :
                ids) {
            Event event = eventRepository.findById(eventId).get();
            popularEvents.add(new EventTitleDto(event.getId(), event.getTitle()));
        }
        return popularEvents;
    }

    public List<UserShortDto> top3ActiveUsers(Platform platform) {
        List<Long> users = eventProgressRepository.topViewedSubsectionsSizeByPlatform(platform, PageRequest.of(0, 3));
        List<UserShortDto> userShortDtos = new ArrayList<>();
        for (Long userId :
                users) {
            UserShortDto userShortDto = userConverter.convertToShortDto(userRepository.findById(userId).get());
            userShortDtos.add(userShortDto);
        }
        return userShortDtos;
    }

    public ByteArrayOutputStream createCVS(List<UserStatDto> users) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (
                CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream), CSVFormat.DEFAULT
                        .withHeader("Время регистрации", "ФИО Сотрудника", "Почта", "Телефон", "Курсы, которые проходит", "Время последнего входа", "Пройдено шагов"));
        ) {
            for (UserStatDto user : users) {
                csvPrinter.printRecord(user.getCreatedDate(), user.getName(), user.getEmail(), user.getPhone(), user.getEvents(), user.getLastActivityDate(), user.getViewedSubsectionsSize());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream;
    }
}
