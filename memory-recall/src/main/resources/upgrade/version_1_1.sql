update test.real_object set parent_tags_number=10;

update test.picture set creation_hour=case when minute(creation_time) >= 30 then (hour(creation_time) + 8.5) else (hour(creation_time) + 8);