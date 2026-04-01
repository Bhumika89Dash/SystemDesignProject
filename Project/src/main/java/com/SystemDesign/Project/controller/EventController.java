package com.SystemDesign.Project.controller;
import com.SystemDesign.Project.model.Event;
import com.SystemDesign.Project.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event")
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    //@GetMapping("/events?page=0&size=10")
    //public List<Event> getAllEvents() {
      //  return eventService.getAllEvents();
    //}

    // Get mapping for pagination
    @GetMapping("/events")
    public List<Event> getAllEvents(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return eventService.getAllEvents(page, size);
    }

    // LRU Impl get ----------------------------------------------------------
    @GetMapping("/events-cache")
    public List<Event> getEventsWithCache(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return eventService.getAllEventsWithCache(page, size);
    }

    // LRU IMPL post Create event
    @PostMapping("/event-cache")
    public Event createEventCache(@RequestBody Event event) {

        System.out.println("\n================= 📢 NEW POST REQUEST =================");

        Event createdEvent = eventService.cachedCreateEvent(event);

        System.out.println("================= ✅ POST COMPLETED =================\n");

        return createdEvent;
    }


    @GetMapping("/events/{userId}")
    public List<Event> getEventsByUser(@PathVariable Long userId) {
        return eventService.getEventsByUser(userId);
    }
}