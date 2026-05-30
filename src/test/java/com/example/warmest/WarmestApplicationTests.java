package com.example.warmest;

import com.example.warmest.service.WarmestDataStructureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the in-memory {@link WarmestDataStructureService}.
 *
 * The {@code warmestComplexScenario} test reproduces the example trace
 * from the exercise document line-by-line and asserts every expected
 * return value.
 */
class WarmestApplicationTests {

    private WarmestDataStructureService warmestService;

    @BeforeEach
    void setUp() {
        warmestService = new WarmestDataStructureService();
    }

    // ----- Scenario 1: empty state ----------------------------------------

    @Test
    void shouldReturnNullWhenWarmestEmpty() {
        assertNull(warmestService.getWarmest());
    }

    @Test
    void putShouldReturnNullFirstInsert() {
        assertNull(warmestService.put("a", 100));
    }

    @Test
    void warmestShouldBeAAfterFirstPut() {
        warmestService.put("a", 100);
        assertEquals("a", warmestService.getWarmest());
    }

    // ----- Scenario 2: updating an existing key ---------------------------

    @Test
    void putShouldReturnOldValueWhenKeyExists() {
        warmestService.put("a", 100);
        Integer oldValue = warmestService.put("a", 101);
        assertEquals(100, oldValue);
    }

    @Test
    void getShouldReturnUpdatedValue() {
        warmestService.put("a", 100);
        warmestService.put("a", 101);
        assertEquals(101, warmestService.get("a"));
    }

    @Test
    void warmestShouldStillBeAAfterUpdate() {
        warmestService.put("a", 100);
        warmestService.put("a", 101);
        assertEquals("a", warmestService.getWarmest());
    }

    @Test
    void getShouldMoveNodeToHead() {
        warmestService.put("a", 100);
        warmestService.put("b", 200);
        assertEquals("b", warmestService.getWarmest());

        warmestService.get("a");
        assertEquals("a", warmestService.getWarmest());
    }

    // ----- Scenario 3: removal flow ---------------------------------------

    @Test
    void removeShouldReturnValueAndThenNull() {
        warmestService.put("a", 100);
        assertEquals(100, warmestService.remove("a"));
        assertNull(warmestService.remove("a"));
    }

    @Test
    void warmestShouldBeNullAfterRemove() {
        warmestService.put("a", 100);
        warmestService.remove("a");
        assertNull(warmestService.getWarmest());
    }

    // ----- Scenario 4: full exercise example trace ------------------------

    @Test
    void warmestComplexScenario() {
        assertNull(warmestService.getWarmest());                              // → null
        assertNull(warmestService.put("a", 100));                             // → null
        assertEquals("a", warmestService.getWarmest());             // → a
        assertEquals(100, warmestService.put("a", 101));            // → 100
        assertEquals(101, warmestService.put("a", 101));            // → 101
        assertEquals(101, warmestService.get("a"));                 // → 101
        assertEquals("a", warmestService.getWarmest());             // → a
        assertEquals(101, warmestService.remove("a"));         // → 101
        assertNull(warmestService.remove("a"));                          // → null
        assertNull(warmestService.getWarmest());                              // → null

        assertNull(warmestService.put("a", 100));                             // → null
        assertNull(warmestService.put("b", 200));                             // → null
        assertNull(warmestService.put("c", 300));                             // → null
        assertEquals("c", warmestService.getWarmest());             // → c
        assertEquals(200, warmestService.remove("b"));         // → 200
        assertEquals("c", warmestService.getWarmest());             // → c
        assertEquals(300, warmestService.remove("c"));         // → 300
        assertEquals("a", warmestService.getWarmest());             // → a
        assertEquals(100, warmestService.remove("a"));         // → 100
        assertNull(warmestService.getWarmest());                             // → null
        assertNull(warmestService.remove("a"));                         // → null
    }
}