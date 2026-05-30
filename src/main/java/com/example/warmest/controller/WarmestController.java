package com.example.warmest.controller;

import com.example.warmest.api.WarmestDataStructureInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warmest")
public class WarmestController {
    private final WarmestDataStructureInterface warmestService;

    public WarmestController(WarmestDataStructureInterface warmestService) {
        this.warmestService = warmestService;
    }


    @PostMapping("/keys/{key}")
    public ResponseEntity<Integer> put(@PathVariable String key, @RequestParam int value) {
        Integer previous = warmestService.put(key, value);
        return ResponseEntity.ok(previous);
    }

    @GetMapping("/keys/{key}")
    public ResponseEntity<Integer> get(@PathVariable String key) {
        Integer previous = warmestService.get(key);
        if (previous == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(previous);
    }

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<Integer> remove(@PathVariable String key) {
        Integer previous = warmestService.remove(key);
        if (previous == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(previous);
    }

    @GetMapping("/warmest")
    public ResponseEntity<String> getWarmest() {
        String warmestKey = warmestService.getWarmest();
        if (warmestKey == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(warmestKey);
    }
}
