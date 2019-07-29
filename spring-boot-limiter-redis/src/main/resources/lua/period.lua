local currentSectionCount;
local previousSectionCount;
local totalCountInPeriod;
currentSectionCount = redis.call('zcount', KEYS[2], '-inf', '+inf');
previousSectionCount = redis.call('zcount', KEYS[1], ARGV[3], '+inf');
totalCountInPeriod = tonumber(currentSectionCount)+tonumber(previousSectionCount);
if totalCountInPeriod < tonumber(ARGV[5]) then 
	redis.call('zadd',KEYS[2],ARGV[1],ARGV[2]);
	if tonumber(currentSectionCount) == 0 then 
		redis.call('expire',KEYS[2],ARGV[4]); 
	end 
    return 1
else 
	return -1
end