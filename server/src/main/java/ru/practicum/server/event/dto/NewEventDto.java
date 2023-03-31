package ru.practicum.server.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.event.location.Location;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Invalid length annotation")
    @NotNull(message = "Field: annotation. Error: must not be blank. Value: null")
    private String annotation;
    @Min(1)
    @NotNull
    private Long category;
    @Size(min = 20, max = 7000, message = "Invalid length description")
    @NotNull(message = "Field: description. Error: must not be blank. Value: null")
    private String description;
    @NotNull(message = "Field: eventDate. Error: must not be blank. Value: null")
    private String eventDate;
    @NotNull(message = "Field: location. Error: must not be blank. Value: null")
    private Location location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Invalid length annotation")
    @NotNull(message = "Field: title. Error: must not be blank. Value: null")
    private String title;
}
