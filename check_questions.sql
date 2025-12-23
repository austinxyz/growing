SELECT
    q.id,
    q.title,
    q.focus_area_id,
    fa.name as focus_area_name
FROM questions q
LEFT JOIN focus_areas fa ON q.focus_area_id = fa.id;
