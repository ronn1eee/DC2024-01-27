package org.education.controller;

import jakarta.validation.Valid;
import org.education.bean.DTO.MarkerRequestTo;
import org.education.bean.DTO.MarkerResponseTo;
import org.education.bean.Marker;
import org.education.exception.AlreadyExists;
import org.education.exception.ErrorResponse;
import org.education.exception.IncorrectValuesException;
import org.education.exception.NoSuchMarker;
import org.education.service.MarkerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/markers")
public class MarkerController {

    private final MarkerService markerService;

    private final ModelMapper modelMapper;

    @Autowired
    public MarkerController(MarkerService markerService, ModelMapper modelMapper) {
        this.markerService = markerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAllMarkers(){
        List<MarkerResponseTo> response = new ArrayList<>();
        for(Marker marker : markerService.getAll()){
            response.add(modelMapper.map(marker, MarkerResponseTo.class));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> createMarker(@RequestBody @Valid MarkerRequestTo markerRequestTo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IncorrectValuesException("Incorrect input values");
        }
        Marker marker = markerService.create(modelMapper.map(markerRequestTo, Marker.class));
        return new ResponseEntity<>(modelMapper.map(marker, MarkerResponseTo.class), HttpStatus.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> updateMarker(@RequestBody @Valid MarkerRequestTo markerRequestTo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new IncorrectValuesException("Incorrect input values");
        }
        Marker marker = markerService.update(modelMapper.map(markerRequestTo, Marker.class));
        return new ResponseEntity<>(modelMapper.map(marker, MarkerResponseTo.class), HttpStatus.valueOf(200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMarker(@PathVariable int id){
        markerService.delete(id);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getMarker(@PathVariable int id){
        return new ResponseEntity<>(modelMapper.map(markerService.getById(id), MarkerResponseTo.class), HttpStatus.valueOf(200));
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(NoSuchMarker exception){
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.value()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(IncorrectValuesException exception){
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(AlreadyExists exception){
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), 403),HttpStatus.valueOf(403));
    }
}
