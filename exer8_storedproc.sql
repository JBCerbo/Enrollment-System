CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_checkEnrollConflict`(
	enrollStudentID int,
	enrollSubjectID int
)
BEGIN
	DECLARE isSubjectEnrolledToStudent BIT DEFAULT false;
    
	SELECT COUNT(*) > 0 
    INTO isSubjectEnrolledToStudent
	FROM enroll
	WHERE studentID = enrollStudentID AND subjectID = enrollSubjectID;
	
    IF isSubjectEnrolledToStudent
    THEN
		SELECT -1 AS Result, enrollSubjectID AS Conflict_Subject_ID;
    ELSE
		BEGIN
			DECLARE conflictSubjectID INT;
			DECLARE hasConflictTime BIT DEFAULT false;
			DECLARE hasConflictDay BIT DEFAULT false;
			
			DECLARE toEnrollSchedStart TIME;
			DECLARE toEnrollSchedEnd TIME;
			DECLARE toEnrollSchedSun BIT;
			DECLARE toEnrollSchedMon BIT;
			DECLARE toEnrollSchedTue BIT;
			DECLARE toEnrollSchedWed BIT;
			DECLARE toEnrollSchedThu BIT;
			DECLARE toEnrollSchedFri BIT;
			DECLARE toEnrollSchedSat BIT;

			DECLARE enrolledSubjID INT;
			DECLARE enrolledSchedStart TIME;
			DECLARE enrolledSchedEnd TIME;
			DECLARE enrolledSchedSun BIT;
			DECLARE enrolledSchedMon BIT;
			DECLARE enrolledSchedTue BIT;
			DECLARE enrolledSchedWed BIT;
			DECLARE enrolledSchedThu BIT;
			DECLARE enrolledSchedFri BIT;
			DECLARE enrolledSchedSat BIT;
            
			DECLARE done INT DEFAULT FALSE;
			DECLARE enrolledSchedListCursor CURSOR FOR
				SELECT
					subjectID,
					schedStart,
					schedEnd,
					INSTR(schedDays, 'S') > 0 AS schedSun,
					INSTR(schedDays, 'M') > 0 AS schedMon,
					INSTR(schedDays, 'T') > 0 AS schedTue,
					INSTR(schedDays, 'W') > 0 AS schedWed,
					INSTR(schedDays, 'H') > 0 AS schedThu,
					INSTR(schedDays, 'F') > 0 AS schedFri,
					INSTR(schedDays, 'A') > 0 AS schedSat
				FROM
				(
					SELECT
						subjects.*,
						SUBSTRING_INDEX(subjectSchedule, ' ', 1) AS schedDays,
						CAST(INSERT(SUBSTRING_INDEX(SUBSTRING_INDEX(subjectSchedule, ' ', -1), '-', 1), 3, 0, ':') AS TIME) AS schedStart,
						CAST(INSERT(SUBSTRING_INDEX(SUBSTRING_INDEX(subjectSchedule, ' ', -1), '-', -1), 3, 0, ':') AS TIME) AS schedEnd
					FROM subjects
					LEFT JOIN enroll ON enroll.subjectID = subjects.subjectID
					WHERE enroll.studentID = enrollStudentID
				) AS enrolled_subjects_table;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
				
			SELECT
				schedStart,
				schedEnd,
				INSTR(schedDays, 'S') > 0 AS schedSun,
				INSTR(schedDays, 'M') > 0 AS schedMon,
				INSTR(schedDays, 'T') > 0 AS schedTue,
				INSTR(schedDays, 'W') > 0 AS schedWed,
				INSTR(schedDays, 'H') > 0 AS schedThu,
				INSTR(schedDays, 'F') > 0 AS schedFri,
				INSTR(schedDays, 'A') > 0 AS schedSat
			INTO
				toEnrollSchedStart,
				toEnrollSchedEnd,
				toEnrollSchedSun,
				toEnrollSchedMon,
				toEnrollSchedTue,
				toEnrollSchedWed,
				toEnrollSchedThu,
				toEnrollSchedFri,
				toEnrollSchedSat
			FROM
			(
				SELECT
					*,
					SUBSTRING_INDEX(subjectSchedule, ' ', 1) AS schedDays,
					CAST(INSERT(SUBSTRING_INDEX(SUBSTRING_INDEX(subjectSchedule, ' ', -1), '-', 1), 3, 0, ':') AS TIME) AS schedStart,
					CAST(INSERT(SUBSTRING_INDEX(SUBSTRING_INDEX(subjectSchedule, ' ', -1), '-', -1), 3, 0, ':') AS TIME) AS schedEnd
				FROM
					subjects
			) AS subj_tbl
			WHERE subjectID = enrollSubjectID;
            
            OPEN enrolledSchedListCursor;
            enrolledSchedListCursorLoop: LOOP
				FETCH
					enrolledSchedListCursor 
				INTO
					enrolledSubjID,
					enrolledSchedStart,
					enrolledSchedEnd,
					enrolledSchedSun,
					enrolledSchedMon,
					enrolledSchedTue,
					enrolledSchedWed,
					enrolledSchedThu,
					enrolledSchedFri,
					enrolledSchedSat;
				IF done
				THEN
					LEAVE enrolledSchedListCursorLoop;
                END IF;
                SET hasConflictTime = false;
                SET hasConflictDay = false;
				SET hasConflictTime = hasConflictTime OR (toEnrollSchedStart >= enrolledSchedStart AND toEnrollSchedStart <= enrolledSchedEnd);
				SET hasConflictTime = hasConflictTime OR (toEnrollSchedEnd >= enrolledSchedStart AND toEnrollSchedEnd <= enrolledSchedEnd);
                
                SET hasConflictTime = hasConflictTime OR (enrolledSchedEnd < toEnrollSchedStart AND CAST((toEnrollSchedStart - enrolledSchedEnd) AS TIME) < '00:05:00');
                SET hasConflictTime = hasConflictTime OR (toEnrollSchedEnd < enrolledSchedStart AND CAST((enrolledSchedStart - toEnrollSchedEnd) AS TIME) < '00:05:00');
				
                SET hasConflictDay = hasConflictDay OR (enrolledSchedSun AND toEnrollSchedSun = enrolledSchedSun);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedMon AND toEnrollSchedMon = enrolledSchedMon);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedTue AND toEnrollSchedTue = enrolledSchedTue);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedWed AND toEnrollSchedWed = enrolledSchedWed);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedThu AND toEnrollSchedThu = enrolledSchedThu);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedFri AND toEnrollSchedFri = enrolledSchedFri);
				SET hasConflictDay = hasConflictDay OR (enrolledSchedSat AND toEnrollSchedSat = enrolledSchedSat);
                IF (hasConflictTime AND hasConflictDay)
                THEN
					SET conflictSubjectID = enrolledSubjID;
					LEAVE enrolledSchedListCursorLoop;
                END IF;
			END LOOP enrolledSchedListCursorLoop;
			CLOSE enrolledSchedListCursor;
            SELECT CASE WHEN (hasConflictTime AND hasConflictDay) THEN 1 ELSE 0 END AS Result, conflictSubjectID AS Conflict_Subject_ID;
        END;
    END IF;
END