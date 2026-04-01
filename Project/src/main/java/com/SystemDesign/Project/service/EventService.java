package com.SystemDesign.Project.service;

import com.SystemDesign.Project.cache.LRUCache;
import com.SystemDesign.Project.model.Event;
import com.SystemDesign.Project.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LRUCache<String, List<Event>> cache = new LRUCache<>(5);

    private int cacheHits = 0;
    private int cacheMiss = 0;


    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;

    }

    public Event createEvent(Event event) {

        Event saved = eventRepository.save(event);
 // Clearing the cache when stroing a new event
        System.out.println("⚠️ New Event Added → Clearing Cache");
        cache.clear();

        return saved;

    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();

    }

    public List<Event> getEventsByUser(Long userId) {
        return eventRepository.findByUserId(userId);
     }

// -------------------------------------------------------------------------------------------------------------------------------
     // LRU algorithm to get all the events
     public List<Event> getAllEventsWithCache(int page, int size) {

         String key = "events:" + page + ":" + size;

         long start = System.currentTimeMillis();

         // ✅ CACHE HIT
         if (cache.containsKey(key)) {
             cacheHits++;

             long end = System.currentTimeMillis();

             System.out.println("🚀 [CACHE HIT] Key: " + key +
                     " | Time: " + (end - start) + " ms");

             printStats();

             return cache.get(key);
         }

         // ❌ CACHE MISS
         cacheMiss++;

         System.out.println("❌ [CACHE MISS] Key: " + key + " → Fetching from DB");

         Pageable pageable = PageRequest.of(page, size);
         List<Event> events = eventRepository.findAll(pageable).getContent();

         long end = System.currentTimeMillis();

         System.out.println("📦 [DB FETCH DONE] Time: " + (end - start) + " ms");

         // Store in cache
         cache.put(key, events);

         printStats();

         return events;
     }

    private void printStats() {
        int total = cacheHits + cacheMiss;

        double hitRatio = total == 0 ? 0 : (cacheHits * 100.0 / total);

        System.out.println("📊 Cache Stats → Hits: " + cacheHits +
                " | Miss: " + cacheMiss +
                " | Hit Ratio: " + String.format("%.2f", hitRatio) + "%");
    }

    //---------------------------------------------------------------------------------------------

    // IMPLEMENTING CACHED CREATE EVENT ------------------------------------------------------------
    public Event cachedCreateEvent(Event event) {

        long start = System.currentTimeMillis();

        System.out.println("🟡 [CREATE EVENT] Incoming request...");
        System.out.println("📥 Event Data: " + event);

        // Save to DB
        Event savedEvent = eventRepository.save(event);

        long dbEnd = System.currentTimeMillis();

        System.out.println("✅ [DB SAVE SUCCESS] Event ID: " + savedEvent.getId() +
                " | Time: " + (dbEnd - start) + " ms");

        // ⚠️ Cache Invalidation
        int cacheSizeBefore = cache.size();

        cache.clear();

        System.out.println("⚠️ [CACHE INVALIDATED] Cleared " + cacheSizeBefore + " entries");

        long end = System.currentTimeMillis();

        System.out.println("🏁 [CREATE EVENT COMPLETED] Total Time: " + (end - start) + " ms");

        return savedEvent;
    }
    // ----------------------------------------------------------------------------------------------------------------------------

   //implementing pagination
    public List<Event> getAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(pageable).getContent();
    }
}
