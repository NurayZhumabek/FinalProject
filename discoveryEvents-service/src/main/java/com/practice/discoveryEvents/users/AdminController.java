package com.practice.discoveryEvents.users;


import com.practice.discoveryEvents.categories.Category;
import com.practice.discoveryEvents.categories.CategoryDTO;
import com.practice.discoveryEvents.categories.CategoryService;
import com.practice.discoveryEvents.categories.NewCategoryDTO;
import com.practice.discoveryEvents.compilations.*;
import com.practice.discoveryEvents.events.*;
import com.practice.discoveryEvents.stats.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    private final CategoryService categoryService;
    private final CompilationMapper compilationMapper;
    private final StatsClient statsClient;

    public AdminController(UserService userService, ModelMapper modelMapper, EventService eventService,
                           CompilationService compilationService, CategoryService categoryService, CompilationMapper compilationMapper, StatsClient statsClient) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.categoryService = categoryService;
        this.compilationMapper = compilationMapper;
        this.statsClient = statsClient;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsers(@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
                                  @RequestParam(value = "ids", required = false) List<Integer> ids,
                                  HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());


        return userService.getAllUsers(ids, from, size)
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody @Valid NewUserRequest userDTO,
                              HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        User user = modelMapper.map(userDTO, User.class);
        User createdUser = userService.createUser(user);

        return modelMapper.map(createdUser, UserDTO.class);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") int userId,
                           HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        userService.deleteUser(userId);
    }


    @GetMapping("/events")
    public List<EventFullDTO> getEvents(@ModelAttribute EventAdminFilterParams filterParams,
                                        HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return eventService.searchEventsByAdmin(filterParams)
                .stream()
                .map(e -> modelMapper.map(e, EventFullDTO.class))
                .collect(Collectors.toList());

    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEvent(@PathVariable("eventId") int eventId, @RequestBody @Valid UpdateEventAdminRequestDTO eventDTO,
                                    HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Event updatedEvent = eventService.updateEventByAdmin(eventId, eventDTO);
        return modelMapper.map(updatedEvent, EventFullDTO.class);
    }


    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDTO createCompilation(@RequestBody @Valid NewCompilationDTO compilationDTO,
                                            HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());


        Compilation createdCompilation = compilationService.createCompilation(compilationDTO);
        return modelMapper.map(createdCompilation, CompilationDTO.class);

    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") int compId,
                                  HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDTO updateCompilation(
            @PathVariable("compId") int compId,
            @RequestBody @Valid UpdateCompilationRequestDTO compDTO,
            HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Compilation updatedCompilation = compilationService.updateCompilationById(compId, compDTO);
        return compilationMapper.toCompilationDTO(updatedCompilation);
    }


    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody @Valid NewCategoryDTO categoryDTO,
                                      HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category created = categoryService.createCategory(category);
        return modelMapper.map(created, CategoryDTO.class);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Integer catId,
                               HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO updateCategory(@PathVariable("catId") Integer catId,
                                      @RequestBody @Valid NewCategoryDTO categoryDTO,
                                      HttpServletRequest request) {
        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category updated = categoryService.updateCategory(catId, category);
        return modelMapper.map(updated, CategoryDTO.class);
    }


    public EventShortDTO toShortDto(Event event) {
        EventShortDTO dto = new EventShortDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.getPaid());
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setViews(event.getViews());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(event.getCategory().getId());
        categoryDTO.setName(event.getCategory().getName());
        dto.setCategory(categoryDTO);

        UserShortDTO userDTO = new UserShortDTO();
        userDTO.setId(event.getInitiator().getId());
        userDTO.setName(event.getInitiator().getName());
        dto.setInitiator(userDTO);

        return dto;
    }
}
