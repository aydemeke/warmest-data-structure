package com.example.warmest;

import com.example.warmest.service.WarmestDataStructureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarmestApplicationTests {

    private WarmestDataStructureService warmestService;

    @BeforeEach
    void setUp() {
        warmestService = new WarmestDataStructureService();
    }

    /*
    * Scenario 1 - Empty state
    * */
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

    /*
     * Scenario 2 - Update same key
     * */
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

    /*
    * Scenario 3 - Remove flow
    * */
    @Test
    void removeShouldReturnValueAndThenNull() {
        warmestService.put("a", 100);
        Integer firstRemoveValue = warmestService.remove("a");
        assertEquals(100, firstRemoveValue);

        Integer secondRemoveValue = warmestService.remove("a");
        assertNull(secondRemoveValue);
    }

    @Test
    void warmestShouldBeNullAfterRemove() {
        warmestService.put("a", 100);
        warmestService.remove("a");

        assertNull(warmestService.getWarmest());
    }

    /*
     * Scenario 4 - Multi key scenario
     * */

    @Test
    void warmestComplexScenario() {
        String warmest = warmestService.getWarmest();// 🡪 returns null
        assertNull(warmest);
        System.out.println("getWarmest -> " + warmest);

        Integer putA = warmestService.put("a", 100);// 🡪 returns null
        assertNull(putA);
        System.out.println("putA -> " + putA);

        warmest = warmestService.getWarmest();;// 🡪 returns a
        assertEquals("a", warmest);
        System.out.println("getWarmest -> " + warmest);

        putA = warmestService.put ("a", 101); //🡪 returns 100
        assertEquals(100, putA);
        System.out.println("putA -> " + putA);

        putA = warmestService.put ("a", 101); //🡪 returns 101
        assertEquals(101, putA);
        System.out.println("putA -> " + putA);

        Integer getA = warmestService.get("a"); //🡪 returns 101
        assertEquals(101, getA);
        System.out.println("getA -> " + getA);

        warmest = warmestService.getWarmest(); //🡪 returns a
        assertEquals("a", warmest);
        System.out.println("getWarmest -> " + warmest);

        Integer removeA = warmestService.remove("a"); //🡪 return 101
        assertEquals(101, removeA);
        System.out.println("removeA -> " + removeA);

        removeA = warmestService.remove("a"); //🡪 return null
        assertNull(removeA);
        System.out.println("removeA -> " + removeA);

        warmest = warmestService.getWarmest(); //🡪 returns null
        assertNull(warmest);
        System.out.println("getWarmest -> " + warmest);

        putA = warmestService.put ("a", 100); //🡪 returns null
        assertNull(putA);
        System.out.println("putA -> " + putA);

        Integer putB = warmestService.put ("b", 200); //🡪 returns null
        assertNull(putB);
        System.out.println("putB -> " + putB);

        Integer putC = warmestService.put ("c", 300); //🡪 returns null
        assertNull(putC);
        System.out.println("putB -> " + putC);

        warmest = warmestService.getWarmest(); //🡪 returns c
        assertEquals("c", warmest);
        System.out.println("getWarmest -> " + warmest);

        Integer removeB = warmestService.remove("b"); //🡪 return 200
        assertEquals(200, removeB);
        System.out.println("removeB -> " + removeB);

        warmest = warmestService.getWarmest(); //🡪 returns c
        assertEquals("c", warmest);
        System.out.println("getWarmest -> " + warmest);

        Integer removeC = warmestService.remove("c"); //🡪 return 300
        assertEquals(300, removeC);
        System.out.println("removeC -> " + removeC);

        warmest = warmestService.getWarmest(); //🡪 returns a
        assertEquals("a", warmest);
        System.out.println("getWarmest -> " + warmest);

        removeA = warmestService.remove("a"); //🡪 return 100
        assertEquals(100, removeA);
        System.out.println("removeA -> " + removeA);

        warmest = warmestService.getWarmest(); //🡪 returns null
        assertNull(warmest);
        System.out.println("getWarmest -> " + warmest);

        removeA = warmestService.remove("a");// 🡪 return null
        assertNull(removeA);
        System.out.println("removeA -> " + removeA);
    }







































//    @Test
//    void shouldUpdateWarmestAfterGet()

//    @Test
//    void shouldReturnPreviousValueOnPut()
}
