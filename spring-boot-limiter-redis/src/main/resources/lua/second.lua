local current;
current = redis.call('incr',KEYS[1]);
if tonumber(current) == 1 then 
   redis.call('expire',KEYS[1],ARGV[1]); 
   return 1;
else
   if tonumber(current) <= tonumber(ARGV[2]) then
  	 return 1;
   else
	 return -1;
   end
end