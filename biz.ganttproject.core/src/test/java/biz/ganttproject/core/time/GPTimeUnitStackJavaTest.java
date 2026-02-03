package biz.ganttproject.core.time;

import biz.ganttproject.core.time.impl.GPTimeUnitStack;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GPTimeUnitStackJavaTest {

  // Helper to transform LocalDate to Date
  public static Date toDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  @Test
  public void testBackward() {
    Date start = toDate(LocalDate.of(2026, 2, 5));
    Date end = toDate(LocalDate.of(2026, 2, 2));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(-3, duration);
  }

  @Test
  public void testSameDay() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2026, 2, 2));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(0, duration);
  }

  @Test
  public void testForwardLessThanWeek() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2026, 2, 5));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(3, duration);
  }

  @Test
  public void testForwardLessThanMonth() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2026, 2, 22));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(20, duration);
  }

  @Test
  public void testForwardLessThanYear() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2026, 5, 2));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(88, duration);
  }

  @Test
  public void testForwardMoreThanYear() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2027, 5, 2));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(453, duration);
  }

  @Test
  public void testForwardVeryLong() {
    Date start = toDate(LocalDate.of(2026, 2, 2));
    Date end = toDate(LocalDate.of(2126, 2, 2));
    int duration = GPTimeUnitStack.DAY.duration(start, end).getLength();
    assertEquals(36524, duration);
  }

}