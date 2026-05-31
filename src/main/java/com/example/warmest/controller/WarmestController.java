package com.example.warmest.controller;

import com.example.warmest.core.WarmestDataStructureInterface;
import org.springframework.http.HttpStatus;
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

        return previous == null ?
                ResponseEntity.status(HttpStatus.CREATED).build() :
                ResponseEntity.ok(previous);
    }

    @GetMapping("/keys/{key}")
    public ResponseEntity<Integer> get(@PathVariable String key) {
        Integer value = warmestService.get(key);

        return value == null ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(value);
    }

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<Integer> remove(@PathVariable String key) {
        Integer previous = warmestService.remove(key);

        return previous == null ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(previous);
    }

    @GetMapping("/warmest")
    public ResponseEntity<String> getWarmest() {
        String warmestKey = warmestService.getWarmest();

        return warmestKey == null ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(warmestKey);
    }
}
