package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.project.hrbank.normalizer.SearchRequestNormalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SearchRequestNormalizerTest {

  @Test
  void clampSize_works_for_null_small_within_large() {
    assertThat(SearchRequestNormalizer.clampSize(null, 20, 5, 100)).isEqualTo(20); // null -> def
    assertThat(SearchRequestNormalizer.clampSize(3, 20, 5, 100)).isEqualTo(5);     // < min -> min
    assertThat(SearchRequestNormalizer.clampSize(50, 20, 5, 100)).isEqualTo(50);   // within -> keep
    assertThat(SearchRequestNormalizer.clampSize(999, 20, 5, 100)).isEqualTo(100); // > max -> max
  }

  @Test
  void normalizeString_trims_and_lowercases_and_whitelists() {
    Set<String> allowed = Set.of("name", "hiredate");
    assertThat(SearchRequestNormalizer.normalizeString(null, allowed, "name")).isEqualTo("name");
    assertThat(SearchRequestNormalizer.normalizeString("  HiReDaTe  ", allowed, "name"))
        .isEqualTo("hiredate");
    assertThat(SearchRequestNormalizer.normalizeString("email", allowed, "name"))
        .isEqualTo("name"); // not allowed -> default
  }

  @Test
  void normalizeSortField_does_not_trim_or_lowercase_just_whitelist() {
    Set<String> allowed = Set.of("name", "employeeNumber");
    assertThat(SearchRequestNormalizer.normalizeSortField(null, allowed, "name"))
        .isEqualTo("name");
    assertThat(SearchRequestNormalizer.normalizeSortField("employeeNumber", allowed, "name"))
        .isEqualTo("employeeNumber");
    assertThat(SearchRequestNormalizer.normalizeSortField("EMPLOYEENUMBER", allowed, "name"))
        .isEqualTo("name"); // not in allowed -> default
  }

  @Test
  void normalizeSortDirection_allows_only_asc_desc_to_lowercase() {
    assertThat(SearchRequestNormalizer.normalizeSortDirection(null, "asc")).isEqualTo("asc");
    assertThat(SearchRequestNormalizer.normalizeSortDirection("ASC", "desc")).isEqualTo("asc");
    assertThat(SearchRequestNormalizer.normalizeSortDirection("desc", "asc")).isEqualTo("desc");
    assertThat(SearchRequestNormalizer.normalizeSortDirection("down", "asc")).isEqualTo("asc");
  }

  @Test
  void defaultDates_work() {
    LocalDate d = LocalDate.of(2024, 2, 10);
    LocalDate def = LocalDate.of(1970, 1, 1);
    assertThat(SearchRequestNormalizer.defaultLocalDate(null, def)).isEqualTo(def);
    assertThat(SearchRequestNormalizer.defaultLocalDate(d, def)).isEqualTo(d);

    Instant now = Instant.now();
    Instant epoch = Instant.EPOCH;
    assertThat(SearchRequestNormalizer.defaultInstant(null, epoch)).isEqualTo(epoch);
    assertThat(SearchRequestNormalizer.defaultInstant(now, epoch)).isEqualTo(now);
  }

  @Test
  void alignToUnitEnd_day_week_month_quarter_year_default() {
    LocalDate d = LocalDate.of(2024, 2, 15); // Thu
    assertThat(SearchRequestNormalizer.alignToUnitEnd(d, "day")).isEqualTo(d);
    assertThat(SearchRequestNormalizer.alignToUnitEnd(d, "week"))
        .isEqualTo(LocalDate.of(2024, 2, 18)); // that week's Sunday (ISO day 7)
    assertThat(SearchRequestNormalizer.alignToUnitEnd(LocalDate.of(2024, 2, 10), "month"))
        .isEqualTo(LocalDate.of(2024, 2, 29)); // leap year
    assertThat(SearchRequestNormalizer.alignToUnitEnd(LocalDate.of(2024, 5, 1), "quarter"))
        .isEqualTo(LocalDate.of(2024, 6, 30));
    assertThat(SearchRequestNormalizer.alignToUnitEnd(LocalDate.of(2024, 5, 1), "year"))
        .isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(SearchRequestNormalizer.alignToUnitEnd(d, "foo")).isEqualTo(d);
  }

  @Test
  void subtractUnits_handles_all_units_and_default() {
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 2, 20), "day", 3))
        .isEqualTo(LocalDate.of(2024, 2, 17));
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 2, 18), "week", 2))
        .isEqualTo(LocalDate.of(2024, 2, 4));
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 3, 31), "month", 1))
        .isEqualTo(LocalDate.of(2024, 2, 29));
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 6, 30), "quarter", 1))
        .isEqualTo(LocalDate.of(2024, 3, 31));
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 12, 31), "year", 1))
        .isEqualTo(LocalDate.of(2023, 12, 31));
    // default -> minusMonths(n) then last day
    assertThat(SearchRequestNormalizer.subtractUnits(LocalDate.of(2024, 3, 31), "foo", 2))
        .isEqualTo(LocalDate.of(2024, 1, 31));
  }

  @Test
  void nextPeriodEnd_handles_all_units_and_default() {
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 1, 31), "day"))
        .isEqualTo(LocalDate.of(2024, 2, 1));
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 2, 18), "week"))
        .isEqualTo(LocalDate.of(2024, 2, 25));
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 1, 31), "month"))
        .isEqualTo(LocalDate.of(2024, 2, 29));
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 3, 31), "quarter"))
        .isEqualTo(LocalDate.of(2024, 6, 30));
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 12, 31), "year"))
        .isEqualTo(LocalDate.of(2025, 12, 31));
    // default -> plusMonths(1) then last day
    assertThat(SearchRequestNormalizer.nextPeriodEnd(LocalDate.of(2024, 3, 31), "foo"))
        .isEqualTo(LocalDate.of(2024, 4, 30));
  }
}