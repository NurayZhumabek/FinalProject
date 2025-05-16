package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.compilations.*;
import com.practice.discoveryEvents.events.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final EventService eventService;
    private final CompilationService compilationService;

    public AdminController(UserService userService, ModelMapper modelMapper, EventService eventService, CompilationService compilationService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.eventService = eventService;
        this.compilationService = compilationService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsers(@RequestParam(value = "from", defaultValue = "0") int from,
                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                  @RequestParam(value = "ids", required = false) List<Integer> ids) {
        return userService.getAllUsers(ids, from, size)
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody @Valid NewUserRequest userDTO) {

        User user = modelMapper.map(userDTO, User.class);
        User createdUser = userService.createUser(user);

        return modelMapper.map(createdUser, UserDTO.class);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") int userId) {
        userService.deleteUser(userId);
    }



    @GetMapping("/events")
    public List<EventFullDTO> getEvents(@ModelAttribute EventAdminFilterParams filterParams) {

        return eventService.searchEventsByAdmin(filterParams)
                .stream()
                .map(e -> modelMapper.map(e, EventFullDTO.class))
                .collect(Collectors.toList());

    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEvent(@PathVariable("eventId") int eventId, @RequestBody @Valid UpdateEventAdminRequestDTO eventDTO) {
        Event updatedEvent = eventService.updateEventByAdmin(eventId, eventDTO);
        return modelMapper.map(updatedEvent, EventFullDTO.class);
    }



    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDTO createCompilation(@RequestBody @Valid NewCompilationDTO compilationDTO) {

        Compilation createdCompilation = compilationService.createCompilation(compilationDTO);
        return modelMapper.map(createdCompilation, CompilationDTO.class);

    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") int compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDTO updateCompilation(@PathVariable("compId") int compId, @RequestBody @Valid UpdateCompilationRequestDTO compDTO) {

        Compilation updatedCompilation = compilationService.updateCompilationById(compId, compDTO);
        return modelMapper.map(updatedCompilation, CompilationDTO.class);

    }








}
