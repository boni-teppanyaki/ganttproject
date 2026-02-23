import biz.ganttproject.core.calendar.*;
import biz.ganttproject.core.time.CalendarFactory;
import biz.ganttproject.core.time.GanttCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.text.DateFormat;
import java.util.*;

public class CoverageOrientedTest {

  // Calendar constructors fail without a proper locale initialization
  @BeforeEach
  void initLocaleApi() {
    new CalendarFactory() {
      {
        setLocaleApi(new CalendarFactory.LocaleApi() {
          @Override
          public Locale getLocale() {
            return Locale.US;
          }
          @Override
          public DateFormat getShortDateFormat() {
            return DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
          }
        });
      }
    };
  }

  @Test
  void testConstructorsAndGetters() {

    Calendar cal = Calendar.getInstance();
    cal.set(2026, Calendar.FEBRUARY, 15);
    Date start = cal.getTime();
    cal.set(2026, Calendar.FEBRUARY, 20);
    Date finish = cal.getTime();

    GanttDaysOff daysOff1 = new GanttDaysOff(start, finish);
    assertEquals(start, daysOff1.getStart().getTime());
    assertEquals(finish, daysOff1.getFinish().getTime());

    GanttCalendar startCal = daysOff1.getStart();
    GanttCalendar finishCal = daysOff1.getFinish();
    GanttDaysOff daysOff2 = new GanttDaysOff(startCal, finishCal);
    assertEquals(startCal, daysOff2.getStart());
    assertEquals(finishCal, daysOff2.getFinish());
  }

  @Test
  void testToStringAndEquals() {
    Date start = new Date(2026, 1, 20);
    Date finish = new Date(2026, 1, 20);
    GanttDaysOff daysOff = new GanttDaysOff(start, finish);

    GanttDaysOff copy = GanttDaysOff.create(daysOff);
    assertTrue(daysOff.equals(copy));
  }

  @Test
  void testIsADayOff() {
    Date start = new Date(2026, 1, 15);
    Date finish = new Date(2026, 1, 20);
    GanttDaysOff daysOff = new GanttDaysOff(start, finish);

    // dates inside range
    Date mid = new Date(2026, 1, 17);
    assertTrue(daysOff.isADayOff(mid));

    // date outside range
    Date before = new Date(2026, 1, 14);
    Date after = new Date(2026, 1, 21);
    assertFalse(daysOff.isADayOff(before));
    assertFalse(daysOff.isADayOff(after));
  }

  @Test
  void testIsADayOffInWeek() {
    Date start = new Date(2026, 1, 15);
    Date finish = new Date(2026, 1, 20);
    GanttDaysOff daysOff = new GanttDaysOff(start, finish);

    Calendar cal = Calendar.getInstance();
    cal.set(2026, Calendar.FEBRUARY, 10);
    assertEquals(-1, daysOff.isADayOffInWeek(cal.getTime()));
  }

  @Test
  void testCreateCopies() {
    Date start = new Date(2026, 1, 15);
    Date finish = new Date(2026, 1, 20);
    GanttDaysOff daysOff = new GanttDaysOff(start, finish);

    GanttDaysOff copy = GanttDaysOff.create(daysOff);
    assertNotSame(daysOff, copy);
    assertEquals(daysOff.getStart(), copy.getStart());
    assertEquals(daysOff.getFinish(), copy.getFinish());
  }

  @Test
  void testPublicHolidays() {
    WeekendCalendarImpl cal = new WeekendCalendarImpl();
    CalendarEvent event1 = CalendarEvent.newEvent(new Date(), true, CalendarEvent.Type.HOLIDAY, "Holiday1", null);
    CalendarEvent event2 = CalendarEvent.newEvent(new Date(), false, CalendarEvent.Type.WORKING_DAY, "Workday1", null);
    Set<CalendarEvent> holidays = new HashSet<>();
    holidays.add(event1);
    holidays.add(event2);

    cal.setPublicHolidays(holidays);
    Collection<CalendarEvent> result = cal.getPublicHolidays();
    assertTrue(result.contains(event1));
    assertTrue(result.contains(event2));
  }

  @Test
  void testBaseCalendarID() {
    WeekendCalendarImpl cal = new WeekendCalendarImpl();
    cal.setBaseCalendarID("testID");
    assertEquals("testID", cal.getBaseCalendarID());
  }

  @Test
  void testIsWeekend() {
    WeekendCalendarImpl cal = new WeekendCalendarImpl();
    cal.setOnlyShowWeekends(false);
    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    Date sunday = c.getTime();

    cal.setWeekDayType(Calendar.SUNDAY, GPCalendar.DayType.WEEKEND);
    assertTrue(cal.isWeekend(sunday));

    cal.setOnlyShowWeekends(true);
    assertFalse(cal.isWeekend(sunday));
  }

  @Test
  void testFindClosestWorkingTime() {
    WeekendCalendarImpl cal = new WeekendCalendarImpl();
    cal.setOnlyShowWeekends(false);

    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
    Date saturday = c.getTime();

    // Assume Saturday is not working → should find a working day
    Date closest = cal.findClosestWorkingTime(saturday);
    assertNotNull(closest);
  }

  @Test
  void testCopyCalendar() {
    WeekendCalendarImpl cal = new WeekendCalendarImpl();
    cal.setBaseCalendarID("originalID");
    cal.setOnlyShowWeekends(false);

    // Add one holiday for copy() to copy
    CalendarEvent event = CalendarEvent.newEvent(new Date(), true, CalendarEvent.Type.HOLIDAY, "HolidayCopy", null);
    cal.setPublicHolidays(Set.of(event));

    WeekendCalendarImpl copy = (WeekendCalendarImpl) cal.copy();

    // Check that copy has same properties but is a new object
    assertNotSame(cal, copy);
    assertEquals(cal.getBaseCalendarID(), copy.getBaseCalendarID());
    assertEquals(cal.getPublicHolidays(), copy.getPublicHolidays());
    assertEquals(cal.getOnlyShowWeekends(), copy.getOnlyShowWeekends());
  }
}