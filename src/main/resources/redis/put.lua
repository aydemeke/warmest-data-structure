-- =====================================================================
-- put.lua  --  Atomic O(1) put for the WarmestDataStructure
-- =====================================================================
--
-- Redis data model (a doubly-linked list backed by hashes):
--
--   HASH    warmest:values  key -> value
--   HASH    warmest:next    key -> next-key   (forward DLL pointers)
--   HASH    warmest:prev    key -> prev-key   (backward DLL pointers)
--   STRING  warmest:head    current warmest key (HEAD of the DLL)
--   STRING  warmest:tail    current coldest key (TAIL of the DLL)
--
-- All Redis primitives used (HGET, HSET, HDEL, GET, SET, DEL) are O(1).
-- Running the whole sequence inside a single Lua script guarantees that:
--   1. It is atomic – no other Redis client can interleave between steps.
--   2. The hash and the DLL pointers stay consistent across all instances.
--
-- KEYS[1] = "warmest:values"
-- KEYS[2] = "warmest:next"
-- KEYS[3] = "warmest:prev"
-- KEYS[4] = "warmest:head"
-- KEYS[5] = "warmest:tail"
-- ARGV[1] = key
-- ARGV[2] = value (as string)
--
-- Returns: previous value associated with key, or nil.
-- =====================================================================

local valuesK = KEYS[1]
local nextK   = KEYS[2]
local prevK   = KEYS[3]
local headK   = KEYS[4]
local tailK   = KEYS[5]

local key   = ARGV[1]
local value = ARGV[2]

local previous = redis.call('HGET', valuesK, key)

-- Always update the value
redis.call('HSET', valuesK, key, value)

-- If key was already in the list, unlink it before re-linking at head
if previous then
    local p = redis.call('HGET', prevK, key)
    local n = redis.call('HGET', nextK, key)

    -- Re-stitch prev's next pointer
    if p then
        if n then
            redis.call('HSET', nextK, p, n)
        else
            redis.call('HDEL', nextK, p)
            redis.call('SET',  tailK, p)  -- prev becomes new tail
        end
    end

    -- Re-stitch next's prev pointer
    if n then
        if p then
            redis.call('HSET', prevK, n, p)
        else
            redis.call('HDEL', prevK, n)
            redis.call('SET',  headK, n)  -- next becomes new head (will be overwritten below)
        end
    end

    -- Edge: removed node was both head AND tail (only element)
    if not p and not n then
        redis.call('DEL', headK)
        redis.call('DEL', tailK)
    end

    redis.call('HDEL', nextK, key)
    redis.call('HDEL', prevK, key)
end

-- Link the (possibly fresh) node at the head
local oldHead = redis.call('GET', headK)
if oldHead then
    redis.call('HSET', nextK, key,     oldHead)
    redis.call('HSET', prevK, oldHead, key)
    redis.call('HDEL', prevK, key)   -- new head has no prev
else
    -- List was empty: this key becomes both head and tail
    redis.call('SET',  tailK, key)
    redis.call('HDEL', nextK, key)
    redis.call('HDEL', prevK, key)
end
redis.call('SET', headK, key)

return previous