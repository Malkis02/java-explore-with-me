package ru.practicum.server.event.location;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;


@Getter
@Setter
@Embeddable
public class Location {
    private Float lat;
    private Float lon;
}
