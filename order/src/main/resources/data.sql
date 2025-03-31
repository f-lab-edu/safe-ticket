-- Insert Events
INSERT INTO event (name, description, duration_minutes, status, created_at, updated_at)
VALUES ('트와이스 콘서트', '10주년 콘서트 개최', 120, 'PUBLISHED', '2023-01-01 00:00:00', '2023-01-01 00:00:00');

INSERT INTO event (name, description, duration_minutes, status, created_at, updated_at)
VALUES ('BTS 콘서트', '신년 축하 콘서트', 100, 'PUBLISHED', '2023-01-01 00:00:00', '2023-01-01 00:00:00');


-- Insert Venues
INSERT INTO venue (name, state, city, town, capacity, created_at, updated_at)
VALUES ('올림픽공원홀', '송파구', '방이동', '88-2', 500, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

INSERT INTO venue (name, state, city, town, capacity, created_at, updated_at)
VALUES ('롯데콘서트홀', '송파구', '올림픽로', '롯데월드몰 8층', 300, '2023-01-01 00:00:00', '2023-01-01 00:00:00');


-- Insert Showtimes for Event 1
INSERT INTO showtime (start_time, end_time, event_id, venue_id, created_at, updated_at)
VALUES ('2025-03-03 12:00:00', '2025-03-03 14:00:00', 1, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

INSERT INTO showtime (start_time, end_time, event_id, venue_id, created_at, updated_at)
VALUES ('2025-03-03 15:00:00', '2025-03-03 17:00:00', 1, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

-- Insert Showtimes for Event 2
INSERT INTO showtime (start_time, end_time, event_id, venue_id, created_at, updated_at)
VALUES ('2025-03-04 12:00:00', '2025-03-04 14:00:00', 2, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

INSERT INTO showtime (start_time, end_time, event_id, venue_id, created_at, updated_at)
VALUES ('2025-03-04 15:00:00', '2025-03-04 17:00:00', 2, 2, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

------------------------------------------------------------------------------------------------------------------------------------

-- Insert Sections for Venue 2
INSERT INTO section (venue_id, name, capacity, created_at, updated_at)
VALUES (1, 'R석', 250, '2023-01-01 00:00:00', '2023-01-01 00:00:00');

INSERT INTO section (venue_id, name, capacity, created_at, updated_at)
VALUES (1, 'S석', 250, '2023-01-01 00:00:00', '2023-01-01 00:00:00');


-- Create Temporary Table for Seat Numbers
CREATE TEMPORARY TABLE temp_seat_numbers (num INT);

-- Insert 1 to 50 into Temporary Table
INSERT INTO temp_seat_numbers (num)
WITH RECURSIVE seat_numbers AS (
    SELECT 1 AS num
    UNION ALL
    SELECT num + 1 FROM seat_numbers WHERE num < 250
)
SELECT num FROM seat_numbers;

-- Insert Seats for Section 1 (R석)
INSERT INTO seat (section_id, seat_number, seat_row, seat_type, created_at, updated_at)
SELECT 1, CONCAT('R', num), num,'R', NOW(), NOW() FROM temp_seat_numbers;

-- Insert Seats for Section 2 (S석)
INSERT INTO seat (section_id, seat_number, seat_row, seat_type, created_at, updated_at)
SELECT 2, CONCAT('S', num), num, 'S', NOW(), NOW() FROM temp_seat_numbers;


-- Drop Temporary Table
DROP TEMPORARY TABLE temp_seat_numbers;

-- Insert Tickets
INSERT INTO ticket (showtime_id, seat_id, price, status, created_at, updated_at)
SELECT
    1 AS showtime_id,  -- showtime_id (fixed)
    s.seat_id AS seat_id,   -- seat_id (from seat table)
    100000 AS price,   -- price (fixed value)
    'AVAILABLE' AS status, -- status (initial value)
    NOW() AS created_at,  -- created_at (current time)
    NOW() AS updated_at   -- updated_at (current time)
FROM seat s;
