package com.sprint.project.hrbank.normalizer;

import java.time.Instant;
import java.time.LocalDate;
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
    String v = raw.toLowerCase();
    return allowed.contains(v) ? v : def;
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

  public static LocalDate calculateFromUnit(LocalDate to, String unit, int periods) {

    return switch (unit) {
      case "day" -> to.minusDays(periods);
      case "week" -> to.minusWeeks(periods);
      case "month" -> to.minusMonths(periods);
      case "quarter" -> to.minusMonths(periods * 3L);
      case "year" -> to.minusYears(periods);
      default -> to.minusMonths(periods);
    };
  }

}
