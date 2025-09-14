package com.sprint.project.hrbank.normalizer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Set;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SearchRequestNormalizer {

  // size가 null이거나 작으면 기본값, 크면 max로 클램프
  public static int clampSize(Integer size, int def, int min, int max) {
    if (size == null) {
      return def;
    }
    if (size < min) {
      return min;
    }
    return Math.min(size, max);
  }

  // String 화이트리스트 검사: 미허용이면 기본값 반환
  public static String normalizeString(String raw, Set<String> allowed, String def) {
    if (raw == null || raw.isBlank()) {
      return def;
    }
    String v = raw.trim().toLowerCase(Locale.ROOT);
    return allowed.contains(v) ? v : def;
  }

  // sortField 화이트리스트 검사: String과 달리 trim, toLowerCase 생략
  public static String normalizeSortField(String raw, Set<String> allowed, String def) {
    if (raw == null) {
      return def;
    }
    return allowed.contains(raw) ? raw : def;
  }

  // sortDirection: asc/desc만 허용, 소문자로 표준화
  public static String normalizeSortDirection(String raw, String def) {
    if (raw == null) {
      return def;
    }
    String v = raw.toLowerCase(Locale.ROOT);
    return (v.equals("asc") || v.equals("desc")) ? v : def;
  }

  // LocalDate 기본값 설정
  public static LocalDate defaultLocalDate(LocalDate raw, LocalDate def) {
    return (raw == null) ? def : raw;
  }

  // Instant 기본값 설정
  public static Instant defaultInstant(Instant raw, Instant def) {
    return (raw == null) ? def : raw;
  }

  /**
   * 단위의 '끝'에 정렬 (day=그날, week=ISO 일요일, month=말일, quarter=분기말, year=연말)
   */
  public static LocalDate alignToUnitEnd(LocalDate date, String unit) {
    return switch (unit) {
      case "day" -> date;
      case "week" -> date.with(WeekFields.ISO.dayOfWeek(), 7); // ISO: 월(1)~일(7)
      case "month" -> date.with(TemporalAdjusters.lastDayOfMonth());
      case "quarter" -> {
        int q = ((date.getMonthValue() - 1) / 3) + 1;
        int lastMonthOfQ = q * 3;
        yield LocalDate.of(date.getYear(), lastMonthOfQ, 1)
            .with(TemporalAdjusters.lastDayOfMonth());
      }
      case "year" -> date.with(TemporalAdjusters.lastDayOfYear());
      default -> date;
    };
  }

  /**
   * 버킷 개수 계산 시 역방향 이동(단위 끝 기준) - 11이면 총 12버킷
   */
  public static LocalDate subtractUnits(LocalDate unitEnd, String unit, int n) {
    return switch (unit) {
      case "day" -> unitEnd.minusDays(n);
      case "week" -> unitEnd.minusWeeks(n);
      case "month" -> unitEnd.minusMonths(n).with(TemporalAdjusters.lastDayOfMonth());
      case "quarter" -> alignToUnitEnd(unitEnd.minusMonths(3L * n), "quarter");
      case "year" -> unitEnd.minusYears(n).with(TemporalAdjusters.lastDayOfYear());
      default -> unitEnd.minusMonths(n).with(TemporalAdjusters.lastDayOfMonth());
    };
  }

  public static LocalDate nextPeriodEnd(LocalDate end, String unit) {
    return switch (unit) {
      case "day" -> end.plusDays(1);
      case "week" -> end.plusWeeks(1);
      case "month" -> end.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
      case "quarter" -> alignToUnitEnd(end.plusMonths(3), "quarter");
      case "year" -> end.plusYears(1).with(TemporalAdjusters.lastDayOfYear());
      default -> end.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    };
  }

}
