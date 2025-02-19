package com.safeticket.venue.dto;

import com.safeticket.venue.entity.Address;
import com.safeticket.venue.entity.Venue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VenueDTO {
    private Long id;
    private String name;
    private Address address;
    private int capacity;

    public static VenueDTO of(Venue venue) {
        if (venue == null) {
            return null;
        }
        return VenueDTO.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .capacity(venue.getCapacity())
                .build();
    }
}
