-- Report existing service durations before enforcing application validation.
SELECT service_id, name, duration_minutes
FROM services
WHERE duration_minutes IS NULL
   OR duration_minutes <= 0
   OR MOD(duration_minutes, 15) <> 0;

ALTER TABLE appointments ADD COLUMN duration_minutes INT NULL;

-- Historical appointments remain NULL because their effective duration is unknown.
-- New and edited appointments must persist a valid multiple of 15 minutes.
