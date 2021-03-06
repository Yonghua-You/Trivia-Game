package com.campsite.reservation.web;

import com.campsite.reservation.domain.Reservation;
import com.campsite.reservation.domain.mapper.ReservationMapper;
import com.campsite.reservation.service.IllegalReservationStateException;
import com.campsite.reservation.service.ReservationDatesNotAvailableException;
import com.campsite.reservation.service.ReservationNotFoundException;
import com.campsite.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Reservation API", description = "Reservation APIs")
@RequestMapping(path = "/reservation")
public class ReservationController {
    private ReservationService service;
    @Autowired
    public ReservationController(ReservationService reservationService)
    {
        this.service = reservationService;
    }

    protected ReservationController()
    {

    }
    @Operation(summary = "search vacant days within a given period", responses = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(value = "/vacantdays", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<LocalDate>> getVacantDates(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1);
        }
        if (endDate == null) {
            endDate = startDate.plusMonths(1);
        }
        List<LocalDate> vacantDates;
        try {
            vacantDates = service.searchVacantDays(startDate, endDate);
        }catch (IllegalArgumentException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }
        return new ResponseEntity<>(vacantDates, HttpStatus.OK);
    }
    @Operation(summary = "Find the reservation", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @GetMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<ReservationDto>> get(@PathVariable() UUID guid)
    {
        Reservation reservation;
        try {
            reservation = service.searchReservationByGuid(guid);
        }catch (ReservationNotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }
        return new ResponseEntity<>(getResource(reservation), HttpStatus.OK);
    }

    @Operation(summary = "Create new reservation", responses = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<ReservationDto>> create(@RequestBody() @Valid ReservationDto reservationDto)
    {
        Reservation reservation;
        try {
            reservation = service.save(ReservationMapper.INSTANCE.toReservation(reservationDto));
        }catch(IllegalReservationStateException | ReservationDatesNotAvailableException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }
        EntityModel<ReservationDto> resource = getResource(reservation);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()));

        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }
    @Operation(summary = "Update the reservation", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @PutMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<ReservationDto>> update(@PathVariable("guid") UUID guid, @RequestBody @Valid ReservationDto reservationDto)
    {
        Reservation reservation;
        try {
            reservation = service.updateReservation(
                    ReservationMapper.INSTANCE.toReservation(reservationDto));
        }catch (IllegalReservationStateException  | ReservationDatesNotAvailableException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, ex.getMessage()
            );
        }catch (ReservationNotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }

        EntityModel<ReservationDto> resource = getResource(reservation);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
    @Operation(summary = "Cancel the reservation", responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @DeleteMapping(value = "/{guid}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancel (@PathVariable("guid") UUID guid)
    {
        boolean cancelled = false;
        try {
            cancelled = service.cancel(guid);
        }catch (ReservationNotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage()
            );
        }
        if (cancelled) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private EntityModel<ReservationDto> getResource(Reservation reservation) {
        EntityModel<ReservationDto> resource = EntityModel.of(ReservationMapper.INSTANCE.toReservationDto(reservation));
        Link selfLink = WebMvcLinkBuilder
                .linkTo(this.getClass()).slash(reservation.getGuid()).withSelfRel();
        resource.add(selfLink);
        return resource;
    }
}
