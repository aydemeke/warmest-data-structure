-- =====================================================================
-- remove.lua  --  Atomic O(1) remove
-- =====================================================================
--
-- Removes `key` from the hash AND unlinks it from the doubly-linked
-- list, updating head / tail if needed.
--
-- KEYS / ARGV layout: see put.lua
-- ARGV[1] = key
--
-- Returns: previous value, or nil if no mapping.
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

redis.call('HDEL', valuesK, key)

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
        redis.call('SET',  headK, n)
    end
end

-- If the removed node was both head and tail (only element), wipe both
if not p and not n then
    redis.call('DEL', headK)
    redis.call('DEL', tailK)
end

redis.call('HDEL', nextK, key)
redis.call('HDEL', prevK, key)

return value