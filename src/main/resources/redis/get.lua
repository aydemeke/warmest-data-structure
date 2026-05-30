-- =====================================================================
-- get.lua  --  Atomic O(1) get + promote-to-head
-- =====================================================================
--
-- Returns the current value of `key` and, if it exists, moves it to the
-- head of the doubly-linked list so it becomes the new warmest key.
--
-- KEYS / ARGV layout: see put.lua
-- ARGV[1] = key       (no value argument for get)
--
-- Returns: current value, or nil if no mapping.
-- =====================================================================

local valuesK = KEYS[1]
local nextK   = KEYS[2]
local prevK   = KEYS[3]
local headK   = KEYS[4]
local tailK   = KEYS[5]

local key = ARGV[1]

local value = redis.call('HGET', valuesK, key)
if not value then
    return nil
end

-- Unlink current position
local p = redis.call('HGET', prevK, key)
local n = redis.call('HGET', nextK, key)

if p then
    if n then
        redis.call('HSET', nextK, p, n)
    else
        redis.call('HDEL', nextK, p)
        redis.call('SET',  tailK, p)
    end
end

if n then
    if p then
        redis.call('HSET', prevK, n, p)
    else
        redis.call('HDEL', prevK, n)
        redis.call('SET',  headK, n)  -- (will be overwritten below)
    end
end

redis.call('HDEL', nextK, key)
redis.call('HDEL', prevK, key)

-- If key was already the head, no movement needed beyond re-stitching above
local currentHead = redis.call('GET', headK)
if currentHead == key then
    -- nothing more to do
    return value
end

-- Link at head
local oldHead = redis.call('GET', headK)
if oldHead then
    redis.call('HSET', nextK, key,     oldHead)
    redis.call('HSET', prevK, oldHead, key)
else
    redis.call('SET',  tailK, key)
end
redis.call('SET', headK, key)

return value